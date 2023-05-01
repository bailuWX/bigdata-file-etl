package com.bigdata.omp.modules.codeManager.service.impl;

import com.bigdata.omp.modules.codeManager.mapper.SaveLogDataMapper;
import com.bigdata.omp.modules.codeManager.model.EtlLogKeyword;
import com.bigdata.omp.modules.codeManager.mapper.EtlLogKeywordMatchMapper;
import com.bigdata.omp.modules.codeManager.model.EtlLogKeywordMatch;
import com.bigdata.omp.modules.codeManager.service.SaveLogDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 解析每个子服务的日志文件,分析其中每一行的报错信息,
 */
@Service
public class SaveLogDataServiceImpl implements SaveLogDataService {
    @Autowired
    private SaveLogDataMapper saveLogDataMapper;

    @Autowired
    private EtlLogKeywordMatchMapper etlLogKeywordMatchMapper;

    @Override
    public void saveLogData(EtlLogKeyword etlLogKeyword) {
        saveLogDataMapper.insert(etlLogKeyword);
    }

    @Override
    public List<EtlLogKeywordMatch> selectEtlLogKeywordMatch() {
        return etlLogKeywordMatchMapper.selectAll();
    }
}
