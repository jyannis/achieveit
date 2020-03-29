package com.ecnu2020.achieveit.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecnu2020.achieveit.AchieveitApplication;
import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Feature;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.ProjectCondition;
import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.AuthService;
import com.ecnu2020.achieveit.service.StaffService;
import com.ecnu2020.achieveit.service.impl.ProjectServiceImpl;
import com.ecnu2020.achieveit.shiro.MyRealm;
import com.ecnu2020.achieveit.util.SendMail;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * @description:
 * @author: ganlirong
 * @create: 2020/03/28
 */
@RunWith(SpringRunner.class)
//@PowerMockRunnerDelegate(SpringRunner.class)
//@PrepareForTest({SecurityUtils.class})//静态类mock
@SpringBootTest(classes = AchieveitApplication.class)
public class ProjectServiceImplTest {
    @MockBean
    private ProjectMapper projectMapper;
    @MockBean
    private AuthMapper authMapper;
    @MockBean
    private StaffMapper staffMapper;
    @Mock
    private StaffService staffService;
    @InjectMocks
    private MyRealm myRealm;
    @Autowired
    private ProjectServiceImpl projectService;
    @MockBean
    private AuthService authService;
    @MockBean
    private SendMail sendMail;

    private Project project;
    private List<String> roleEnum;
    private PageParam pageParam;
    private UserDTO currentUser;
    private Subject subject;
    private Staff staff;
    private Auth auth;


    @Before//对应jnuit5的beforeEach 4的@beforeClass对应5的beforeAll
    public void setUp() {
        auth=Auth.builder().staffId("test").projectId("0318testPro").build();
        staff=
            Staff.builder().id("test1").password("123456").email("1234@qq.com").manager((short)1).name("staffUser").build();
        currentUser= UserDTO.builder().id("test0").name("管理员").build();
        roleEnum = Arrays.stream(RoleEnum.values()).map(r -> r.getRoleName()).collect(Collectors.toList());
        pageParam = PageParam.builder().pageSize(10).pageNum(1).build();
        project = Project.builder().id("0318testPro").business("业务1").feature("特色功能").description("描述")
            .status(ProjectStatusEnum.BUILD.getStatus()).technology("spring").deleted((short) 0).build();

        when(staffService.login(anyString(),anyString())).thenReturn(staff);
        DefaultSecurityManager securityManager=new DefaultSecurityManager();
        securityManager.setRealm(myRealm);
        SecurityUtils.setSecurityManager(securityManager);
        subject=SecurityUtils.getSubject();
        AuthenticationToken token=new UsernamePasswordToken("test1","123456");
        subject.login(token);

        EntityHelper.initEntityNameMap(Auth.class, new Config());
        EntityHelper.initEntityNameMap(Staff.class, new Config());
        EntityHelper.initEntityNameMap(Project.class, new Config());

    }

