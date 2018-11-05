package com.github.hicolors.leisure.backend.gateway.model.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AuthTokenModel
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/5
 */
@Data
public class AuthTokenModel {

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("访问 token")
    private String accessToken;

    @ApiModelProperty("token 失效期")
    private Long tokenExpiresIn;

    @ApiModelProperty("刷新 token ")
    private String refreshToken;

    @ApiModelProperty("刷新 token 失效期")
    private Long refreshTokenExpiresIn;

}
