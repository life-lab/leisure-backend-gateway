package com.github.hicolors.leisure.backend.gateway.application.rest;

import com.github.hicolors.leisure.backend.gateway.application.service.SignService;
import com.github.hicolors.leisure.backend.gateway.model.sign.*;
import com.github.hicolors.leisure.member.authorization.token.impl.AuthToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.text.MessageFormat;

/**
 * SignInRest
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */

@Api(tags = "sign", description = "登入/登出/注册等 相关操作")
@RestController
@RequestMapping("/")
@Validated
public class SignRest {

    @Autowired
    private SignService signService;

    @ApiOperation("登入 - 密码")
    @PostMapping("/sign-in/password")
    public AuthToken signInPassword(@RequestBody @Validated SignInPassword model) {
        return signService.password(model);
    }

    @ApiOperation("登入 - 获取手机验证码")
    @GetMapping("/sign-in/mobile/verification-code")
    public VerificationCode mobileVerificationCode(@RequestParam("mobile") String mobile, @RequestParam(value = "sign", required = false) String sign) {
        return new VerificationCode(MessageFormat.format("验证码已成功发送到 {0} 上，请注意查看！", mobile));
    }

    @ApiOperation("登入 - 手机")
    @PostMapping("/sign-in/mobile")
    public AuthToken signInMobile(@RequestBody @Validated SignInMobile model) {
        return signService.mobile(model);
    }

    @ApiOperation("登入 - 获取邮箱验证码")
    @GetMapping("/sign-in/email/verification-code")
    public VerificationCode emailVerificationCode(@Email(message = "不是一个合法的电子邮件地址") @RequestParam("email") String email, @RequestParam(value = "sign", required = false) String sign) {
        return new VerificationCode(MessageFormat.format("验证码已成功发送到 {0} 上，请注意查看！", email));
    }

    @ApiOperation("登入 - 邮箱")
    @PostMapping("/sign-in/email")
    public AuthToken signInEmail(@RequestBody @Validated SignInEmail model) {
        return signService.email(model);
    }

    @ApiOperation("登入 - 刷新 token")
    @PostMapping("/sign-in/refresh-token")
    public AuthToken signInRefreshToken(@RequestBody @Validated SignInRefreshToken model) {
        return signService.refreshToken(model);
    }

    @ApiOperation("登出")
    @GetMapping("/sign-out")
    public void signOut(@RequestHeader("access-token") String accessToken) {
        signService.signOut(accessToken);
    }
}
