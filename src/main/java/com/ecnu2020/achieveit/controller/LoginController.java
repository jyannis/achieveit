package com.ecnu2020.achieveit.controller;

import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RestController
@Api("登录")
@Validated
public class LoginController {

    @PostMapping("/login")
    @ApiOperation(value = "登录",response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "员工id", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码(长度6-20)", required = true, paramType = "query", dataType = "String")
    })
    public Object login(@NotBlank String id,
                          @NotNull @Size(min = 6,max = 20)String password) {

        AuthenticationToken token = new UsernamePasswordToken(id, password);

        //尝试登陆，将会调用realm的认证方法
        SecurityUtils.getSubject().login(token);

        return "success";
    }

}
