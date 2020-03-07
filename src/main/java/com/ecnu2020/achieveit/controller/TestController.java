package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.common.Result;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.util.SendMail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供测试，生产时关闭
 * @author yan on 2020-02-27
 */
@RestController
@ConditionalOnExpression("${dev.enable:true}")//当enable为true时才选择加载该配置类
@Api("测试接口")
public class TestController {

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER,gitAuth = 1,fileAuth = 1)
    @GetMapping("/test1")
    @ApiOperation("权限异常测试")
    public Object test1(){
        UserDTO principal = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        return "success";
    }

    @Auth(role = RoleEnum.NON)
    @GetMapping("/test2")
    @ApiOperation("无权限测试")
    public Object test2(){
        UserDTO principal = (UserDTO) SecurityUtils.getSubject().getPrincipal();
        return 123;
    }


    @Autowired
    SendMail sendMail;

    @GetMapping("/mail/test/{mail}")
    @ApiOperation("邮件测试")
    public Object mailTest(@PathVariable("mail")String mail) throws Exception {
        sendMail.sendMail(mail,"邮件主题","邮件内容");
        return "success";
    }

}
