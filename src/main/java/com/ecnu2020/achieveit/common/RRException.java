package com.ecnu2020.achieveit.common;

import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import lombok.Getter;

/**
 * 自定义RuntimeException
 * @author yan on 2020-02-27
 */
@Getter
public class RRException extends RuntimeException {

    private ExceptionTypeEnum exceptionTypeEnum;

    public RRException() {
    }

    public RRException(ExceptionTypeEnum exceptionTypeEnum) {
        this.exceptionTypeEnum = exceptionTypeEnum;
    }
}
