package com.bigdata.omp.modules.codeManager.mapper;

import com.bigdata.omp.modules.codeManager.model.EtlLogKeyword;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 将分析后的数据 写入 错误日志表, 分类归档
 */
@Repository
public interface SaveLogDataMapper extends Mapper<EtlLogKeyword> {

}
