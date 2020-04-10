package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.TaskTime;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.entity.request_response.auth.GetRoleRep;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.StaffService;
import com.ecnu2020.achieveit.service.impl.AuthServiceImpl;
import com.ecnu2020.achieveit.shiro.MyRealm;
import com.ecnu2020.achieveit.util.SendMail;
import junit.framework.TestCase;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @Description test AuthService imp
 * @Author ZC
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthServiceTest extends TestCase {

    @Mock
    private AuthMapper authMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private AuthServiceImpl authServiceImp;

    @Mock
    private SendMail sendMail;

    @Mock
    private StaffService staffService;

    @Mock
    private StaffMapper staffMapper;


    @InjectMocks
    private MyRealm myRealm;

    private AddMemberReq addMemberReq;
    private Auth auth;
    private Project project;
    private PageParam pageParam;
    private Staff staff;
    private Subject subject;
    private  UserDTO userDTO;
    private GetRoleRep getRoleRep;


    @Before
    public void setUp() {
        auth = new Auth();
        project = new Project();
        addMemberReq = new AddMemberReq();
        pageParam = new PageParam();
        staff = new Staff();
        userDTO = new UserDTO();
        userDTO.setId("1");
        getRoleRep = new GetRoleRep();
        EntityHelper.initEntityNameMap(Auth.class, new Config());
        when(staffService.login(anyString(),anyString())).thenReturn(staff);
       DefaultSecurityManager securityManager = new DefaultSecurityManager();
       securityManager.setRealm(myRealm);
       SecurityUtils.setSecurityManager(securityManager);
       subject = SecurityUtils.getSubject();
       auth = Auth.builder().role(RoleEnum.TEST_LEADER.getRoleName()).taskTimeAuth((short)1).build();
       AuthenticationToken token = new UsernamePasswordToken("test1", "123456");
       subject.login(token);

    }

    @Test
//    @Ignore
    public void testEPGLeaderAddAuth(){
        auth.setRole(RoleEnum.EPG_LEADER.getRoleName());
        addMemberReq.setRole(RoleEnum.EPG.getRoleName());
        project.setStatus(ProjectStatusEnum.BUILD.getStatus());
        when(authMapper.insertSelective(any())).thenReturn(1);
        when(staffMapper.selectByPrimaryKey(any())).thenReturn(staff);
        when(authMapper.selectOne(any())).thenReturn(auth);
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.addMemberAuth(anyString(), addMemberReq);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_FAIL,e.getExceptionTypeEnum());
        }
    }

    @Test
//    @Ignore
    public void testQAManagerAddAuth(){
        auth.setRole(RoleEnum.QA_MANAGER.getRoleName());
        addMemberReq.setRole(RoleEnum.QA.getRoleName());
        project.setStatus(ProjectStatusEnum.BUILD.getStatus());
        when(authMapper.insertSelective(any())).thenReturn(1);
        when(staffMapper.selectByPrimaryKey(any())).thenReturn(staff);
        when(authMapper.selectOne(any())).thenReturn(auth);
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.addMemberAuth(anyString(), addMemberReq);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_FAIL,e.getExceptionTypeEnum());
        }
    }

    @Test
