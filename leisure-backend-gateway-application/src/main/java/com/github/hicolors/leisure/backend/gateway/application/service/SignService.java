package com.github.hicolors.leisure.backend.gateway.application.service;

import com.github.hicolors.leisure.backend.gateway.model.auth.AuthTokenModel;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInEmail;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInMobile;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInPassword;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInRefreshToken;

/**
 * SignService
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
public interface SignService {

    AuthTokenModel password(SignInPassword model);

    AuthTokenModel mobile(SignInMobile model);

    AuthTokenModel email(SignInEmail model);

    AuthTokenModel refreshToken(SignInRefreshToken model);

    void signOut(String accessToken);
}
