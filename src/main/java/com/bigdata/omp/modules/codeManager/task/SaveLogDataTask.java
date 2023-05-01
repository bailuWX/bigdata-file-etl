package com.bigdata.omp.modules.codeManager.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.bigdata.omp.config.SaveLogConfig;
import com.bigdata.omp.config.YamlPropertyResourceFactory;
import com.bigdata.omp.modules.codeManager.model.EtlLogKeyword;
import com.bigdata.omp.modules.codeManager.model.EtlLogKeywordMatch;
import com.bigdata.omp.modules.codeManager.model.RSlave;
import com.bigdata.omp.modules.codeManager.service.SaveLogDataService;
import com.bigdata.omp.util.BigMappedByteBufferReader;
import com.bigdata.omp.util.SFTPUtil;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource(value = "classpath:application.yml", encoding = "utf-8", factory = YamlPropertyResourceFactory.class)
@ConfigurationProperties(prefix = "scheduled-config")
public class SaveLogDataTask {
    public static final String SALT = "bigdata";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SaveLogDataService saveLogDataService;

    @Autowired
    private SaveLogConfig saveLogConfig;

    @Autowired
    private com.bigdata.omp.modules.codeManager.service.SelectRSlaveService SelectRSlaveService;

    //子服务ssh协议端口
    private static final int SFTP_PORT = 2345;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    @Scheduled(cron = "${scheduled-config.cron}")
    public void execute() {
        logger.info("开始执行saveLogDataTask");
        //使用阿里巴巴推荐的创建线程池的方式
        //通过ThreadPoolExecutor构造函数自定义参数创建
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
        /* 1.CallerRunsPolicy 不想丢弃任务，使用本身线程执行。除非线程池已经关闭了。 2.AbortPolicy 抛出异常RejectedExecutionException，丢弃任务
        3.DiscardPolicy 不抛异常，丢弃任务  4.DiscardOldestPolicy 丢弃最早的任务，重试执行该任务*/
                new ThreadPoolExecutor.CallerRunsPolicy());
        List<RSlave> rSlaves = SelectRSlaveService.selectRSlave();
        try {
            //遍历所有子服务的记录,逐条跟踪其ip、端口,文件大致的位置等,每条线程运行一个收集-转换-归档的任务
            for (RSlave rSlave : rSlaves) {
                executor.execute(() -> {
                    //每条线程运行一个收集-转换-归档的任务
                    executeTask(rSlave);
                });
            }
        } catch (Exception e) {
            logger.error("任务执行异常");
        } finally {
            //终止线程池
            executor.shutdown();
        }
        logger.info("Finished all threads");
        logger.info("本次[文件下载-->解析-->智能归档]任务成功执行!===========saveLogDataTask执行结束.");
    }

    /**
     * 每天凌晨1点执行一次：0 0 1 * * ?
     *
     * @param rSlave rSlave
     */
    private void executeTask(RSlave rSlave) {
        BigMappedByteBufferReader reader = null;
        String fileName = "";   //下载源文件名称
        String saveFileName = "";   //缓存文件的相对名称
        String saveFile = "";   //缓存到本地的文件的绝对路径
        try {
            //子服务主键id
            Long idSlave = rSlave.getIdSlave();
            //获取子服务主机ip
            String hostName = rSlave.getHostName();
            //子服务ssh登录用户名
            String hostUser = rSlave.getHostUser();
            //子服务ssh加密的密钥-需要解密
            String slaveEncryptHostPassword = rSlave.getHostPassword();

            //初始化解密机器（by org.jasypt）
            BasicTextEncryptor standardPBEStringEncryptor = new BasicTextEncryptor();
            standardPBEStringEncryptor.setPassword(SALT);

            //获取sftp
            SFTPUtil sftpUtilInstance = SFTPUtil.getSFTPUtilInstance(hostName, SFTP_PORT, hostUser, standardPBEStringEncryptor.decrypt(slaveEncryptHostPassword));

            //获取子服务启动占用端口号和文件的存放大致位置
            String port = rSlave.getPort();
            String hostFilePath = rSlave.getHostFilePath();

            //适配yml配置文件,自动定位到源文件名(或路径)
            try {
                fileName = getFileName(port);

                if (!fileName.startsWith("/")) {
                    fileName = "/".concat(fileName);
                }
            } catch (Exception e) {
                logger.error("找不到源文件名,或名称错误 " + fileName, e);
            }
            //生成uuid来区分不同的文件
            String simpleUUID = IdUtil.simpleUUID();

            //本地缓存文件的名称
            saveFileName = fileName.concat(".id-").concat(String.valueOf(idSlave))
                    .concat("-").concat(hostName).concat(".").concat(simpleUUID).
                    concat(".temp");

            //本地缓存文件的绝对路径
            saveFile = saveLogConfig.getSaveFileDirectory().concat(saveFileName);

            //连接成功并且有指定目录则下载文件并且解析文件,将错误日志归档数据库
            if (!sftpUtilInstance.listFiles(hostFilePath).isEmpty()) {
                //下载日志文件
                try {
                    sftpUtilInstance.download(hostFilePath, fileName, saveFile);
                } catch (Exception e) {
                    logger.error("下载错误，请检查配置文件!当前源文件路径: " + hostFilePath + fileName, e);
                    return;
                }

                //操作日志文件
                try {
                    File DestFile = new File(saveFile);

                    if (DestFile.exists()) {
                        reader = new BigMappedByteBufferReader(saveFile, 1024);
                        while (reader.read() != -1) {
                            byte[] bytes = reader.getCurrentBytes();
                            //获取log的每一行字符串
                            String log = new String(bytes);

                            //得到所有的匹配规则
                            List<EtlLogKeywordMatch> etlLogKeywordMatches = saveLogDataService.selectEtlLogKeywordMatch();

                            for (EtlLogKeywordMatch match : etlLogKeywordMatches) {
                                String keyWords = match.getKeyWords();
                                String keyDesc = match.getKeyDesc();


                                if (log.contains("[ETL PLAT]ERROR")) {
                                    EtlLogKeyword etlLogKeyword = new EtlLogKeyword();
                                    etlLogKeyword.setPort(port);
                                    etlLogKeyword.setHostName(hostName);

                                    if (log.contains(keyWords)) {
                                        etlLogKeyword.setKeyWords(keyWords);
                                        etlLogKeyword.setKeyDesc(keyDesc);
                                    } else {
                                        etlLogKeyword.setKeyWords("undefined-error");
                                        etlLogKeyword.setKeyDesc("未定义错误类型");
                                    }

                                    int start = log.indexOf("[ETL PLAT]ERROR");
                                    int end = Math.min(start + 200, log.length());
                                    etlLogKeyword.setErrorMsg(log.substring(start, end));

                                    //入库
                                    try {
                                        saveLogDataService.saveLogData(etlLogKeyword);
                                    } catch (Exception e) {
                                        logger.error("文件解析成功,但归档入库失败", e);
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.error("文件解析失败,temp路径错误或不存在,请检查: " + saveFile, e);
                }
            }
        } catch (Exception e) {
            logger.error("saveLogDataTask定时任务执行失败", e);
        } finally {
            try {//文件读取关闭io流
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("文件读取io流关闭异常,请检查错误日志!", e);
            }

            try {//每次完整的一次操作全部执行完毕,就清理!
                deleteLogFile(saveFile);
            } catch (Exception e) {
                logger.error("文件读取并解析成功,但清理缓存失败!", e);
            }

        }

    }

    /**
     * 适配yml配置文件,自动定位到源文件名(或路径)
     *
     * @param port port
     * @return String
     */
    private String getFileName(String port) {
        if (!StrUtil.isEmpty(port)) {

            String fileName;
            String path = saveLogConfig.getPath();

            String replaceTemp = path.replace("{port}", port);

            if (replaceTemp.contains("{")) {
                String dateFormat = replaceTemp.substring(replaceTemp.lastIndexOf("{") + 1, replaceTemp.lastIndexOf("}"));
                LocalDate yesterday = LocalDate.now().minusDays(1);
                String dateStr = yesterday.format(DateTimeFormatter.ofPattern(dateFormat));

                fileName = replaceTemp.replace("{".concat(dateFormat).concat("}"), dateStr);
                return fileName;
            }
            return replaceTemp;
        }
        return "";
    }

    /**
     * 删除该目录下的所有文件
     *
     * @param dir dir
     */
    private void deleteLogFile(String dir) {
        File file = new File(dir);
        if (FileUtil.isFile(file)) {

            FileUtil.del(file);
        }
    }
}
