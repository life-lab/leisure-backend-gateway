package com.github.life.lab.leisure.backend.gateway.application.exception;

/**
 * EnumCodeMessage
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/10/22
 */
public enum EnumBackendGatewayCodeMessage {

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

    /********* 验证相关 1010000xxx  定义在 sdk 中 *********/

    /********* 角色权限相关 *********/
    CREDENTIAL_ERROR(101001000L, "用户名密码不匹配，请您重新输入！"),
    VALIDATION_CODE_ERROR(101001001L, "验证码不正确，请您重新输入"),
    REFRESH_TOKEN_ERROR(101001002L, "刷新令牌不正确，请您重新登录！"),
    MEMBER_NON_EXSIT(101001003L, "当前用户不存在，，请您先注册"),
    MEMBER_PLATFORM_NON_EXSIT(101001004L, "当前用户没有所属企业，暂不支持登录管理平台！"),
    MEMBER_NOT_BELONG_THE_PLATFORM(101001005L, "当前用户不属于该企业，暂不支持切换到该企业！"),


    /********* 角色权限相关 *********/
    URL_DENY(101002000L, "访问被拒绝，该接口暂时停止服务！"),
    ;

    private final Long code;

    private final String message;

    EnumBackendGatewayCodeMessage(Long code, String message) {
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
