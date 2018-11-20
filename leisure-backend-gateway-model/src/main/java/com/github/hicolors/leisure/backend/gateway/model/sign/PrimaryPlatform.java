package com.github.hicolors.leisure.backend.gateway.model.sign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PrimaryPlatform
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/20
 */
@Data
@ApiModel("主平台 模型")
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryPlatform {

    @ApiModelProperty("主平台 id")
    private Long platformId;

    @ApiModelProperty("主平台 名称")
    private String platformName;
}
