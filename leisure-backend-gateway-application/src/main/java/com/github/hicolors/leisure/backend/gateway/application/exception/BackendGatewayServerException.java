package com.github.hicolors.leisure.backend.gateway.application.exception;

import com.github.hicolors.leisure.common.exception.BusinessException;

/**
 * MemberServerException
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/10/22
 */
public class BackendGatewayServerException extends BusinessException {

    public BackendGatewayServerException(EnumCodeMessage codeMessage) {
        super(codeMessage.getCode(), codeMessage.getMessage(), null);
    }

    public BackendGatewayServerException(EnumCodeMessage codeMessage, Object data) {
        super(codeMessage.getCode(), codeMessage.getMessage(), data);
    }

    public BackendGatewayServerException(EnumCodeMessage codeMessage, String msg) {
        super(codeMessage.getCode(), msg, null);
    }
}