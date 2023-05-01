package com.bigdata.omp.util;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SFTPHandler {
    private static final Logger log = LoggerFactory.getLogger(SFTPHandler.class);

    protected String charset = "UTF-8";
    protected String host;
    protected int port;
    protected String userName;
    protected String password;
    protected Channel channel = null;
    protected ChannelSftp sftp = null;
    protected Session sshSession = null;


    public boolean connect() {
        boolean flag = true;
        try {
            log.info("host:**********" + host);
            JSch jsch = new JSch();
            jsch.getSession(userName, host, port);
            sshSession = jsch.getSession(userName, host, port);
            sshSession.setPassword(password);
            sshSession.setConfig("StrictHostKeyChecking", "no");
            if (!sshSession.isConnected()) {
                sshSession.connect();
            }
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            log.error("连接错误", e);
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 连接ftp，连接失败抛出异常
     *
     * @return
     */
    public boolean connect2() throws JSchException {
        boolean flag = true;
        try {
            log.info("host:**********" + host);
            JSch jsch = new JSch();
            jsch.getSession(userName, host, port);
            sshSession = jsch.getSession(userName, host, port);
            sshSession.setPassword(password);
            sshSession.setConfig("StrictHostKeyChecking", "no");
            if (!sshSession.isConnected()) {
                sshSession.connect(30 * 1000);
            }
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            log.error("连接错误", e);
            throw e;
        }
        return flag;
    }

    /**
     * 执行相关的命令
     */
    public void execCmd() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        BufferedReader reader = null;

        try {
            if (connect()) {//建立服务器连接
                while ((command = br.readLine()) != null) {
                    channel = sshSession.openChannel("exec");
                    ((ChannelExec) channel).setCommand(command);
                    channel.setInputStream(null);
                    ((ChannelExec) channel).setErrStream(System.err);

                    channel.connect();
                    InputStream in = channel.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
                    String buf = null;
                    while ((buf = reader.readLine()) != null) {
                        log.debug(buf);
                        //System.out.println(buf);
                    }
                }
            }
        } catch (IOException e) {
            log.error("执行错误", e);
        } catch (JSchException e) {
            log.error("执行错误", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("关闭流错误", e);
            }
            disconnect();
        }
    }

    /**
     * 上传文件
     *
     * @param directory      上传的服务器目录
     * @param inputStream    要上传的文件
     * @param targetFileName 目标文件名
     * @param
     */
    public void upload(String directory, InputStream inputStream, String targetFileName) {
        try {
            if (connect()) {
                sftp.cd(directory);
                sftp.put(inputStream, targetFileName);
                sftp.setFilenameEncoding("UTF-8");
                log.debug("成功上传文件至" + directory);
            }
        } catch (Exception e) {
            log.error("上传错误", e);
        } finally {
            disconnect();
        }
    }

    /**
     * 数据导入文件上传
     *
     * @throws IOException
     */

    public void uploadTxt(String directory, BufferedReader br, String targetFileName) throws IOException {

        try {
            if (connect()) {
                System.out.println("进来了  。。。。");
                System.out.println("targetFileName====" + targetFileName);
                System.out.println("directory=====" + directory);
                sftp.cd(directory);
                OutputStream out = sftp.put(targetFileName);
                //FileOutputStream fos = (FileOutputStream) sftp.put(targetFileName);;
                //OutputStreamWriter wr = new OutputStreamWriter(fos, "UTF-8");
                sftp.setFilenameEncoding("UTF-8");
                String string = "";
                while ((string = br.readLine()) != null) {
                    byte[] b = string.getBytes(StandardCharsets.UTF_8);
                    out.write(b);
                    out.flush();
                }

                out.close();
                System.out.println("结束。。。。");
            }
        } catch (SftpException e) {

            e.printStackTrace();
        }

    }

    /**
     * 上传文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件
     * @param
     */
    public void upload(String directory, String uploadFile) {
        try {
            if (connect()) {
                sftp.cd(directory);
                File file = new File(uploadFile);
                sftp.put(new FileInputStream(file), file.getName());
                log.debug("成功上传文件至" + directory);
            }
        } catch (Exception e) {
            log.error("上传错误", e);
        } finally {
            disconnect();
        }
    }

    /**
     * @param directory
     * @param fileName
     * @param is
     */
    public void upload(String directory, String fileName, InputStream is) throws SftpException {
        try {
            this.connect();
            sftp.cd(directory);
            sftp.put(is, fileName);
            log.info("file:{} is upload successful", fileName);
        } catch (SftpException e) {
            log.warn("directory is not exist");
            try {
                log.info(directory + " 开始创建");
                sftp.mkdir(directory);
            } catch (SftpException e2) {
                e2.printStackTrace();
                log.info(directory + " 创建失败");
            }
            log.info(directory + " 创建成功");
            sftp.cd(directory);
            sftp.put(is, fileName);
        } finally {
            disconnect();
        }
    }

    /**
     * 下载文件
     *
     * @param hostFilePath 下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     * @param
     */
    public void download(String hostFilePath, String downloadFile, String saveFile) throws Exception {
        FileOutputStream fieloutput = null;
        try {
            if (connect()) {
                sftp.cd(hostFilePath);


                //得到文件对象而不是目录
                String realFileName = getRealFileName(downloadFile);


                //得到文件上一级目录
                String realFilePath = getRealFilePath(downloadFile, realFileName);

                //文件最终根目录
                String finalFilePath = hostFilePath + realFilePath;

                if (!hostFilePath.equals(finalFilePath)) {
                    //进入日志目录
                    sftp.cd(finalFilePath);
                }

                log.info("本次下载Src绝对路径:" + finalFilePath + realFileName);
                log.info("本次下载Dest绝对路径:" + saveFile);


                //准备文件输出流
                File file = new File(saveFile);

                //如果localPath和LocalFile都不存在,那就直接创建
                fieloutput = FileUtils.openOutputStream(file);

                //下载文件
                sftp.get(finalFilePath + realFileName, fieloutput);
                log.debug("成功下载文件: 从" + hostFilePath + downloadFile + "下载至" + saveFile);
            }
        } finally {
            if (null != fieloutput) {
                try {
                    fieloutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            disconnect();
        }
    }


    /**
     * 得到middle  Path
     *
     * @param downloadFile downloadFile
     * @param realFileName realFileName
     * @return String
     */
    private String getRealFilePath(String downloadFile, String realFileName) {
        if (!StrUtil.isEmpty(downloadFile)) {
            return downloadFile.substring(0, downloadFile.indexOf(realFileName));
        }

        return "";
    }


    /**
     * 得到真实的文件名
     * sg:  "/.../nohup.8001.out" --> nohup.8001.out
     *
     * @param downloadFile downloadFile
     */
    private String getRealFileName(String downloadFile) {
        if (!StrUtil.isEmpty(downloadFile)) {
            String[] strings = downloadFile.split("/");
            return strings[strings.length - 1];
        }
        return "";
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param
     */
    public boolean delete(String directory, String deleteFile) {
        boolean flag = true;
        try {
            if (connect()) {
                sftp.cd(directory);
                sftp.rm(deleteFile);
                log.debug("成功删除文件" + deleteFile);
            } else {
                flag = false;
            }
        } catch (Exception e) {
            log.error("删除错误", e);
            flag = false;
        } finally {
            disconnect();
        }
        return flag;
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public byte[] downloadByByte(String directory, String downloadFile) {
        byte[] fileData = null;
        try {
            if (connect()) {
                sftp.cd(directory);
                InputStream is = sftp.get(downloadFile);
                fileData = IOUtils.toByteArray(is);
                log.debug("成功下载文件:" + directory + "/" + downloadFile);
            }
        } catch (Exception e) {
            log.error("下载错误", e);
        } finally {
            disconnect();
        }
        return fileData;
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @param
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory) {
        Vector v = new Vector();
        try {
            if (connect()) {
                v = sftp.ls(directory);
            }
        } catch (SftpException e) {
            log.error("目录查询错误", e);
        } finally {
            disconnect();
        }
        return v;
    }

    /**
     * 列出目录下的子目录
     *
     * @param directory 要列出的目录
     * @param
     * @return
     * @throws SftpException
     */
    public List<String> listDir(String directory) {
        Vector v = new Vector();
        List<String> dirList = new ArrayList<String>();
        try {
            if (connect()) {
                v = sftp.ls(directory);
                for (int i = 0; i < v.size(); i++) {
                    LsEntry entry = (LsEntry) v.get(i);
                    String fileName = entry.getFilename();
                    if (entry.getAttrs().isDir() && !fileName.startsWith(".")) {
                        dirList.add(fileName);
                    }
                }
            }
        } catch (SftpException e) {
            log.error("目录查询错误", e);
        } finally {
            disconnect();
        }
        return dirList;
    }

    /**
     * 关闭连接 server
     */
    public void disconnect() {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
                log.debug("channel连接关闭成功！" + sftp);
            } else if (channel.isClosed()) {
                log.info("channel 已经关闭,不需要重复关闭！" + sftp);
            }
        }
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
                log.debug("sftp连接关闭成功！" + sftp);
            } else if (sftp.isClosed()) {
                log.info("sftp 已经关闭,不需要重复关闭！" + sftp);
            }
        }
        if (sshSession != null) {
            if (sshSession.isConnected()) {
                sshSession.disconnect();
                log.debug("sshSession连接关闭成功！" + sftp);
            }
        }
    }

    public Map<String, String> isConnect() {
        Map<String, String> result = new HashMap<String, String>();
        try {
            if (connect()) {
                result.put("isSuccess", "1");
                result.put("message", "连接成功！");
            } else {
                result.put("isSuccess", "0");
                result.put("message", "连接失败！");
            }
        } catch (Exception e) {
            result.put("isSuccess", "0");
            result.put("message", "连接失败！");
        } finally {
            disconnect();
        }
        return result;
    }

    public List<Map<String, Object>> listDownloadDir(String directory) {
        Vector v = new Vector();
        List<Map<String, Object>> dirList = new ArrayList<Map<String, Object>>();
        DecimalFormat df = new DecimalFormat("#.##");
        try {
            if (connect()) {
                v = sftp.ls(directory);
                for (int i = 0; i < v.size(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    LsEntry entry = (LsEntry) v.get(i);
                    String fileName = entry.getFilename();
                    if (!fileName.startsWith(".")) {
                        //dirList.add(fileName);
                        map.put("fileName", entry.getFilename());
                        Long space = entry.getAttrs().getSize();
                        if (space < 1024) {
                            map.put("file_size", space + "B");
                        } else if (space < 1024 * 1024) {
                            double spaced = space;
                            double spacekb = spaced / 1024;
                            map.put("file_size", df.format(spacekb) + "KB");
                        } else if (space < 1024 * 1024 * 1024) {
                            double spaced = space;
                            double spacemb = spaced / 1024 / 1024;
                            map.put("file_size", df.format(spacemb) + "MB");
                        } else {
                            double spaced = space;
                            double spacegb = spaced / 1024 / 1024 / 1024;
                            map.put("file_size", df.format(spacegb) + "GB");
                        }
                        map.put("last_modify_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(entry.getAttrs().getMtimeString())));
                        map.put("file_permission", entry.getAttrs().getPermissionsString());
                        map.put("isDir", entry.getAttrs().isDir());
                        dirList.add(map);
                    }

                }
            }
        } catch (Exception e) {
            log.error("目录查询错误", e);
        } finally {
            disconnect();
        }
        return dirList;
    }

    public boolean upload1(String directory, String fileName, InputStream is) {
        boolean flag = true;
        try {
            if (connect()) {
                sftp.cd(directory);
                sftp.put(is, fileName);
                log.info("成功上传文件至" + directory);
            }
        } catch (Exception e) {
            flag = false;
            log.error("上传错误", e);
        } finally {
            disconnect();
        }
        return flag;
    }

    /**
     * 连接ftp，上传文件到指定目录
     *
     * @param directory
     * @param fileName
     * @param is
     * @return
     */
    public boolean upload2(String directory, String fileName, InputStream is) throws SftpException, JSchException {
        boolean flag = true;
        try {
            if (connect2()) {
                sftp.cd(directory);
                sftp.put(is, fileName);
                log.info("成功上传文件至" + directory);
            }
        } catch (Exception e) {
            log.error("上传错误", e);
            throw e;
        } finally {
            disconnect();
        }
        return flag;
    }

    /**
     * 开发工厂获取结果集文件并封装成json数据
     *
     * @param instId
     * @param setDir 结果集文件所在服务器目录
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> getResultSetFiles(String instId, String setDir) throws IOException {
        log.info("=====instId:" + instId + " setDir:" + setDir);
        Vector v = new Vector();
        List<Map<String, Object>> resultSetList = new ArrayList<Map<String, Object>>();

        InputStream in = null;
        try {
            if (connect()) {
                v = sftp.ls(setDir);
                for (int i = 0; i < v.size(); i++) {
                    LsEntry entry = (LsEntry) v.get(i);
                    String fileName = entry.getFilename();
                    if (fileName.startsWith(instId + "_")) {
                        String filePath = setDir + fileName;
                        in = sftp.get(filePath);
                        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                        BufferedReader br = new BufferedReader(reader);
                        String tmpStr = "";
                        Map<String, Object> fileContentMap = new HashMap<String, Object>();
                        //List<String> columnNameList = new ArrayList<String>();
                        List<Object> rowList = new ArrayList<Object>();
                        boolean isGetColumn = false;
                        String[] cols = null;
                        while ((tmpStr = br.readLine()) != null) {
                            if (!isGetColumn) {
                                // 封装colModel,列名相关的信息
                                cols = tmpStr.split(",");

                                List<Map<String, Object>> colModel = new ArrayList<>();
                                for (String colName : cols) {
                                    Map<String, Object> colMap = new HashMap<>();
                                    colMap.put("name", colName);
                                    colMap.put("index", colName);
                                    colMap.put("label", colName);
                                    colMap.put("sortable", false);
                                    colModel.add(colMap);
                                }
                                fileContentMap.put("colModel", colModel);

                                //fileContentMap.put("columnName", strs);
                                isGetColumn = true;
                            } else {
                                // 封装每行的数据成key,value形式
                                String[] vals = tmpStr.split(",");

                                Map<String, Object> row = new HashMap<String, Object>();
                                for (int j = 0; j < vals.length; j++) {
                                    row.put(cols[j], vals[j]);
                                }

                                //row.put("cell", strs);
                                rowList.add(row);
                            }
                        }
                        in.close();
                        fileContentMap.put("rows", rowList);
                        resultSetList.add(fileContentMap);
                    }
                }
            }
        } catch (Exception e) {
            log.error("文件读取失败！", e);
        } finally {
            disconnect();
        }
        return resultSetList;
    }
}