    @Test
    public void list() {
        ProjectCondition projectCondition = new ProjectCondition();

        when(projectMapper.selectByExample(any())).thenReturn(Arrays.asList(project));
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));

        //Execute


        projectService.list(projectCondition, pageParam);
        verify(authMapper, times(1)).selectByExample(any());
        verify(projectMapper, times(1)).selectByExample(any());
    }

    @Test
    public void list_when_projectList_is_empty() {
        ProjectCondition projectCondition = new ProjectCondition();

        when(projectMapper.selectByExample(any())).thenReturn(Arrays.asList(project));
        when(authMapper.selectByExample(any())).thenReturn(new ArrayList<>());

        //Execute
        projectService.list(projectCondition, pageParam);
        verify(authMapper, times(1)).selectByExample(any());
        verify(projectMapper, times(0)).selectByExample(any());
    }

    @Test
    public void should_get_project_when_exist() {
        when(projectMapper.selectByPrimaryKey(eq(project.getId()))).thenReturn(project);
        //Execute
        Project actual=projectService.get(project.getId());
        verify(projectMapper, times(1)).selectByPrimaryKey(any());
        assertEquals(project,actual);
    }

    @Test
    public void should_get_null_when_not_exist() {
        when(projectMapper.selectByPrimaryKey(eq("notexsit"))).thenReturn(null);
        //Execute
        Project actual=projectService.get("notexsit");
        verify(projectMapper, times(1)).selectByPrimaryKey(eq("notexsit"));
        assertNull(actual);
    }

    @Test
    public void build() {
        when(staffMapper.selectByPrimaryKey(anyString())).thenReturn(staff);
        when(projectMapper.insertSelective(eq(project))).thenReturn(4);
        when(authService.addMemberAuth(anyString(),any())).thenReturn(auth);
        when(projectMapper.selectOne(project)).thenReturn(project);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());
        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        projectService.build(project,"t1","t2","t3","t4");
        verify(staffMapper, times(4)).selectByPrimaryKey(anyString());
        verify(authService, times(5)).addMemberAuth(anyString(),any());
        verify(projectMapper).selectOne(projectArgumentCaptor.capture());
        assertEquals(project,projectArgumentCaptor.getValue());
        assertEquals("1234@qq.com",staff.getEmail());
    }

    @Test
    public void apply_build_when_project_exist() {
        when(projectMapper.selectByPrimaryKey(any())).thenReturn(project);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());
        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        project.setStatus(ProjectStatusEnum.WAITING.getStatus());
        Boolean res=projectService.applyBuild(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.BUILD.getStatus(),projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void apply_build_when_project_not_exist() {
        when(projectMapper.selectByPrimaryKey(any())).thenReturn(null);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());
        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        projectService.applyBuild(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test(expected = RRException.class)
    public void build_when_exist() {
        when(projectMapper.selectByPrimaryKey(any())).thenReturn(project);
        //Execute
        projectService.build(project,"t1","t2","t3","t4");
        verify(authService, times(0)).addMemberAuth(anyString(),any());
    }

    @Test
    public void update_when_status_valid() {
        project.setStatus(ProjectStatusEnum.REVIEW.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(eq(project))).thenReturn(1);
        //Execute
        Boolean res=projectService.update(project);
        verify(projectMapper, times(1)).updateByPrimaryKey(any());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void update_when_status_invalid() {
        project.setStatus(ProjectStatusEnum.CLOSE.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(eq(project))).thenReturn(1);
        //Execute
        projectService.update(project);
        verify(projectMapper, times(0)).updateByPrimaryKey(any());
    }

    @Test
    public void review_when_approve() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));

        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.review(project.getId(),1);
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.REVIEW.getStatus(),projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test
    public void review_when_reject() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.review(project.getId(),-1);
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.REJECTED.getStatus(),projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void review_when_status_invalid() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any()).stream()
            .map(auth -> auth.getProjectId())
            .collect(Collectors.toList())).thenReturn(Arrays.asList(staff.getId()));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        projectService.review(project.getId(),0);
        verify(projectMapper, times(0)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test
    public void deliver_when_project_exist() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.deliver(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.DELIVER.getStatus(),
            projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void deliver_when_project_not_exist() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(null);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.deliver(project.getId());
        verify(projectMapper, times(0)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test
    public void close_when_project_exist() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.close(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.CLOSE.getStatus(),
            projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void close_when_project_not_exist() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(null);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.close(project.getId());
        verify(projectMapper, times(0)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test
    public void apply_when_status_valid() {
        project.setStatus(ProjectStatusEnum.CLOSE.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));

        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.apply(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.APPLY.getStatus(),
            projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void apply_when_status_invalid() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any()).stream()
            .map(auth -> auth.getProjectId())
            .collect(Collectors.toList())).thenReturn(Arrays.asList(staff.getId()));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        projectService.apply(project.getId());
        verify(projectMapper, times(0)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test
    public void file_when_approve() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));

        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        project.setStatus(ProjectStatusEnum.APPLY.getStatus());
        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.file(project.getId(),1);
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        verify(sendMail).sendStaffEmail(any(),anyString(),anyString());
        assertEquals(ProjectStatusEnum.FILE.getStatus(),projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test
    public void file_when_reject() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        project.setStatus(ProjectStatusEnum.APPLY.getStatus());
        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.file(project.getId(),-1);
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        verify(sendMail).sendStaffEmail(any(),anyString(),anyString());
        assertEquals(ProjectStatusEnum.CLOSE.getStatus(),projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }


    @Test(expected = RRException.class)
    public void file_when_status_invalid() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(authMapper.selectByExample(any()).stream()
            .map(auth -> auth.getProjectId())
            .collect(Collectors.toList())).thenReturn(Arrays.asList(staff.getId()));
        doNothing().when(sendMail).sendStaffEmail(any(),anyString(),anyString());

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        projectService.file(project.getId(),0);
        verify(projectMapper, times(0)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test
    public void delete_when_project_exist() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.delete(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals((short)1, projectArgumentCaptor.getValue().getDeleted().shortValue());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void delete_when_project_not_exist() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(null);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        projectService.delete(project.getId());
        verify(projectMapper, times(0)).updateByPrimaryKey(projectArgumentCaptor.capture());
    }

    @Test
    public void update_status_success() {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(null);
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(1);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.updateStatus(project,ProjectStatusEnum.CLOSE.getStatus());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertTrue(res);
    }

    @Test
    public void update_status_fail() {
        when(projectMapper.updateByPrimaryKey(any())).thenReturn(0);

        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);
        //Execute
        Boolean res=projectService.updateStatus(project,ProjectStatusEnum.CLOSE.getStatus());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertFalse(res);
    }

    @Test
    public void onGoing_when_status_valid() {
        project.setStatus(ProjectStatusEnum.REVIEW.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(eq(project))).thenReturn(1);
        ArgumentCaptor<Project> projectArgumentCaptor=ArgumentCaptor.forClass(Project.class);

        //Execute
        Boolean res=projectService.onGoing(project.getId());
        verify(projectMapper, times(1)).updateByPrimaryKey(projectArgumentCaptor.capture());
        assertEquals(ProjectStatusEnum.ONGOING.getStatus(),
            projectArgumentCaptor.getValue().getStatus());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void onGoing_when_status_invalid() {
        project.setStatus(ProjectStatusEnum.CLOSE.getStatus());
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(projectMapper.updateByPrimaryKey(eq(project))).thenReturn(1);
        //Execute
        projectService.onGoing(project.getId());
        verify(projectMapper, times(0)).updateByPrimaryKey(any());
    }

    @Test
    public void should_return_empty_when_roles_empty() {
        when(authMapper.selectByExample(any()).stream()
            .map(auth -> auth.getProjectId())
            .collect(Collectors.toList())).thenReturn(Arrays.asList(staff.getId()));
        //Execute
        List<String> res=projectService.getStaffIdList(project.getId(),new ArrayList<>());
        verify(authMapper, times(0)).selectByExample(any());
        assertEquals(0,res.size());
    }

    @Test
    public void should_return_list_when_roles_not_empty() {
        when(authMapper.selectByExample(any())).thenReturn(Arrays.asList(auth));
        //Execute
        List<String> res=projectService.getStaffIdList(project.getId(),
            Arrays.asList(RoleEnum.PROJECT_MANAGER.getRoleName()));
        verify(authMapper, times(1)).selectByExample(any());
        assertEquals(1,res.size());
    }

}



