package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Feature;
import com.ecnu2020.achieveit.entity.TaskTime;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.FeatureMapper;
import com.ecnu2020.achieveit.mapper.TaskTimeMapper;
import com.ecnu2020.achieveit.service.TaskTimeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/11 20:40
 **/
@Service
public class TaskTimeServiceImpl implements TaskTimeService {

    @Autowired
    private TaskTimeMapper taskTimeMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private FeatureMapper featureMapper;

    @Override
    @Transactional
    public TaskTime createTaskTime(String projectId,TaskTime taskTime){
        if(judgeIfExistAuth(projectId))throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
        long day = taskTime.getUpdateTime().getTime() - taskTime.getEndTime().getTime()/(24*60*60*1000);
        if(day>3) throw new RRException(ExceptionTypeEnum.ADD_TASKTIME_FAIL);
        taskTimeMapper.insert(taskTime);
        //TODO 发送邮件通知上级审核
        return taskTimeMapper.selectOne(taskTime);
    }

    @Override
    @Transactional
    public Boolean ReviewTaskTime(Integer id,Short status){
        TaskTime taskTime = TaskTime.builder().id(id).build();
        TaskTime taskTimeExample = taskTimeMapper.selectOne(taskTime);

        if(status == -1) {
            //TODO 发送邮件提醒修改工时
        }
        taskTimeExample.setStatus(status);
        return taskTimeMapper.updateByPrimaryKey(taskTimeExample) > 0;
    }

    @Override
    @Transactional
    public Boolean modTaskTime(String projectId,TaskTime taskTime){
        //可以更新开始结束时间，更新时间
        if(judgeIfExistAuth(projectId))throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
        long day = taskTime.getUpdateTime().getTime() - taskTime.getEndTime().getTime()/(24*60*60*1000);
        if(day>3) throw new RRException(ExceptionTypeEnum.ADD_TASKTIME_FAIL);
        System.out.println(taskTime.getId());
        if(taskTime.getStatus()==1) throw new RRException(ExceptionTypeEnum.ADD_TASKTIME_REFUSE);
        //TODO 发送邮件通知上级审核
        return taskTimeMapper.updateByPrimaryKey(taskTime) > 0;
    }

    @Override
    public PageInfo<TaskTime> getTaskTimeList(PageParam pageParam){
        UserDTO currentUser  = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Auth authExample=Auth.builder().staffId(currentUser.getId()).build();
        List<String> projectId = authMapper.select(authExample)
                .stream()
                .filter(auth -> auth.getRole().equals(RoleEnum.SUPERIOR.getRoleName()))
                .map(auth -> auth.getProjectId())
                .collect(Collectors.toList());
        if(projectId.isEmpty()) return new PageInfo<>(null);
        Example example = new Example(Feature.class);
        example.createCriteria().andIn("projectId",projectId);
        List<Integer> featureId = featureMapper.selectByExample(example)
                  .stream()
                  .map(feature -> feature.getId())
                  .collect(Collectors.toList());
        if(featureId.isEmpty()) return new PageInfo<>(null);
        Example example1 = new Example(TaskTime.class);
        example1.createCriteria().andIn("featureId",featureId);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<TaskTime> taskTimeList = taskTimeMapper.selectByExample(example1);
        return new PageInfo<>(taskTimeList);
    }

    private Boolean judgeIfExistAuth(String projectId){
        UserDTO currentUser  = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Example example = new Example(Auth.class);
        example.createCriteria().andEqualTo("projectId",projectId).andEqualTo("staffId",currentUser.getId());
        Auth authExample1= authMapper.selectOneByExample(example);
        String str = RoleEnum.TEST_LEADER.getRoleName() + RoleEnum.TESTER.getRoleName() + RoleEnum.DEVELOPER.getRoleName();
        if(!str.contains(authExample1.getRole()) || authExample1.getTaskTimeAuth() == 0) return true;
        return false;
    }
}

