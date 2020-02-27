package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.common.RRException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnAuthController {

    @RequestMapping(value = "/unauth")
    public void unauth() {
        throw new RRException(ExceptionTypeEnum.LOGIN_INVALID);
    }

}
