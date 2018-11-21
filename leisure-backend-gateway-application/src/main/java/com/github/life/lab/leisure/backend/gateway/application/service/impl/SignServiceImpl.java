package com.github.life.lab.leisure.backend.gateway.application.service.impl;

import com.github.life.lab.leisure.backend.gateway.application.exception.BackendGatewayServerException;
import com.github.life.lab.leisure.backend.gateway.application.exception.EnumBackendGatewayCodeMessage;
import com.github.life.lab.leisure.backend.gateway.application.service.SignService;
import com.github.life.lab.leisure.backend.gateway.model.sign.*;
import com.github.life.lab.leisure.backend.gateway.sdk.exception.AuthorizationException;
import com.github.life.lab.leisure.backend.gateway.sdk.exception.EnumAuthorizationExceptionCodeMessage;
import com.github.life.lab.leisure.common.exception.ExtensionException;
import com.github.life.lab.leisure.common.utils.JsonUtils;
import com.github.life.lab.leisure.member.authorization.feign.SignInClient;
import com.github.life.lab.leisure.member.authorization.token.TokenStore;
import com.github.life.lab.leisure.member.authorization.token.impl.AuthToken;
import com.github.life.lab.leisure.member.authorization.token.impl.MemberAuthorization;
import com.github.life.lab.leisure.member.authorization.validator.MemberValidator;
import com.github.life.lab.leisure.member.authorization.validator.exception.MemberAuthorizationException;
import com.github.life.lab.leisure.member.model.persistence.Member;
import com.github.life.lab.leisure.member.model.persistence.Platform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

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


    @Override
    public PrimaryPlatform switchPrimaryPlatform(SwitchPlatformModel model, String accessToken) {
        Long userId = redisTokenStore.findUserIdByAccessToken(accessToken);
        if (userId == 0L) {
            throw new AuthorizationException(EnumAuthorizationExceptionCodeMessage.ACCESS_TOKEN_IS_INVALID);
        }
        String userInfoJson = redisTokenStore.findUserInfoByUserId(userId);
        if (StringUtils.isBlank(userInfoJson)) {
            throw new AuthorizationException(EnumAuthorizationExceptionCodeMessage.USER_INFO_IS_BLANK);
        }
        MemberAuthorization userInfo = JsonUtils.deserialize(userInfoJson, MemberAuthorization.class);
        if (CollectionUtils.isEmpty(userInfo.getPlatformRoles())) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_PLATFORM_NON_EXSIT);
        } else {
            if (Objects.nonNull(userInfo.getPlatformRoles().get(model.getPlatformId()))) {
                Platform platform = userClient.queryPlatform(model.getPlatformId());
                userInfo.setPlatformId(platform.getId());
                userInfo.setPlatformName(platform.getName());
                redisTokenStore.storeUserInfo(userInfo);
                return new PrimaryPlatform(platform.getId(), platform.getName());
            } else {
                throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_NOT_BELONG_THE_PLATFORM);
            }
        }
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


    private AuthToken storeAccessToken(Member member) {
        if (Objects.nonNull(member.getMemberDetail())
                && Objects.isNull(member.getMemberDetail().getPlatform())) {
            throw new BackendGatewayServerException(EnumBackendGatewayCodeMessage.MEMBER_PLATFORM_NON_EXSIT);
        }
        memberValidator.validator(member);
        return redisTokenStore.storeAccessToken(userClient.queryMemberAuthorization(member.getId()));
    }
}
