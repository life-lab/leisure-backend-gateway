package com.github.life.lab.leisure.backend.gateway.model.sign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;


/**
 * SignInEmail
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
@Data
@ApiModel("刷新令牌登录 model")
public class SignInRefreshToken {

    @ApiModelProperty("刷新 token")
    @NotBlank(message = "验证码不允许为空")
    @Length(min = 16, max = 32, message = "刷新 token 长度不合法")
    private String refreshToken;
}
