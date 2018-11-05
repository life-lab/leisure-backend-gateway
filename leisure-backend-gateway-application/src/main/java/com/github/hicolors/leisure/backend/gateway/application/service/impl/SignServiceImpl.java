package com.github.hicolors.leisure.backend.gateway.application.service.impl;

import com.github.hicolors.leisure.backend.gateway.application.service.SignService;
import com.github.hicolors.leisure.backend.gateway.model.auth.AuthTokenModel;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInEmail;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInMobile;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInPassword;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInRefreshToken;
import org.springframework.stereotype.Service;

/**
 * SignServiceImpl
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */

@Service
public class SignServiceImpl implements SignService {

    @Override
    public AuthTokenModel password(SignInPassword model) {
        return null;
    }

    @Override
    public AuthTokenModel mobile(SignInMobile model) {
        return null;
    }

    @Override
    public AuthTokenModel email(SignInEmail model) {
        return null;
    }

    @Override
    public AuthTokenModel refreshToken(SignInRefreshToken model) {
        return null;
    }

    @Override
    public void signOut(String accessToken) {

    }
}
