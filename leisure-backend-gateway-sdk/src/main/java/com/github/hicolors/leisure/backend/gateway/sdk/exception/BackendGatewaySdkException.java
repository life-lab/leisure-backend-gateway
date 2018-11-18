package com.github.hicolors.leisure.backend.gateway.sdk.exception;

import com.github.hicolors.leisure.common.exception.BusinessException;

/**
 * exception
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
public class BackendGatewaySdkException extends BusinessException {

    public BackendGatewaySdkException(EnumBackendGatewaySdkCodeMessage codeMessage) {
        super(codeMessage.getCode(), codeMessage.getMessage(), null);
    }

    public BackendGatewaySdkException(EnumBackendGatewaySdkCodeMessage codeMessage, Object data) {
        super(codeMessage.getCode(), codeMessage.getMessage(), data);
    }
}