package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
