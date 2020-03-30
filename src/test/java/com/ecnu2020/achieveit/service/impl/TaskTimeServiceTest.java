package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.TaskTime;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.mapper.TaskTimeMapper;
import com.ecnu2020.achieveit.service.StaffService;
import com.ecnu2020.achieveit.service.impl.TaskTimeServiceImpl;
import com.ecnu2020.achieveit.shiro.MyRealm;
import com.ecnu2020.achieveit.util.SendMail;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.EntityHelper;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @Description test TaskTimeService Api
 * @Author ZC
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskTimeServiceTest {

    @Mock
    private TaskTimeMapper taskTimeMapper;

    @Mock
    private StaffMapper staffMapper;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private TaskTimeServiceImpl taskTimeServiceImpl;

    @Mock
    private SendMail sendMail;

    @Mock
    private  StaffService staffService;

    @InjectMocks
    private MyRealm myRealm;

    private TaskTime taskTime;
    private PageParam pageParam;
    private Auth auth;
    private Staff staff;
    private Subject subject;

    @Before
    public void setUp(){
        taskTime = new TaskTime();
        pageParam = new PageParam();
        auth = new Auth();
        staff = Staff.builder().id("test1").password("123456").build();
        EntityHelper.initEntityNameMap(Auth.class, new Config());
        when(staffService.login(anyString(),anyString())).thenReturn(staff);
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(myRealm);
        SecurityUtils.setSecurityManager(securityManager);
        subject = SecurityUtils.getSubject();
        auth = Auth.builder().role(RoleEnum.TEST_LEADER.getRoleName()).taskTimeAuth((short)1).build();
        taskTime = TaskTime.builder().updateTime(new Timestamp(System.currentTimeMillis()))
                .endTime(new Timestamp(System.currentTimeMillis())).id(1).status((short)0).build();
        AuthenticationToken token = new UsernamePasswordToken("test1", "123456");
        subject.login(token);

        when(authMapper.selectOneByExample(any())).thenReturn(auth);
        when(taskTimeMapper.insertSelective(any())).thenReturn(1);
        when(taskTimeMapper.selectOne(any())).thenReturn(taskTime);
        when(taskTimeMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectOne(any())).thenReturn(auth);
        when(staffMapper.selectByPrimaryKey(any())).thenReturn(staff);
        when(taskTimeMapper.selectByPrimaryKey(anyInt())).thenReturn(taskTime);
    }

    @Test
    public void testCreateTaskTime(){
        TaskTime testTaskTime = taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        Assert.assertEquals(testTaskTime.getId(),taskTime.getId());
    }

    @Test
    public void testReviewTaskTime(){
        Boolean bool = taskTimeServiceImpl.ReviewTaskTime(anyInt(),(short)-1);
        Assert.assertTrue(bool);
    }

    @Test
    public void testModTaskTime(){
        Boolean bool = taskTimeServiceImpl.modTaskTime(anyString(),taskTime);
        Assert.assertTrue(bool);
    }

    @Test
    public void testGetTaskTimeList(){
        List<TaskTime> list = new ArrayList<>();
        List<Auth> listAuth = new ArrayList<>();
        list.add(taskTime);
        listAuth.add(auth);
        when(taskTimeMapper.select(any())).thenReturn(list);
        when(taskTimeMapper.selectByExample(any())).thenReturn(list);
        when(authMapper.select(any())).thenReturn(listAuth);
        Assert.assertEquals(taskTimeServiceImpl.getTaskTimeList(pageParam).getPageNum(),1);
    }

    @Test
    public void testSuperiorGetTaskTimeList(){
        List<TaskTime> list = new ArrayList<>();
        List<Auth> listAuth = new ArrayList<>();
        auth.setRole(RoleEnum.SUPERIOR.getRoleName());
        list.add(taskTime);
        listAuth.add(auth);
        when(taskTimeMapper.select(any())).thenReturn(list);
        when(taskTimeMapper.selectByExample(any())).thenReturn(list);
        when(authMapper.select(any())).thenReturn(listAuth);
        Assert.assertEquals(taskTimeServiceImpl.getTaskTimeList(pageParam).getPageNum(),1);
    }


    @Test
    public void testCreateTaskTimeNotPersmission(){
        auth.setTaskTimeAuth((short)0);
        try{
            taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.PERMISSION_DENIED,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testCreateTaskTimeTimeFail(){
        taskTime.setEndTime(new Timestamp(System.currentTimeMillis()));
        taskTime.setUpdateTime(new Timestamp(System.currentTimeMillis()+ 4*24*60*60*1000));
        try{
            taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_TASKTIME_FAIL,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testCreateTaskTimeStateFail(){
        taskTime.setStatus((short)1);
        try{
            taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_TASKTIME_REFUSE,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testModTaskTimeStateFail(){
        taskTime.setStatus((short)1);
        try{
            taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_TASKTIME_REFUSE,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testModTaskTimeNotPersmission(){
        auth.setTaskTimeAuth((short)0);
        try{
            taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.PERMISSION_DENIED,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testModTaskTimeTimeFail(){
        taskTime.setEndTime(new Timestamp(System.currentTimeMillis()));
        taskTime.setUpdateTime(new Timestamp(System.currentTimeMillis()+ 4*24*60*60*1000));
        try{
            taskTimeServiceImpl.createTaskTime(anyString(),taskTime);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_TASKTIME_FAIL,e.getExceptionTypeEnum());
        }
    }

    @After
    public void tear(){
    }
}
