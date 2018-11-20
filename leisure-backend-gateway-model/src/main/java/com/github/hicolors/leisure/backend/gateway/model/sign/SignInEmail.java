package com.github.hicolors.leisure.backend.gateway.model.sign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * SignInEmail
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
@Data
@ApiModel("邮箱登录 model")
public class SignInEmail {

    @ApiModelProperty(notes = "邮箱", required = true)
    @NotBlank(message = "邮箱不允许为空")
    @Email
    private String email;

    @ApiModelProperty(notes = "验证码", required = true)
    @NotBlank(message = "验证码不允许为空")
    @Length(min = 4, max = 8, message = "验证码长度不合法")
    private String validationCode;
}
