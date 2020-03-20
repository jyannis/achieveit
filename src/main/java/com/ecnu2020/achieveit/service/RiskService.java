package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

/**
 * @Description 风险部分接口
 * @Author ZC
 **/
public interface RiskService {

    Boolean modRisk(Risk risk);

    Risk addRisk(Risk risk);

    PageInfo<Risk> getRiskList(String projectId,PageParam pageParam);

    Boolean delRisk(Integer id);

    /**
      * @Author ZC
      * @Description 风险跟踪
    **/
    void setRiskMail();
}
