package com.ecnu2020.achieveit.shiro;

import com.ecnu2020.achieveit.dto.UserDTO;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component
public class MyRealm extends AuthorizingRealm {

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
        String username = token.getUsername();
        String password = new String(token.getPassword());

        UserDTO userDTO = new UserDTO();
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDTO, token.getCredentials(), getName());
        return info;


        //认证失败
//        throw new AuthenticationException();
    }
}
