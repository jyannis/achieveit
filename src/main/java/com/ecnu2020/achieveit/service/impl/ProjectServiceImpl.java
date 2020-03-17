package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.Staff;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public PageInfo<Project> list(ProjectCondition projectCondition , PageParam pageParam) {
        UserDTO currentUser= (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Example authExample=new Example(Auth.class);
        authExample.createCriteria().andEqualTo("staffId",currentUser.getId()).andIn("role",
            projectCondition.getRoleEnum());

        //从Auth表中找到当前用户指定角色所在项目id列表
        List<String> projectIdList=
            authMapper.selectByExample(authExample)
                .stream()
                .map(auth -> auth.getProjectId())
                .collect(Collectors.toList());

        Example example = new Example(Project.class);
        example.createCriteria().andIn("id",projectIdList).andIn("status",projectCondition.getStatus());

        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());

        List<Project> content=projectMapper.selectByExample(example)
//            .stream()
//            //关键字
//            .filter(project -> project.toString().contains(projectCondition.getKeyWord()))
//            .collect(Collectors.toList())
            ;

        return new PageInfo<>(content);
    }

    @Override
    public Project get(String projectId) {
        return projectMapper.selectByPrimaryKey(projectId);
    }

    @Override
    @Transactional
    public Project build(Project project, String superiorId) {
        Project old=projectMapper.selectByPrimaryKey(project.getId());
        if(old!=null)throw new RRException(ExceptionTypeEnum.PROJECTID_REPEATED);
        Staff superior=staffMapper.selectByPrimaryKey(superiorId);
        Optional.ofNullable(superior)
            .orElseThrow(()->new RRException(ExceptionTypeEnum.INVALID_STAFF));
        AddMemberReq addMemberReq = AddMemberReq.builder()
                                    .staffId(superiorId).role(RoleEnum.SUPERIOR.getRoleName())
                                    .fileAuth((short) 2).gitAuth((short) 2).taskTimeAuth((short)1)
                                    .build();
        authService.addMemberAuth(project.getId(),addMemberReq);
        projectMapper.insertSelective(project);

        //发送通知给项目上级

        return projectMapper.selectOne(project);
    }

    @Override
    public Boolean update(Project project) {
        Optional.of(projectMapper.selectByPrimaryKey(project.getId()))
            .filter(p -> Arrays.asList(ProjectStatusEnum.REVIEW.getStatus(),
            ProjectStatusEnum.ONGOING.getStatus()).contains(p.getStatus()))
            .orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR));
        return projectMapper.updateByPrimaryKey(project)>0;
    }

    @Override
    public Boolean review(String projectId, Integer status) {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        Boolean res;
        //通过
        if(status.equals(1)){
            if(res=updateStatus(old,ProjectStatusEnum.REVIEW.getStatus())){
                //发送邮件给配置管理员, EPG Leader, QA Manager

            }
        }
        //拒绝
        else if(status.equals(-1)){
            if(res=updateStatus(old,ProjectStatusEnum.REJECTED.getStatus())){
                //发送邮件给项目经理

            }
        } else throw new RRException(ExceptionTypeEnum.INVALID_STATUS);
        return res;
    }

    @Override
    public Boolean deliver(String projectId) {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        return updateStatus(old,ProjectStatusEnum.DELIVER.getStatus());
    }

    @Override
    public Boolean close(String projectId) {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        return updateStatus(old,ProjectStatusEnum.CLOSE.getStatus());
    }

    @Override
    public Boolean apply(String projectId) {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old)
            //已完结才能申请归档
            .filter(p -> ProjectStatusEnum.CLOSE.getStatus().equals(p.getStatus()))
            .orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR));
        Boolean res;
        if(res=updateStatus(old,ProjectStatusEnum.APPLY.getStatus())){
            //发送邮件给组织配置管理员

        }
        return res;
    }

    @Override
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

            }
        }
        //拒绝
        else if (status.equals(-1)) {
            //变回已完结
            updateStatus(old, ProjectStatusEnum.CLOSE.getStatus());
            //发送邮件给项目经理 提示归档申请未通过，需要修改后重新提交申请


        } else throw new RRException(ExceptionTypeEnum.INVALID_STATUS);
        return res;

    }

    @Override
    public Boolean delete(String projectId) {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        old.setDeleted((short)1);
        projectMapper.updateByPrimaryKey(old);
        return true;
    }

    private Boolean updateStatus(Project old, String status){
        old.setStatus(status);
        return projectMapper.updateByPrimaryKey(old)>0;
    }

}
