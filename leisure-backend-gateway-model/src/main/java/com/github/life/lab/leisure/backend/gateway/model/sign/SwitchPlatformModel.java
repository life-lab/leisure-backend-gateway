package com.github.life.lab.leisure.backend.gateway.model.sign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * SwitchPlatformModel
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/20
 */
@ApiModel("切换所属平台 model")
@Data
public class SwitchPlatformModel {

    @ApiModelProperty(notes = "平台", required = true)
    @NotNull(message = "平台 id不允许为空")
    private Long platformId;
}
