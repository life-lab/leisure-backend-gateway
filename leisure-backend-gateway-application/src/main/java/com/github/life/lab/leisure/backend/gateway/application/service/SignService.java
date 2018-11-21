package com.github.life.lab.leisure.backend.gateway.application.service;

import com.github.life.lab.leisure.backend.gateway.model.sign.*;
import com.github.life.lab.leisure.member.authorization.token.impl.AuthToken;

/**
 * SignService
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
public interface SignService {

    AuthToken password(SignInPassword model);

    AuthToken mobile(SignInMobile model);

    AuthToken email(SignInEmail model);

    AuthToken refreshToken(SignInRefreshToken model);

    PrimaryPlatform switchPrimaryPlatform(SwitchPlatformModel model, String accessToken);

    void signOut(String accessToken);

    void memberListener(Long id);
}
