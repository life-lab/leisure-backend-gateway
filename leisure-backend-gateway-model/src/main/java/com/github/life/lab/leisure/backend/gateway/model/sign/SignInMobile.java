package com.github.life.lab.leisure.backend.gateway.model.sign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;


/**
 * SignInUsernamePassword
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
@Data
@ApiModel("手机号登录 model")
public class SignInMobile {
    @ApiModelProperty(notes = "手机号", required = true)
    @NotBlank(message = "手机号不允许为空")
    @Length(min = 10, max = 20, message = "手机号长度不合法")
    private String mobile;

    @ApiModelProperty(notes = "验证码", required = true)
    @NotBlank(message = "验证码不允许为空")
    @Length(min = 4, max = 8, message = "验证码长度不合法")
    private String validationCode;
}