//    @Ignore
    public void testNonAddAuth(){
        auth.setRole(RoleEnum.NON.getRoleName());
        project.setStatus(ProjectStatusEnum.BUILD.getStatus());
        when(authMapper.insertSelective(any())).thenReturn(1);
        when(staffMapper.selectByPrimaryKey(any())).thenReturn(staff);
        when(authMapper.selectOne(any())).thenReturn(auth);
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.addMemberAuth(anyString(), addMemberReq);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.PERMISSION_DENIED,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testCheckRole(){
        Short testShort = 1;
        when(authMapper.selectOne(any())).thenReturn(auth);
        Boolean bool = authServiceImp.checkRole(RoleEnum.NON,testShort,testShort,testShort,"",userDTO);
        Assert.assertTrue(bool);
    }

    @Test
    public void testCheckManager(){
        staff.setManager((short)1);
        when(staffMapper.selectOne(any())).thenReturn(staff);
        Boolean bool = authServiceImp.checkManager(userDTO);
        Assert.assertTrue(bool);
    }

    @Test
//    @Ignore
    public void testUpdateAuth(){
        project.setStatus(ProjectStatusEnum.BUILD.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(authMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectOne(any())).thenReturn(auth);
        Boolean bool = authServiceImp.modMemberAuth(anyString(),addMemberReq);
        Assert.assertTrue(bool);
    }

    @Test
//    @Ignore
    public void testDeleteAuth(){
        project.setStatus(ProjectStatusEnum.BUILD.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(authMapper.delete(any())).thenReturn(1);
        when(authMapper.selectOne(any())).thenReturn(auth);
        Boolean bool = authServiceImp.deleteMemberAuth(anyString(),"");
        Assert.assertTrue(bool);
    }


    @Test
//    @Ignore
    public void testAddWithProjectOver(){
        project.setStatus(ProjectStatusEnum.CLOSE.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.addMemberAuth(anyString(), addMemberReq);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.PROJECT_STATUS_ERROR,e.getExceptionTypeEnum());
        }
    }

    @Test
//    @Ignore
    public void testAddWithMemberExist(){
        auth.setRole(RoleEnum.PROJECT_MANAGER.getRoleName());
        project.setStatus(ProjectStatusEnum.REVIEW.getStatus());
        when(authMapper.insertSelective(any())).thenReturn(1);
        when(authMapper.selectOne(any())).thenReturn(auth);
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.addMemberAuth(anyString(), addMemberReq);
        }catch (RRException e) {
            Assert.assertEquals(ExceptionTypeEnum.ADD_FAIL, e.getExceptionTypeEnum());
        }
    }

    @Test
//    @Ignore
    public void testDeleteWithProjectOver(){
        project.setStatus(ProjectStatusEnum.CLOSE.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.deleteMemberAuth(anyString(),"");
        }catch (RRException e) {
            Assert.assertEquals(ExceptionTypeEnum.PROJECT_STATUS_ERROR, e.getExceptionTypeEnum());
        }
    }

    @Test
//    @Ignore
    public void testDeleteWithMemberNotExist(){
        project.setStatus(ProjectStatusEnum.REVIEW.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(authMapper.selectOne(any())).thenReturn(null);
        try {
            authServiceImp.deleteMemberAuth(anyString(),"");
        }catch (RRException e) {
            Assert.assertEquals(ExceptionTypeEnum.INVALID_STAFF, e.getExceptionTypeEnum());
        }
    }

    @Test
//    @Ignore
    public void testDeleteAuthFail(){
        auth.setRole(RoleEnum.PROJECT_MANAGER.getRoleName());
        project.setStatus(ProjectStatusEnum.REVIEW.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(authMapper.selectOne(any())).thenReturn(auth);
        try {
            authServiceImp.deleteMemberAuth(anyString(),"");
        }catch (RRException e) {
            Assert.assertEquals(ExceptionTypeEnum.DELETE_AUTH_FAIL, e.getExceptionTypeEnum());
        }
    }


    //    @Test
    @Ignore
    public void testUpdateWithMemberNotExist(){
        project.setStatus(ProjectStatusEnum.REVIEW.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.modMemberAuth(anyString(), addMemberReq);
        }catch (RRException e) {
            Assert.assertEquals(ExceptionTypeEnum.INVALID_STAFF, e.getExceptionTypeEnum());
        }
    }

    //    @Test
    @Ignore
    public void testUpdateWithProjectOver(){
        project.setStatus(ProjectStatusEnum.CLOSE.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        try {
            authServiceImp.modMemberAuth(anyString(),addMemberReq);
        }catch (RRException e) {
            Assert.assertEquals(ExceptionTypeEnum.PROJECT_STATUS_ERROR, e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testGetProjectMember(){
       auth.setId(1);
       List<Auth> testList = new ArrayList<>();
       testList.add(auth);
       when(authMapper.selectByExample(any())).thenReturn(testList);
       Assert.assertEquals(authServiceImp.getProjectMember("1","", pageParam).getPageNum(),1);
    }

    @Test
    public void testGetRoles(){
        auth.setId(1);
        List<Auth> testList = new ArrayList<>();
        testList.add(auth);
        when(authMapper.select(any())).thenReturn(testList);
        Assert.assertEquals(authServiceImp.getRoles(anyString(),pageParam).getPageNum(),1);
    }

}

