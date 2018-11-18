package com.github.hicolors.leisure.backend.gateway.sdk.exception;

/**
 * EnumMemberAuthorizationCodeMessage
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
public enum EnumBackendGatewaySdkCodeMessage {

    /**
     * 透传权限信息的相关错误
     */

    USER_INFO_NOT_FOUND(100110001L, "用户信息为空，请重新登录！"),
    ;

    private final Long code;

    private final String message;

    EnumBackendGatewaySdkCodeMessage(Long code, String message) {
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