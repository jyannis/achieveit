package com.ecnu2020.achieveit.common;

import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回实体
 * @author yan on 2020-02-27
 */
@Data
public class Result implements Serializable {

    private Integer code;
    private Integer status;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        return success("操作成功", data);
    }

    public static Result success(String mess, Object data) {
            Result m = new Result();
            m.setCode(0);
            m.setStatus(0);
            m.setData(data);
            m.setMsg(mess);
            return m;
    }

    public static Result fail(String mess) {
        return fail(mess, null);
    }

    public static Result fail(String mess, Object data) {
        Result m = new Result();
        m.setCode(-1);
        m.setStatus(-1);
        m.setData(data);
        m.setMsg(mess);

        return m;
    }

    public static Result result(ExceptionTypeEnum exceptionTypeEnum, Object data) {
        Result m = new Result();
        m.setCode(exceptionTypeEnum.getErrorCode());
        m.setStatus(-1);
        m.setData(data);
        m.setMsg(exceptionTypeEnum.getCodeMessage());

        return m;
    }

    public static Result result(ExceptionTypeEnum exceptionTypeEnum) {
        return result(exceptionTypeEnum,null);
    }
}