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
import com.ecnu2020.achieveit.service.StaffService;
import com.ecnu2020.achieveit.service.impl.StaffServiceImpl;
import com.ecnu2020.achieveit.shiro.MyRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @Description test StaffService Api
 * @Author ZC
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class StaffServiceTest {

    @Mock
    private StaffMapper staffMapper;

    @InjectMocks
    private StaffServiceImpl staffServiceImpl;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private StaffService staffService;

    @InjectMocks
    private MyRealm myRealm;

    private Staff staff;
    private PageParam pageParam;
    private Auth auth;
    private Subject subject;

    @Before
    public void setUp(){
        staff = new Staff();
        auth = new Auth();
        pageParam = new PageParam();
        staff.setId("1");
        auth.setStaffId("test");
        EntityHelper.initEntityNameMap(Staff.class, new Config());
        List<Staff> listStaff = new ArrayList<>();
        List<Auth> listAuth = new ArrayList<>();
        listAuth.add(auth);
        listStaff.add(staff);
        when(staffService.login(anyString(),anyString())).thenReturn(staff);
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(myRealm);
        SecurityUtils.setSecurityManager(securityManager);
        subject = SecurityUtils.getSubject();
        auth = Auth.builder().role(RoleEnum.TEST_LEADER.getRoleName()).taskTimeAuth((short)1).build();
        AuthenticationToken token = new UsernamePasswordToken("test1", "123456");
        subject.login(token);

        when(staffMapper.selectOne(any())).thenReturn(staff);
        when(staffMapper.selectByPrimaryKey(anyString())).thenReturn(staff);
        when(staffMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(staffMapper.selectAll()).thenReturn(listStaff);
        when(authMapper.select(any())).thenReturn(listAuth);
        when(staffMapper.selectByExample(any())).thenReturn(listStaff);
    }

    @Test
    public void testLogin(){
        Staff testStaff = staffServiceImpl.login(anyString(),"");
        Assert.assertEquals("1",testStaff.getId());
    }

    @Test
    public void testImportStaff(){
        auth.setRole(RoleEnum.PROJECT_MANAGER.getRoleName());
        when(authMapper.selectOne(any())).thenReturn(auth);
        Assert.assertEquals(staffServiceImpl.importStaff(anyString(),pageParam).getPageNum(),1);
    }

    @Test
    public void testModStaffInfo(){
        Boolean bool = staffServiceImpl.modStaffInfo(staff);
        Assert.assertTrue(bool);
    }

    @Test
    public void  testGetProjectStaff(){
        Assert.assertEquals(staffServiceImpl.getProjectStaff(anyString(),"",pageParam).getPageNum(),1);
    }

    @Test
    public void testImportStaffNotPermission(){
        auth.setRole(RoleEnum.TESTER.getRoleName());
        when(authMapper.selectOne(any())).thenReturn(auth);
        try{
            staffServiceImpl.importStaff(anyString(),pageParam);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.PERMISSION_DENIED,e.getExceptionTypeEnum());
        }
    }

}
