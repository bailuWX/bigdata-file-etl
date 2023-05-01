package com.bigdata.omp.modules.codeManager.service;

import com.bigdata.omp.modules.codeManager.model.EtlLogKeyword;
import com.bigdata.omp.modules.codeManager.model.EtlLogKeywordMatch;

import java.util.List;

/**
 * 解析每个子服务的日志文件,分析其中每一行的报错信息,
 */
public interface SaveLogDataService {

    /**
     * 错误日志收集表.插入错误日志,运行一次,表示向表中插入一行记录
     *
     * @param etlLogKeyword etlLogKeyword
     */
    void saveLogData(EtlLogKeyword etlLogKeyword);

    /**
     * 查询所有的匹配规则,得到每一行记录
     *
     * @return List
     */
    List<EtlLogKeywordMatch> selectEtlLogKeywordMatch();
}
