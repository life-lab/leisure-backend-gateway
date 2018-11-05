package com.github.hicolors.leisure.backend.gateway.application.rest;

import com.github.hicolors.leisure.backend.gateway.application.service.SignService;
import com.github.hicolors.leisure.backend.gateway.model.auth.AuthTokenModel;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInEmail;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInMobile;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInPassword;
import com.github.hicolors.leisure.backend.gateway.model.sign.SignInRefreshToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * SignInRest
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */

@Api(tags = "sign", description = "登入/登出/注册等 相关操作")
@RestController
@RequestMapping("/")
public class SignRest {

    @Autowired
    private SignService signService;

    @ApiOperation("登入 - 密码")
    @PostMapping("/sign-in/password")
    public AuthTokenModel signInPassowrd(@RequestBody @Validated SignInPassword model) {
        return signService.password(model);
    }

    @ApiOperation("登入 - 手机")
    @PostMapping("/sign-in/mobile")
    public AuthTokenModel signInMobile(@RequestBody @Validated SignInMobile model) {
        return signService.mobile(model);
    }

    @ApiOperation("登入 - 邮箱")
    @PostMapping("/sign-in/email")
    public AuthTokenModel signInEmail(@RequestBody @Validated SignInEmail model) {
        return signService.email(model);
    }

    @ApiOperation("登入 - 刷新 token")
    @PostMapping("/sign-in/refresh-token")
    public AuthTokenModel signInRefreshToken(@RequestBody @Validated SignInRefreshToken model) {
        return signService.refreshToken(model);
    }

    @ApiOperation("登出")
    @GetMapping("/sign-out")
    public void signOut(@RequestHeader("access-token") String accessToken) {
        signService.signOut(accessToken);
    }
}