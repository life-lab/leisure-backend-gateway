package com.github.hicolors.leisure.backend.gateway.sdk.exception;

import com.github.hicolors.leisure.common.exception.ExtensionException;
import com.github.hicolors.leisure.common.exception.HttpStatus;

/**
 * exception
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
public class AuthorizationException extends ExtensionException {

    public AuthorizationException(EnumAuthorizationExceptionCodeMessage codeMessage) {
        super(HttpStatus.UNAUTHORIZED.value(), codeMessage.getCode(), codeMessage.getMessage(), null, null);
    }
}