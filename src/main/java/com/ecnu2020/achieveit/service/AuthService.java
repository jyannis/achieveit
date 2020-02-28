package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.enums.RoleEnum;

public interface AuthService {

    Boolean checkManager(UserDTO principal);

    Boolean checkRole(RoleEnum acquireRole, short acquireGitAuth, short acquireFileAuth, short acquireTaskTimeAuth, String projectId, UserDTO principal);

}
