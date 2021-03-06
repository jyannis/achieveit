package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.Page;
import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.ConfigRequest;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.ProjectCondition;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.AuthService;
import com.ecnu2020.achieveit.service.ProjectService;
import com.ecnu2020.achieveit.util.SendMail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import tk.mybatis.mapper.entity.Example;

/**
 * @description:
 * @author: ganlirong
 * @create: 2020/03/03
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private SendMail sendMail;

    private static final String BUILD_MESSAGE = "用户%s，%s正在申请立项：%s，请审批。";

    private static final String BUILD_SUBJECT = "申请立项审批通知";

    private static final String REVIEW_APPROVE_MESSAGE = "用户%s，您的项目：%s，已审核通过。";

    private static final String REVIEW_SUBJECT = "立项审核结果通知";

    private static final String REVIEW_REJECT_MESSAGE = "用户%s，您的项目：%s，被项目上级驳回，立项失败。";

    private static final String APPLY_MESSAGE = "用户%s，项目：%s已提交归档申请，请审核。";

    private static final String APPLY_SUBJECT = "归档申请审批通知";

    private static final String FILE_APPROVE_MESSAGE = "用户%s，您的项目：%s，已通过归档申请。";

    private static final String FILE_SUBJECT = "归档申请审核结果通知";

    private static final String FILE_REJECT_MESSAGE = "用户%s，您的项目：%s，没有通过归档申请，请与配置管理员协商并修改归档资料，重新申请。";

    private static final String CONFIG_SUBJECT = "项目配置通知";

    private static final String CONFIG_MESSAGE = "用户%s，您的项目：%s，配置库已完成。";


    @Override
    public PageInfo<Project> list(ProjectCondition projectCondition, PageParam pageParam) {
        UserDTO currentUser = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Example authExample = new Example(Auth.class);
        authExample.createCriteria().andEqualTo("staffId", currentUser.getId()).andIn("role",
            projectCondition.getRoleEnum());

        //从Auth表中找到当前用户指定角色所在项目id列表
        List<String> projectIdList =
            authMapper.selectByExample(authExample)
                .stream()
                .map(auth -> auth.getProjectId())
                .collect(Collectors.toList());

        List<Project> content = new ArrayList<>();

        if (!projectIdList.isEmpty()) {
            Example example = new Example(Project.class);
            example.createCriteria()
                .orLike("name", "%" + projectCondition.getKeyWord() + "%")
                .orLike("description", "%" + projectCondition.getKeyWord() + "%")
                .orLike("technology", "%" + projectCondition.getKeyWord() + "%")
                .orLike("business", "%" + projectCondition.getKeyWord() + "%")
                .orLike("feature", "%" + projectCondition.getKeyWord() + "%")
                .orLike("customerInfo", "%" + projectCondition.getKeyWord() + "%")
                .orLike("vmSpace", "%" + projectCondition.getKeyWord() + "%")
                .orLike("gitPath", "%" + projectCondition.getKeyWord() + "%")
                .orLike("filePath", "%" + projectCondition.getKeyWord() + "%")
            ;

            example.and(example.createCriteria().andIn("id", projectIdList).andIn("status",
                projectCondition.getStatus()).andEqualTo("deleted", 0));

            content = projectMapper.selectByExample(example);

            return new PageInfo<>(content);
        }
        PageInfo<Project> res = new PageInfo<>(content);
        res.setPageSize(10);
        return res;
    }

    @Override
    public Project get(String projectId) {
        return projectMapper.selectByPrimaryKey(projectId);
    }

    @Override
    @Transactional
    public Project build(Project project, String superiorId, String configManagerId,
                         String qaManagerId, String epgLeaderId) {
        Project old = projectMapper.selectByPrimaryKey(project.getId());
        if (old != null) throw new RRException(ExceptionTypeEnum.PROJECTID_REPEATED);
        Staff superior = staffMapper.selectByPrimaryKey(superiorId);
        Optional.ofNullable(superior)
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.INVALID_STAFF));

        Staff configManager = staffMapper.selectByPrimaryKey(configManagerId);
        Optional.ofNullable(configManager)
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.INVALID_STAFF));

        Staff qaManager = staffMapper.selectByPrimaryKey(qaManagerId);
        Optional.ofNullable(qaManager)
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.INVALID_STAFF));

        Staff epgLeader = staffMapper.selectByPrimaryKey(epgLeaderId);
        Optional.ofNullable(epgLeader)
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.INVALID_STAFF));

        //分配项目上级
        Auth superiorAuth = Auth.builder()
            .staffId(superiorId)
            .projectId(project.getId())
            .role(RoleEnum.SUPERIOR.getRoleName())
            .fileAuth((short) 2).gitAuth((short) 2).taskTimeAuth((short) 1)
            .build();
        projectMapper.insertSelective(project);
        authMapper.insertSelective(superiorAuth);

        //分配项目经理
        UserDTO currentUser = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Auth manager = Auth.builder()
            .staffId(currentUser.getId())
            .projectId(project.getId())
            .role(RoleEnum.PROJECT_MANAGER.getRoleName())
            .fileAuth((short) 2).gitAuth((short) 2).taskTimeAuth((short) 1)
            .build();
        authMapper.insertSelective(manager);

        //分配组织配置管理员
        Auth configAuth = Auth.builder()
            .staffId(configManagerId)
            .projectId(project.getId())
            .role(RoleEnum.CONFIGURATION_MANAGER.getRoleName())
            .fileAuth((short) 0).gitAuth((short) 0).taskTimeAuth((short) 0)
            .build();
        authMapper.insertSelective(configAuth);

        //分配QA manager
        Auth qaAuth = Auth.builder()
            .staffId(qaManagerId)
            .projectId(project.getId())
            .role(RoleEnum.QA_MANAGER.getRoleName())
            .fileAuth((short) 0).gitAuth((short) 0).taskTimeAuth((short) 0)
            .build();
        authMapper.insertSelective(qaAuth);

        //分配EPG leader
        Auth epgAuth = Auth.builder()
            .staffId(epgLeaderId)
            .projectId(project.getId())
            .role(RoleEnum.EPG_LEADER.getRoleName())
            .fileAuth((short) 0).gitAuth((short) 0).taskTimeAuth((short) 0)
            .build();
        authMapper.insertSelective(epgAuth);

        return projectMapper.selectOne(project);
    }

    @Override
    @Transactional
    public Boolean applyBuild(String projectId) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old)
            .filter(p -> Arrays.asList(ProjectStatusEnum.REJECTED.getStatus(),
                ProjectStatusEnum.WAITING.getStatus()).contains(p.getStatus()))
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        updateStatus(old, ProjectStatusEnum.BUILD.getStatus());
        //发送通知给项目上级
        Example authExample = new Example(Auth.class);
        authExample.createCriteria().andEqualTo("projectId", projectId).andEqualTo("role", "项目上级");
        String superiorId =
            authMapper.selectByExample(authExample)
                .stream()
                .findAny()
                .map(auth -> auth.getStaffId())
                .get();
        Staff superior = staffMapper.selectByPrimaryKey(superiorId);
        UserDTO currentUser = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        String message = String.format(BUILD_MESSAGE, superior.getName(), currentUser.getName(),
            old.getName());
        sendMail.sendStaffEmail(Arrays.asList(superiorId), BUILD_SUBJECT, message);
        return true;
    }

    @Override
    @Transactional
    public Boolean update(Project project) {
        Optional.of(projectMapper.selectByPrimaryKey(project.getId()))
            .filter(p -> Arrays.asList(ProjectStatusEnum.REVIEW.getStatus(),
                ProjectStatusEnum.WAITING.getStatus(),
                ProjectStatusEnum.ONGOING.getStatus(), ProjectStatusEnum.REJECTED.getStatus()).contains(p.getStatus()))
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR));
        return projectMapper.updateByPrimaryKey(project) > 0;
    }

    @Override
    public Boolean config(ConfigRequest configRequest) {
        Project old = projectMapper.selectByPrimaryKey(configRequest.getId());
        Optional.of(old)
            .filter(p -> Arrays.asList(ProjectStatusEnum.REVIEW.getStatus(),
                ProjectStatusEnum.ONGOING.getStatus()).contains(p.getStatus()))
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR));
        BeanUtils.copyProperties(configRequest, old);
        projectMapper.updateByPrimaryKey(old);

        //发送邮件给项目经理
        List<Staff> staffList = getStaffList(old.getId(),
            Arrays.asList(RoleEnum.PROJECT_MANAGER.getRoleName()));
        staffList.forEach(staff -> {
            String message = String.format(CONFIG_MESSAGE, staff.getName(), old.getName());
            sendMail.sendStaffEmail(Arrays.asList(staff.getId()), CONFIG_SUBJECT, message);
        });

        return true;
    }

    @Override
    @Transactional
    public Boolean review(String projectId, Integer status) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        Boolean res;
        //通过
        if (status.equals(1)) {
            if (res = updateStatus(old, ProjectStatusEnum.REVIEW.getStatus())) {
                //发送邮件给项目经理、配置管理员, EPG Leader, QA Manager
                List<Staff> staffList = getStaffList(projectId,
                    Arrays.asList(RoleEnum.PROJECT_MANAGER.getRoleName(),
                        RoleEnum.CONFIGURATION_MANAGER.getRoleName(),
                        RoleEnum.EPG_LEADER.getRoleName(),
                        RoleEnum.QA_MANAGER.getRoleName()));

                staffList.forEach(staff -> {
                    String message = String.format(REVIEW_APPROVE_MESSAGE, staff.getName(),
                        old.getName());
                    sendMail.sendStaffEmail(Arrays.asList(staff.getId()), REVIEW_SUBJECT, message);
                });

            }
        }
        //拒绝
        else if (status.equals(-1)) {
            if (res = updateStatus(old, ProjectStatusEnum.REJECTED.getStatus())) {
                //发送邮件给项目经理
                List<Staff> staffList = getStaffList(projectId,
                    Arrays.asList(RoleEnum.PROJECT_MANAGER.getRoleName()));
                staffList.forEach(staff -> {
                    String message = String.format(REVIEW_REJECT_MESSAGE, staff.getName(),
                        old.getName());
                    sendMail.sendStaffEmail(Arrays.asList(staff.getId()), REVIEW_SUBJECT, message);
                });
            }
        } else throw new RRException(ExceptionTypeEnum.INVALID_STATUS);
        return res;
    }

    @Override
    @Transactional
    public Boolean deliver(String projectId) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        return updateStatus(old, ProjectStatusEnum.DELIVER.getStatus());
    }

    @Override
    @Transactional
    public Boolean close(String projectId) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        return updateStatus(old, ProjectStatusEnum.CLOSE.getStatus());
    }

    @Override
    @Transactional
    public Boolean apply(String projectId) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old)
            //已完结才能申请归档
            .filter(p -> ProjectStatusEnum.CLOSE.getStatus().equals(p.getStatus()))
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR));
        Boolean res;
        if (res = updateStatus(old, ProjectStatusEnum.APPLY.getStatus())) {
            //发送邮件给组织配置管理员
            List<Staff> staffList = getStaffList(projectId,
                Arrays.asList(RoleEnum.CONFIGURATION_MANAGER.getRoleName()));

            staffList.forEach(staff -> {
                String message = String.format(APPLY_MESSAGE, staff.getName(), old.getName());
                sendMail.sendStaffEmail(Arrays.asList(staff.getId()), APPLY_SUBJECT, message);
            });


        }
        return res;
    }


    @Override
    @Transactional
    public Boolean file(String projectId, Integer status) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old)
            //归档申请中的才能归档
            .filter(p -> ProjectStatusEnum.APPLY.getStatus().equals(p.getStatus()))
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR));
        Boolean res = true;
        //通过
        if (status.equals(1)) {
            if (res = updateStatus(old, ProjectStatusEnum.FILE.getStatus())) {
                //发送邮件给项目经理，通知归档申请已通过
                List<Staff> staffList = getStaffList(projectId,
                    Arrays.asList(RoleEnum.PROJECT_MANAGER.getRoleName()));

                staffList.forEach(staff -> {
                    String message = String.format(FILE_APPROVE_MESSAGE, staff.getName(),
                        old.getName());
                    sendMail.sendStaffEmail(Arrays.asList(staff.getId()), FILE_SUBJECT, message);
                });

            }
        }
        //拒绝
        else if (status.equals(-1)) {
            //变回已完结
            if (res = updateStatus(old, ProjectStatusEnum.CLOSE.getStatus())) {
                //发送邮件给项目经理 提示归档申请未通过，需要修改后重新提交申请
                List<Staff> staffList = getStaffList(projectId,
                    Arrays.asList(RoleEnum.PROJECT_MANAGER.getRoleName()));
                staffList.forEach(staff -> {
                    String message = String.format(FILE_REJECT_MESSAGE, staff.getName(),
                        old.getName());
                    sendMail.sendStaffEmail(Arrays.asList(staff.getId()), FILE_SUBJECT, message);
                });

            }

        } else throw new RRException(ExceptionTypeEnum.INVALID_STATUS);
        return res;

    }

    @Override
    @Transactional
    public Boolean delete(String projectId) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        old.setDeleted((short) 1);
        projectMapper.updateByPrimaryKey(old);
        return true;
    }


    @Override
    @Transactional
    public Boolean onGoing(String projectId) {
        Project old = projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old)
            .filter(p -> ProjectStatusEnum.REVIEW.getStatus().equals(p.getStatus()))
            .orElseThrow(() -> new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        return updateStatus(old, ProjectStatusEnum.ONGOING.getStatus());
    }

    public Boolean updateStatus(Project old, String status) {
        old.setStatus(status);
        return projectMapper.updateByPrimaryKey(old) > 0;
    }

    public List<Staff> getStaffList(String projectId, List<String> roles) {
        if (roles.isEmpty()) {
            return new ArrayList<>();
        }
        Example authExample = new Example(Auth.class);
        authExample.createCriteria().andEqualTo("projectId", projectId).andIn("role",
            roles);
        List<Staff> staffList = new ArrayList<>();

        authMapper.selectByExample(authExample)
            .stream()
            .map(auth -> auth.getStaffId())
            .distinct()
            .collect(Collectors.toList())
            .forEach(staffId -> {
                Staff staff = staffMapper.selectByPrimaryKey(staffId);
                staffList.add(staff);
            });


        return staffList;

    }


}
