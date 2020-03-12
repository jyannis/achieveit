package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.common.Page;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.entity.request_response.auth.DeleteMemberReq;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface AuthService {

    Boolean checkManager(UserDTO principal);

    Boolean checkRole(RoleEnum acquireRole, short acquireGitAuth, short acquireFileAuth, short acquireTaskTimeAuth, String projectId, UserDTO principal);

    /**
      * @Author Zc
      * @Description 增加人员权限
      * @Date 18:02 2020/3/4
      * @Param [projectId, addMemberReq]
      * @return Auth
    **/
    Auth addMemberAuth(String projectId, AddMemberReq addMemberReq);

    /**
      * @Author Zc
      * @Description 删除人员权限
      * @Date 18:02 2020/3/4
      * @Param [projectId, deleteMemberReq]
      * @return void
    **/
    Boolean deleteMemberAuth(String projectId, DeleteMemberReq deleteMemberReq);

    /**
      * @Author Lucas
      * @Description 修改人员权限
      * @Date 18:03 2020/3/4
      * @Param [projectId, addMemberReq]
      * @return Auth
    **/
    Boolean modMemberAuth(String projectId,AddMemberReq addMemberReq);

    /**
      * @Author Lucas
      * @Description 通过项目id查询人员权限列表并分页返回
      * @Date 18:04 2020/3/4
      * @Param [projectId, pageParam]
      * @return PageInfo<Auth>
    **/
    PageInfo<Auth> getProjectMember(String keyword, PageParam pageParam);
}
