package com.github.hicolors.leisure.backend.gateway.model.sign;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;


/**
 * SignInUsernamePassword
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
@Data
public class SignInPassword {

    @ApiModelProperty(notes = "用户名", required = true)
    @NotBlank(message = "用户名不允许为空")
    @Length(min = 4, max = 32, message = "用户名长度不合法")
    private String username;

    @ApiModelProperty(notes = "密码", required = true)
    @NotBlank(message = "密码不允许为空")
    @Length(min = 4, max = 32, message = "密码长度不合法")
    private String password;
}
