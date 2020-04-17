package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.TaskTime;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.mapper.TaskTimeMapper;
import com.ecnu2020.achieveit.service.TaskTimeService;
import com.ecnu2020.achieveit.util.SendMail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 实现TaskTimeService
 * @Author ZC
 **/
@Slf4j
@Service
public class TaskTimeServiceImpl implements TaskTimeService {

    @Autowired
    private TaskTimeMapper taskTimeMapper;

    @Autowired
    private AuthMapper authMapper;


    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private SendMail sendMail;


    @Override
    @Transactional
    public TaskTime createTaskTime(String projectId,TaskTime taskTime){
        if(judgeIfExistAuth(projectId))throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
        long day = (taskTime.getUpdateTime().getTime() - taskTime.getEndTime().getTime())/(24*60*60*1000);
        if(day>3) throw new RRException(ExceptionTypeEnum.ADD_TASKTIME_FAIL);
        taskTimeMapper.insertSelective(taskTime);
        sendReviewMail(projectId);
        return taskTimeMapper.selectOne(taskTime);
    }

    @Override
    @Transactional
    public Boolean ReviewTaskTime(Integer id,Short status){
        TaskTime taskTime = TaskTime.builder().id(id).build();
        TaskTime taskTimeExample = taskTimeMapper.selectOne(taskTime);

        if(status == -1) {
            sendFailMail(taskTimeExample.getBeginTime());
        }
        taskTimeExample.setStatus(status);
        return taskTimeMapper.updateByPrimaryKey(taskTimeExample) > 0;
    }

    @Override
    @Transactional
    public Boolean modTaskTime(String projectId,TaskTime taskTime){
        //可以更新开始结束时间，更新时间
        if(judgeIfExistAuth(projectId))throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
        long day = (taskTime.getUpdateTime().getTime() - taskTime.getEndTime().getTime())/(24*60*60*1000);
        if(day>3) throw new RRException(ExceptionTypeEnum.ADD_TASKTIME_FAIL);
        TaskTime taskTimeExample = taskTimeMapper.selectByPrimaryKey(taskTime.getId());
        if(taskTimeExample.getStatus()==1) throw new RRException(ExceptionTypeEnum.ADD_TASKTIME_REFUSE);
        sendReviewMail(projectId);
        return taskTimeMapper.updateByPrimaryKey(taskTime) > 0;
    }

    @Override
    public PageInfo<TaskTime> getTaskTimeList(PageParam pageParam){
        UserDTO currentUser  = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        TaskTime tempTaskTime = TaskTime.builder().staffId(currentUser.getId()).build();
        Auth auth = Auth.builder().staffId(currentUser.getId()).build();
        List<String> projectId = authMapper.select(auth)
                                .stream()
                                .filter(auth1 -> auth1.getRole().equals(RoleEnum.SUPERIOR.getRoleName()))
                                .map(e -> e.getProjectId())
                                .collect(Collectors.toList());
        List<Integer> taskTimeId;
        if(!projectId.isEmpty()) {
            Example example1 = new Example(TaskTime.class);
            example1.createCriteria().andIn("projectId", projectId);
            taskTimeId = taskTimeMapper.selectByExample(example1)
                             .stream()
                             .filter(taskTime -> taskTime.getStatus() == 0)
                             .map(taskTime -> taskTime.getId())
                            .collect(Collectors.toList());
        }else{
            taskTimeId = taskTimeMapper.select(tempTaskTime)
                    .stream()
                    .map(taskTime -> taskTime.getId())
                    .collect(Collectors.toList());
        }
        if(taskTimeId.isEmpty()) return new PageInfo<>();
        Example example = new Example(TaskTime.class);
        example.createCriteria().andIn("id",taskTimeId);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<TaskTime> taskTimeList = taskTimeMapper.selectByExample(example);
        return new PageInfo<>(taskTimeList);
    }

    private Boolean judgeIfExistAuth(String projectId) {
        UserDTO currentUser = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Example example = new Example(Auth.class);
        example.createCriteria().andEqualTo("projectId", projectId).andEqualTo("staffId", currentUser.getId());
        Auth authExample1 = authMapper.selectOneByExample(example);
        String str = RoleEnum.QA_MANAGER.getRoleName() + RoleEnum.TESTER.getRoleName() + RoleEnum.DEVELOPER.getRoleName();
        if (!str.contains(authExample1.getRole()) || authExample1.getTaskTimeAuth() == 0) return true;
        return false;
    }

   /**
     * @Author ZC
     * @Description 发送审核未通过邮件
   **/
    private void sendFailMail(Timestamp timestamp){
        UserDTO userDTO = (UserDTO)SecurityUtils.getSubject().getPrincipal();
        String failMessage = "您提交的" + timestamp + "工时信息未通过审核，请修改后重新提交";
        String targetMail = staffMapper.selectByPrimaryKey(userDTO.getId()).getEmail();
        String failSubject = "工时信息未通过审核";
        try {
            sendMail.sendMail(targetMail, failSubject, failMessage);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }
    /**
      * @Author ZC
      * @Description 发送请求审核邮件
    **/
    private void sendReviewMail(String projectId){
        UserDTO userDTO = (UserDTO)SecurityUtils.getSubject().getPrincipal();
        final  String reviewSubject = "审核工时信息";
        Auth auth  = Auth.builder()
                .projectId(projectId).role(RoleEnum.SUPERIOR.getRoleName()).build();
        String staffId = authMapper.selectOne(auth).getStaffId();
        System.out.println(staffId);
        String targetMail = staffMapper.selectByPrimaryKey(staffId).getEmail();
        System.out.println(targetMail);
        String reviewMessage = userDTO.getName() + "已提交工时信息，请审核";
        try {
            sendMail.sendMail(targetMail, reviewSubject, reviewMessage);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }
}

