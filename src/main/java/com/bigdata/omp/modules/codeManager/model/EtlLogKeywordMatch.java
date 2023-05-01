package com.bigdata.omp.modules.codeManager.model;

import lombok.Data;

import javax.persistence.*;

/**
 * etl_log_match 匹配规则表,主要存储不同的错误类型
 */
@Table(name = "etl_log_keyword_match")
@Data
public class EtlLogKeywordMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keyId;

    @Column(name = "MATCH_TYPE")
    private Long matchType;

    @Column(name = "KEY_WORDS")
    private String keyWords;

    @Column(name = "KEY_DESC")
    private String keyDesc;

    @Column(name = "KEY_TYPE")
    private String keyType;

    @Column(name = "SYSTEM_ID")
    private Long systemId;


}
