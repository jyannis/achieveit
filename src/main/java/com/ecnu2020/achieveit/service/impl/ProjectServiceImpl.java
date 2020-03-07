package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Project;
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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.ApiModelProperty;

import org.apache.logging.log4j.util.Strings;
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

    @Override
    public PageInfo<Project> list(ProjectCondition projectCondition , PageParam pageParam) {
        UserDTO currentUser= (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Auth authExample=Auth.builder().staffId(currentUser.getId()).build();

        //从Auth表中找到当前用户所在项目id列表
        List<String> projectIdList=
            authMapper.select(authExample)
                .stream()
                .map(auth -> auth.getProjectId())
                .collect(Collectors.toList());

        Example example = new Example(Project.class);
        example.createCriteria().andIn("id",projectIdList).andIn("status",projectCondition.getStatus());

        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());

        List<Project> content=projectMapper.selectByExample(example)
            .stream()
            //关键字
            .filter(project -> project.toString().contains(projectCondition.getKeyWord()))
            .collect(Collectors.toList());

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
        Optional.ofNullable(staffMapper.selectByPrimaryKey(superiorId))
            .filter(staff -> staff.getManager().equals((short)1))
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
            if(res=updateStatus(projectId,ProjectStatusEnum.REVIEW.getStatus())){
                //发送邮件给配置管理员, EPG Leader, QA Manager

            }
        }
        //拒绝
        else if(status.equals(-1)){
            if(res=updateStatus(projectId,ProjectStatusEnum.REJECTED.getStatus())){
                //发送邮件给项目经理

            }
        } else throw new RRException(ExceptionTypeEnum.INVALID_STATUS);
        return res;
    }

    @Override
    public Boolean deliver(String projectId) {
        return updateStatus(projectId,ProjectStatusEnum.DELIVER.getStatus());
    }

    @Override
    public Boolean close(String projectId) {
        Boolean res;
        if(res=updateStatus(projectId,ProjectStatusEnum.CLOSE.getStatus())){
            //发送邮件给组织配置管理员

        }
        return res;
    }

    @Override
    public Boolean file(String projectId) {
        return updateStatus(projectId,ProjectStatusEnum.FILE.getStatus());
    }

    private Boolean updateStatus(String projectId,String status){
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        old.setStatus(status);
        return projectMapper.updateByPrimaryKey(old)>0;
    }

}
