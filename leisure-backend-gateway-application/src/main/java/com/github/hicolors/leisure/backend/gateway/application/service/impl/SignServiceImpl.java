package com.github.hicolors.leisure.backend.gateway.application.service.impl;

import com.github.hicolors.leisure.backend.gateway.application.client.UserClient;
import com.github.hicolors.leisure.backend.gateway.application.exception.BackendGatewayServerException;
import com.github.hicolors.leisure.backend.gateway.application.exception.EnumCodeMessage;
import com.github.hicolors.leisure.backend.gateway.application.service.SignService;
import com.github.hicolors.leisure.backend.gateway.application.token.RedisTokenStore;
import com.github.hicolors.leisure.backend.gateway.model.auth.AuthTokenModel;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInEmail;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInMobile;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInPassword;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInRefreshToken;
import com.github.hicolors.leisure.common.exception.ExtensionException;
import com.github.hicolors.leisure.member.model.persistence.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SignServiceImpl
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */

@Service
@Slf4j
public class SignServiceImpl implements SignService {

    private static final String VALIDATION_CODE = "000000";

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisTokenStore tokenStore;

    @Override
    public AuthTokenModel password(SignInPassword model) {
        Member member;
        try {
            member = userClient.queryOneByUniqueKeyAndPassword(model.getUsername(), model.getPassword());
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumCodeMessage.CREDENTIAL_ERROR);
        }
        return tokenStore.storeAccessToken(userClient.queryMemberAuthorization(member.getId()));
    }

    @Override
    public AuthTokenModel mobile(SignInMobile model) {
        if (!StringUtils.equals(VALIDATION_CODE, model.getValidationCode())) {
            throw new BackendGatewayServerException(EnumCodeMessage.VALIDATION_CODE_ERROR);
        }
        Member member;
        try {
            member = userClient.queryOneByMobile(model.getMobile());
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumCodeMessage.MEMBER_NON_EXSIT);
        }
        return tokenStore.storeAccessToken(userClient.queryMemberAuthorization(member.getId()));
    }

    @Override
    public AuthTokenModel email(SignInEmail model) {
        if (!StringUtils.equals(VALIDATION_CODE, model.getValidationCode())) {
            throw new BackendGatewayServerException(EnumCodeMessage.VALIDATION_CODE_ERROR);
        }
        Member member;
        try {
            member = userClient.queryOneByEmail(model.getEmail());
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumCodeMessage.MEMBER_NON_EXSIT);
        }
        return tokenStore.storeAccessToken(userClient.queryMemberAuthorization(member.getId()));
    }

    @Override
    public AuthTokenModel refreshToken(SignInRefreshToken model) {
        Long userId = tokenStore.findByRefreshToken(model.getRefreshToken());
        if (userId == 0L) {
            throw new BackendGatewayServerException(EnumCodeMessage.REFRESH_TOKEN_ERROR);
        }
        Member member;
        try {
            member = userClient.queryOneById(userId);
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumCodeMessage.MEMBER_NON_EXSIT);
        }
        return tokenStore.storeAccessToken(userClient.queryMemberAuthorization(member.getId()));
    }

    @Override
    public void signOut(String accessToken) {
        Long userId = tokenStore.findByAccessToken(accessToken);
        log.info("sign out user :{}", userId);
        tokenStore.clearToken(userId);
    }
}
