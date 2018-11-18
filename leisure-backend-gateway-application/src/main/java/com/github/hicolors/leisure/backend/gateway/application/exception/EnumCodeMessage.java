package com.github.hicolors.leisure.backend.gateway.application.exception;

/**
 * EnumCodeMessage
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/10/22
 */
public enum EnumCodeMessage {

    /**
     * member 服务全局错误异常码
     * <p>
     * 异常码示例：  110 000 001
     * <p>
     * <p>
     * 100 -> 前三位，代表服务（应用），不可更改。
     * <p>
     * 000 -> 中间三位，代表模块，公用，根据业务拆分模块。
     * <p>
     * 001 -> 后三位，代表具体异常码。
     *
     */

    /********* 验证相关 *********/
    ACCESS_TOKEN_IS_NULL(101000001L, "访问令牌不能为空！"),
    ACCESS_TOKEN_IS_INVALID(101000002L, "访问令牌无效！"),
    USER_INFO_IS_BLANK(101000003L, "暂无用户信息，请重新登录！"),

    /********* 角色权限相关 *********/
    CREDENTIAL_ERROR(101001000L, "用户名密码不匹配"),
    VALIDATION_CODE_ERROR(101001001L, "验证码不正确"),
    REFRESH_TOKEN_ERROR(101001002L, "refresh token 不正确"),
    MEMBER_NON_EXSIT(101001003L, "用户不存在"),

    ;

    private final Long code;

    private final String message;

    EnumCodeMessage(Long code, String message) {
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
