package com.github.hicolors.leisure.backend.gateway.application.service;

import com.github.hicolors.leisure.backend.gateway.model.sign.SignInEmail;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInMobile;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInPassword;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInRefreshToken;
import com.github.hicolors.leisure.member.authorization.token.impl.AuthToken;

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

    void signOut(String accessToken);
}
