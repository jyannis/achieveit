package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.common.Page;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.enums.RoleEnum;

import java.util.List;

public interface AuthService {

    Boolean checkManager(UserDTO principal);

    Boolean checkRole(RoleEnum acquireRole, short acquireGitAuth, short acquireFileAuth, short acquireTaskTimeAuth, String projectId, UserDTO principal);

    Auth addMemberAuth(String projectId, String staffId, String roleName, short gitAuth, Short fileAuth, Short taskTimeAuth);

    void deleteMemberAuth(String projectId, String staffId, String roleName);

    Auth modMemberAuth(String projectId, String staffId, String roleName, short gitAuth, Short fileAuth, Short taskTimeAuth);

    Page<Auth> getProjectMember(String projectId,int pageNum, int count);
}
