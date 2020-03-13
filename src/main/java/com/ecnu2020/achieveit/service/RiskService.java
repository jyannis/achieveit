package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/11 14:28
 **/
public interface RiskService {

    Boolean modRisk(Risk risk);

    Risk addRisk(Risk risk);

    PageInfo<Risk> getRiskList(String projectId,PageParam pageParam);
}
