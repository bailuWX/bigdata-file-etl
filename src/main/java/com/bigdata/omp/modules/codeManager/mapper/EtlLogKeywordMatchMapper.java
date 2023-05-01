package com.bigdata.omp.modules.codeManager.mapper;

import com.bigdata.omp.modules.codeManager.model.EtlLogKeywordMatch;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 查询日志匹配规则表
 */
@Repository
public interface EtlLogKeywordMatchMapper extends Mapper<EtlLogKeywordMatch> {
}
