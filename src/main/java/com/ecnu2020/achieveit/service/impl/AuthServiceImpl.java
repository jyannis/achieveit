package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.Page;
import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.AuthService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    StaffMapper staffMapper;

    @Autowired
    AuthMapper authMapper;

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
    public Auth addMemberAuth(String projectId, String staffId, String roleName, short gitAuth, Short fileAuth, Short taskTimeAuth) {
        if(getAuth(projectId,staffId,roleName)!= null) throw new RRException(ExceptionTypeEnum.ADD_FAIL);;
        Auth authExample = setAuth(projectId,staffId,roleName,gitAuth,fileAuth,taskTimeAuth);
        authMapper.insertSelective(authExample);
        return authMapper.selectOne(authExample);
    }

    @Override
    @Transactional
    public void deleteMemberAuth(String projectId, String staffId, String roleName) {
        Auth auth = getAuth(projectId,staffId,roleName);
        if(auth == null) throw new RRException(ExceptionTypeEnum.INVALID_STAFF);
        authMapper.delete(auth);
    }

    @Override
    @Transactional
    public Auth modMemberAuth(String projectId, String staffId, String roleName, short gitAuth, Short fileAuth, Short taskTimeAuth) {
        Auth auth = getAuth(projectId,staffId,roleName);
        if(auth == null) throw new RRException(ExceptionTypeEnum.INVALID_STAFF);
        Auth authExample = setAuth(projectId,staffId,roleName,gitAuth,fileAuth,taskTimeAuth);
        authExample.setId(auth.getId());
        authMapper.updateByPrimaryKey(authExample);
        return authMapper.selectOne(authExample);
    }

    @Override
    @Transactional
    public Page<Auth> getProjectMember(String projectId,int pageNum, int count){

        Example example = new Example(Auth.class);
        example.createCriteria().andEqualTo("projectId",projectId);
        RowBounds rowBounds = new RowBounds((pageNum - 1) * count, count);
        List<Auth> listAuth = authMapper.selectByExampleAndRowBounds(example, rowBounds);
        Page<Auth> page = new Page<>();
        int resCount = authMapper.selectCountByExample(example);
        if(resCount == 0) throw new RRException(ExceptionTypeEnum.PROJECTID_INVALID);
        page.setPageNum(pageNum);
        page.setTotal((long)resCount);
        int totalPages = resCount%count == 0? resCount/count:resCount/count+1;
        page.setPages(totalPages);
        page.setItems(listAuth);
        page.setPageSize(count);
        return page;
    }


    private Auth getAuth(String projectId, String staffId, String roleName){
        Auth exampleAuth = Auth.builder()
                        .projectId(projectId).staffId(staffId).role(roleName)
                        .build();
        return authMapper.selectOne(exampleAuth);
    }

    private Auth setAuth(String projectId, String staffId, String roleName,
                           short gitAuth,short fileAuth,short taskTimeAuth){
        Auth exampleAuth = Auth.builder()
                .projectId(projectId).staffId(staffId).role(roleName)
                .gitAuth(gitAuth).taskTimeAuth(taskTimeAuth).fileAuth(fileAuth)
                .build();
        return exampleAuth;
    }

}
