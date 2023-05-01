package com.bigdata.omp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 自定义yml配置文件类
 */
@Component
@ConfigurationProperties(prefix = "savelog-properties")
@Data
public class SaveLogConfig {
    /**
     * 下载到本地服务器的缓存文件的目录前缀
     */
    private String saveFileDirectory;

    /**
     * 远程文件的文件名风格样式
     */
    private String path;


}
