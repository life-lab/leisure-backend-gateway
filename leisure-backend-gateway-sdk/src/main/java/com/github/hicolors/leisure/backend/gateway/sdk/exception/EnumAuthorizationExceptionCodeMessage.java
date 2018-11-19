package com.github.hicolors.leisure.backend.gateway.sdk.exception;

/**
 * EnumMemberAuthorizationCodeMessage
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
public enum EnumAuthorizationExceptionCodeMessage {

    /**
     * 权限信息的相关错误
     */
    ACCESS_TOKEN_IS_NULL(101000001L, "访问令牌不能为空！"),
    ACCESS_TOKEN_IS_INVALID(101000002L, "访问令牌无效！"),
    USER_INFO_IS_BLANK(101000003L, "暂无用户信息，请重新登录！"),
    USER_INFO_NOT_FOUND(101000004L, "用户信息为空，请重新登录！"),
    ;

    private final Long code;

    private final String message;

    EnumAuthorizationExceptionCodeMessage(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}