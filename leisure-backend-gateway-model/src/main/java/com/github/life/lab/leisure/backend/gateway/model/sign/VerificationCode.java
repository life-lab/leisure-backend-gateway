package com.github.life.lab.leisure.backend.gateway.model.sign;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * VerificationCode
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/13
 */
@Data
@NoArgsConstructor
public class VerificationCode {

    private String message;

    private Date timestamp = new Date();

    public VerificationCode(String message) {
        this.message = message;
    }
}
