package com.bigdata.omp.modules.codeManager.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 错误日志分类归档表
 */
@Table(name = "etl_log_keyword")
@Data
public class EtlLogKeyword {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long keyId;

    @Column(name = "KEY_WORDS")
    private String keyWords;

    @Column(name = "KEY_DESC")
    private String keyDesc;

    @Column(name = "ERROR_TYPE")
    private String errorType;

    @Column(name = "ERROR_MSG")
    private String errorMsg;

    @Column(name = "HOST_NAME")
    private String hostName;

    @Column(name = "PORT")
    private String port;

}
