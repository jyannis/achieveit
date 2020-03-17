package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.github.pagehelper.PageInfo;

public interface AuthService {

    Boolean checkManager(UserDTO principal);

    Boolean checkRole(RoleEnum acquireRole, short acquireGitAuth, short acquireFileAuth, short acquireTaskTimeAuth, String projectId, UserDTO principal);

    /**
      * @Author Zc
      * @Description 增加人员权限
    **/
    Auth addMemberAuth(String projectId, AddMemberReq addMemberReq);

    /**
      * @Author Zc
      * @Description 删除人员权限
    **/
    Boolean deleteMemberAuth(String projectId, String staffId);

    /**
      * @Author Lucas
      * @Description 修改人员权限
    **/
    Boolean modMemberAuth(String projectId,AddMemberReq addMemberReq);

    /**
      * @Author Lucas
      * @Description 通过项目id查询人员权限列表并分页返回
    **/
    PageInfo<Auth> getProjectMember(String projectId, String keyword, PageParam pageParam);
}
