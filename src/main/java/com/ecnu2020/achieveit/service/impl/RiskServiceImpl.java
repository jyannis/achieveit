package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.RiskMapper;
import com.ecnu2020.achieveit.service.RiskService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/11 14:28
 **/
@Service
public class RiskServiceImpl implements RiskService {

    @Autowired
    private RiskMapper riskMapper;

    @Override
    @Transactional
    public Boolean modRisk(Risk risk){
        return riskMapper.updateByPrimaryKey(risk) > 0;
    }

    @Override
    @Transactional
    public Risk addRisk(Risk risk){
        if(riskMapper.selectOne(risk)!=null) throw  new RRException(ExceptionTypeEnum.ADD_RISK_FAIL);
        riskMapper.insert(risk);
        return riskMapper.selectOne(risk);
    }

    @Override
    public PageInfo<Risk> getRiskList(String projectId,PageParam pageParam){
        Example example = new Example(Risk.class);
        example.createCriteria().andEqualTo("projectId",projectId);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Risk> bugList = riskMapper.selectByExample(example);
        return new PageInfo<>(bugList);
    }
}
