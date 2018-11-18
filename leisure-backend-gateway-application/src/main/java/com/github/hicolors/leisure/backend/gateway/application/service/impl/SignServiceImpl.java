package com.github.hicolors.leisure.backend.gateway.application.service.impl;

import com.github.hicolors.leisure.backend.gateway.application.exception.BackendGatewayServerException;
import com.github.hicolors.leisure.backend.gateway.application.exception.EnumBackendGatewayCodeMessage;
import com.github.hicolors.leisure.backend.gateway.application.service.SignService;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInEmail;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInMobile;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInPassword;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInRefreshToken;
import com.github.hicolors.leisure.common.exception.ExtensionException;
import com.github.hicolors.leisure.member.authorization.feign.SignInClient;
import com.github.hicolors.leisure.member.authorization.token.TokenStore;
import com.github.hicolors.leisure.member.authorization.token.impl.AuthToken;
import com.github.hicolors.leisure.member.authorization.validator.MemberValidator;
import com.github.hicolors.leisure.member.authorization.validator.exception.MemberAuthorizationException;
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
    private SignInClient userClient;

    @Autowired
    private TokenStore redisTokenStore;

    @Autowired
    private MemberValidator memberValidator;

    @Override
    public AuthToken password(SignInPassword model) {
        Member member;
        try {
            member = userClient.queryOneByUniqueKeyAndPassword(model.getUsername(), model.getPassword());
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.CREDENTIAL_ERROR);
        }
        return storeAccessToken(member);
    }

    @Override
    public AuthToken mobile(SignInMobile model) {
        if (!StringUtils.equals(VALIDATION_CODE, model.getValidationCode())) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.VALIDATION_CODE_ERROR);
        }
        Member member;
        try {
            member = userClient.queryOneByMobile(model.getMobile());
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_NON_EXSIT);
        }
        return storeAccessToken(member);
    }

    @Override
    public AuthToken email(SignInEmail model) {
        if (!StringUtils.equals(VALIDATION_CODE, model.getValidationCode())) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.VALIDATION_CODE_ERROR);
        }
        Member member;
        try {
            member = userClient.queryOneByEmail(model.getEmail());
        } catch (ExtensionException e) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_NON_EXSIT);
        }
        return storeAccessToken(member);
    }

    @Override
    public AuthToken refreshToken(SignInRefreshToken model) {
        Long userId = redisTokenStore.findUserIdByRefreshToken(model.getRefreshToken());
        if (userId == 0L) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.REFRESH_TOKEN_ERROR);
        }
        Member member;
        try {
            member = userClient.queryOneById(userId);
        } catch (ExtensionException e) {
            log.info("query member by id[{}] error", userId);
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_NON_EXSIT);
        }
        return storeAccessToken(member);
    }

    private AuthToken storeAccessToken(Member member) {
        memberValidator.validator(member);
        return redisTokenStore.storeAccessToken(userClient.queryMemberAuthorization(member.getId()));
    }

    @Override
    public void signOut(String accessToken) {
        Long userId = redisTokenStore.findUserIdByAccessToken(accessToken);
        log.info("sign out user :{}", userId);
        redisTokenStore.clearToken(userId);
    }

    @Override
    public void memberListener(Long id) {
        Member member;
        try {
            member = userClient.queryOneById(id);
        } catch (ExtensionException e) {
            log.info("query member by id[{}] error", id);
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_NON_EXSIT);
        }
        try {
            memberValidator.validator(member);
            redisTokenStore.storeUserInfo(userClient.queryMemberAuthorization(member.getId()));
        } catch (MemberAuthorizationException e) {
            log.info("member [{}] verification failed,clear all tokens now!", id);
            redisTokenStore.clearToken(id);
        }
    }
}
