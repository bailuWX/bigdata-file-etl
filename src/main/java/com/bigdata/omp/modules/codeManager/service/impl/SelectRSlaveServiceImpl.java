package com.bigdata.omp.modules.codeManager.service.impl;

import com.bigdata.omp.modules.codeManager.mapper.SelectRSlaveMapper;
import com.bigdata.omp.modules.codeManager.model.RSlave;
import com.bigdata.omp.modules.codeManager.service.SelectRSlaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 查询所有的子服务属性
 */
@Service
public class SelectRSlaveServiceImpl implements SelectRSlaveService {

    @Autowired
    private SelectRSlaveMapper selectRSlaveMapper;

    @Override
    public List<RSlave> selectRSlave() {
        return selectRSlaveMapper.selectAll();
    }
}
