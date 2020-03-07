package com.ecnu2020.achieveit.enums;


/**
 * 异常枚举
 * @author yan on 2020-02-27
 */
public enum ExceptionTypeEnum {

    LOGIN_INVALID(1001,"登录状态失效，请重新登录"),
    PROJECTID_MISSING(1002,"缺失项目id"),
    PROJECTID_INVALID(1003,"无效的项目id"),

    PERMISSION_DENIED(1004,"权限不足"),
    LOGIN_FAILED(1005,"登录校验失败"),
    ADD_FAIL(1006,"用户已存在"),

    INVALID_STAFF(1007,"用户不存在"),
    PROJECTID_REPEATED(1008,"项目ID重复"),
    INVALID_STATUS(1009,"无效的状态"),
    SERVER_ERROR(-1,"服务器内部异常"),
    PROJECT_STATUS_ERROR(1010,"根据当前项目状态，信息不能修改")
    ;





    private int errorCode;

    private String codeMessage;

    ExceptionTypeEnum(int errorCode, String codeMessage) {
        this.errorCode = errorCode;
        this.codeMessage = codeMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getCodeMessage() {
        return codeMessage;
    }
}
