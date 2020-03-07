package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.AuthService;
import com.ecnu2020.achieveit.service.ProjectService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    private StaffMapper staffMapper;

    @Override
    public PageInfo<Object> list(String staffId, String beginTime, String endTime, String keyWord, Integer pageNum, Integer count) {
        PageHelper.startPage(pageNum,count);
        return null;
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
    public Boolean review(String projectId, Integer status) {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        //通过
        if(status.equals(1)){
            if(updateStatus(projectId,ProjectStatusEnum.REVIEW.getStatus())){
                //发送邮件给配置管理员, EPG Leader, QA Manager

                return true;
            }
            throw new RRException(ExceptionTypeEnum.SERVER_ERROR);
        }
        //拒绝
        else if(status.equals(-1)){
            if(updateStatus(projectId,ProjectStatusEnum.REJECTED.getStatus())){
                //发送邮件给项目经理

                return true;
            }
            throw new RRException(ExceptionTypeEnum.SERVER_ERROR);
        }
        throw new RRException(ExceptionTypeEnum.INVALID_STATUS);
    }

    @Override
    public Boolean deliver(String projectId) {
        return updateStatus(projectId,ProjectStatusEnum.DELIVER.getStatus());
    }

    @Override
    public Boolean close(String projectId) {
        return updateStatus(projectId,ProjectStatusEnum.CLOSE.getStatus());

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
