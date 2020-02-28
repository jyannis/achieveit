package com.ecnu2020.achieveit.shiro;

import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.service.StaffService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyRealm extends AuthorizingRealm {

    @Autowired
    StaffService staffService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        Object principal = principals.getPrimaryPrincipal();
        UserDTO userDTO = (UserDTO) principal;

        //注入角色与权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        return info;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        //数据库匹配，认证
        String id = token.getUsername();
        String password = new String(token.getPassword());

        Staff staff = staffService.login(id, password);
        UserDTO userDTO = UserDTO.builder()
                .id(staff.getId())
                .name(staff.getName())
                .department(staff.getDepartment())
                .build();
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDTO, token.getCredentials(), getName());
        return info;


        //认证失败
//        throw new AuthenticationException();
    }
}
