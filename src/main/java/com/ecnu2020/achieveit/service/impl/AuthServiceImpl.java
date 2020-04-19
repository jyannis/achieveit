package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.entity.request_response.auth.GetRoleRep;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.AuthService;

import com.ecnu2020.achieveit.util.SendMail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * @Author ZC
  * @Description 实现AuthService
**/
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    StaffMapper staffMapper;

    @Autowired
    AuthMapper authMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    SendMail sendMail;

    private static final String DISPUTE_EPG = "用户%s,您的项目：%s已分配 EPG";
    private static final String DISPUTE_QA = "用户%s,您的项目：%s已分配 QA";
    private static final String SUBJECT = "项目人员变更";

    @Override
    public Boolean checkManager(UserDTO principal) {
        Staff staff = staffMapper.selectOne(Staff.builder().id(principal.getId()).build());
        return staff != null && 1 == staff.getManager();
    }

    @Override
    public Boolean checkRole(RoleEnum acquireRole, short acquireGitAuth, short acquireFileAuth, short acquireTaskTimeAuth, String projectId, UserDTO principal) {
        Auth authExample = Auth.builder()
                .projectId(projectId)
                .role(acquireRole.getRoleName())
                .staffId(principal.getId())
                .build();
        authExample.setGitAuth(acquireGitAuth == 0 ? null:acquireGitAuth);
        authExample.setFileAuth(acquireFileAuth == 0 ? null:acquireFileAuth);
        authExample.setTaskTimeAuth(acquireTaskTimeAuth == 0 ? null:acquireTaskTimeAuth);
        return authMapper.selectOne(authExample) != null;
    }

    @Override
    @Transactional
    public Auth addMemberAuth(String projectId, AddMemberReq addMemberReq) {
        if(!isProjectOver(projectId)) throw new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR);
        UserDTO userDTO = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        Auth auth = Auth.builder().staffId(userDTO.getId()).projectId(projectId).build();
        Optional.ofNullable(authMapper.selectOne(auth)).orElseThrow(() -> new RRException(ExceptionTypeEnum.PERMISSION_DENIED));
        String role = authMapper.selectOne(auth).getRole();
        if(role.equals(RoleEnum.EPG_LEADER.getRoleName())){
            if(!addMemberReq.getRole().equals(RoleEnum.EPG.getRoleName())) throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
            try {
                sendMail.sendMail(staffMapper.selectByPrimaryKey(userDTO.getId()).getEmail(), SUBJECT, String.format(DISPUTE_EPG, userDTO.getName(), projectId));
            }catch (Exception e){
                log.warn(e.getMessage());
            }
        }else if(role.equals(RoleEnum.QA_MANAGER.getRoleName())){
            if(!addMemberReq.getRole().equals(RoleEnum.QA.getRoleName())) throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
            try {
                sendMail.sendMail(staffMapper.selectByPrimaryKey(userDTO.getId()).getEmail(), SUBJECT, String.format(DISPUTE_QA,userDTO.getName(),projectId));
            }catch (Exception e){
                log.warn(e.getMessage());
            }
        }else if(!role.equals(RoleEnum.PROJECT_MANAGER.getRoleName())){
            throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
        }
        if(getAuth(projectId,addMemberReq.getStaffId(),null)!= null) throw new RRException(ExceptionTypeEnum.ADD_FAIL);
        Auth authExample = setAuth(projectId,addMemberReq);
        authMapper.insertSelective(authExample);
        return authMapper.selectOne(authExample);
    }

    @Override
    @Transactional
    public Boolean deleteMemberAuth(String projectId, String staffId) {
        if(!isProjectOver(projectId)) throw new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR);
        Auth authExample = Auth.builder().staffId(staffId).projectId(projectId).build();
        Auth auth = authMapper.selectOne(authExample);
        Optional.ofNullable(auth).orElseThrow(()->new RRException(ExceptionTypeEnum.INVALID_STAFF));
        if(auth.getRole().equals(RoleEnum.PROJECT_MANAGER.getRoleName())) throw new RRException(ExceptionTypeEnum.DELETE_AUTH_FAIL);
        return authMapper.delete(auth) > 0;
    }

    @Override
    @Transactional
    public Boolean modMemberAuth(String projectId,AddMemberReq addMemberReq){
        if(!isProjectOver(projectId)) throw new RRException(ExceptionTypeEnum.PROJECT_STATUS_ERROR);
        Auth auth = getAuth(projectId,addMemberReq.getStaffId(),addMemberReq.getRole());
        Optional.ofNullable(auth).orElseThrow(()->new RRException(ExceptionTypeEnum.INVALID_STAFF));
        Auth authExample = setAuth(projectId,addMemberReq);
        authExample.setId(auth.getId());
        return authMapper.updateByPrimaryKey(authExample) > 0;
    }

    @Override
    @Transactional
    public PageInfo<Auth> getProjectMember(String projectId,String keyword, PageParam pageParam){
        Example example = new Example(Auth.class);
        example.createCriteria().andEqualTo("projectId",projectId);
        List<Integer> id = authMapper.selectByExample(example)
                .stream()
                .filter(auth -> auth.toString().contains(keyword))
                .map(auth -> auth.getId())
                .collect(Collectors.toList());
        if(id.isEmpty()) return new PageInfo<>();
        Example example1 = new Example(Auth.class);
        example1.createCriteria().andIn("id",id);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Auth> list = authMapper.selectByExample(example1);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public PageInfo<GetRoleRep> getRoles(String projectId, PageParam pageParam){
        List<Auth> auth = authMapper.select(Auth.builder().projectId(projectId).build());
        if(auth.isEmpty()) return new PageInfo<>();
        List<GetRoleRep> list = new ArrayList<>();
        for(Auth auth1:auth){
            GetRoleRep roleRep = GetRoleRep.builder().staffId(auth1.getStaffId()).role(auth1.getRole()).build();
            list.add(roleRep);
        }
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        return new PageInfo<>(list);
    }

    /**
      * @Author Zc`
      * @Description 返回人员权限
      * @Date 18:05 2020/3/4
      * @Param [projectId, staffId, roleName]
      * @return auth
    **/
    public Auth getAuth(String projectId, String staffId, String roleName){
        Auth exampleAuth = Auth.builder()
                        .projectId(projectId).staffId(staffId).role(roleName)
                        .build();
        return authMapper.selectOne(exampleAuth);
    }

    /**
      * @Author Zc
      * @Description 新建Auth对象
      * @Date 18:05 2020/3/4
      * @Param [projectId, addMemberReq]
      * @return auth
    **/
    public Auth setAuth(String projectId,AddMemberReq addMemberReq){
        Auth exampleAuth = Auth.builder()
                .projectId(projectId)
                .staffId(addMemberReq.getStaffId())
                .role(addMemberReq.getRole())
                .gitAuth(addMemberReq.getGitAuth())
                .taskTimeAuth(addMemberReq.getTaskTimeAuth())
                .fileAuth(addMemberReq.getFileAuth())
                .build();
        return exampleAuth;
    }

    /**
      * @Author Zc
      * @Description 判断项目状态是否为已归档或已交付
      * @Date 18:06 2020/3/4
      * @Param [projectId]
      * @return boolean
    **/
    public boolean isProjectOver(String projectId){
        String str = projectMapper.selectByPrimaryKey(projectId).getStatus();
        if(str.equals(ProjectStatusEnum.CLOSE.getStatus())||str.equals(ProjectStatusEnum.FILE.getStatus())) return false;
        return true;
    }


}
