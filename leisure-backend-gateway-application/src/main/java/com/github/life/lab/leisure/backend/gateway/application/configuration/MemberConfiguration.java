package com.github.life.lab.leisure.backend.gateway.application.configuration;

import com.github.life.lab.leisure.member.authorization.token.TokenStore;
import com.github.life.lab.leisure.member.authorization.token.impl.RedisTokenStore;
import com.github.life.lab.leisure.member.authorization.validator.MemberValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MemberConfiguration
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @since 2018-12-04
 */
@Configuration
public class MemberConfiguration {

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore();
    }

    @Bean
    public MemberValidator memberValidator() {
        return new MemberValidator();
    }
}
