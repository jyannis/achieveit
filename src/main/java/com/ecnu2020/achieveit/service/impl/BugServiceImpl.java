package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Bug;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.BugEnum;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.BugMapper;
import com.ecnu2020.achieveit.service.BugService;
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
 * @Date 2020/3/11 14:41
 **/
@Service
public class BugServiceImpl implements BugService {

    @Autowired
    private BugMapper bugMapper;

    @Override
    @Transactional
    public Boolean modBug(Bug bug){
        if(bug.getStatus().equals(BugEnum.SOLVE.getStatus())) throw new RRException(ExceptionTypeEnum.UPDATE_BUG_FAIL);
        return bugMapper.updateByPrimaryKey(bug) > 0;
    }

    @Override
    @Transactional
    public Bug addBug(Bug bug){
        if(bugMapper.selectOne(bug)!=null) throw  new RRException(ExceptionTypeEnum.ADD_BUD_FAIL);
        bugMapper.insert(bug);
        return bugMapper.selectOne(bug);
    }

    @Override
    public PageInfo<Bug> getBugList(String projectId,PageParam pageParam){
        Example example = new Example(Bug.class);
        example.createCriteria().andEqualTo("projectId",projectId);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Bug> bugList = bugMapper.selectByExample(example);
        return new PageInfo<>(bugList);
    }
}
