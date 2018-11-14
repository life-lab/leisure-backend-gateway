package com.github.hicolors.leisure.backend.gateway.application.token;

import com.github.hicolors.leisure.backend.gateway.model.auth.AuthTokenModel;
import com.github.hicolors.leisure.common.utils.JsonUtils;
import com.github.hicolors.leisure.member.model.authorization.MemberAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * RedisTokenStore
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/11
 */
@Component
@Slf4j
public class RedisTokenStore {

    private static final String USER_INFO_PREFIX = "auth:info:user:";

    private static final String USER_ACCESS_TOKEN_PREFIX = "auth:access-token:user:";

    private static final String USER_REFRESH_TOKEN_PREFIX = "auth:refresh-token:user:";

    private static final String ACCESS_TOKEN_PREFIX = "auth:access-token:";

    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh-token:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * access token 过期时间
     */
    @Value("${auth.accessTokenValidateSeconds:86400}")
    private Long accessTokenValidateSeconds;

    /**
     * refresh token 过期时间
     */
    @Value("${auth.refreshTokenValidateSeconds:2592000}")
    private Long refreshTokenValidateSeconds;

    /**
     * refresh token 小于当前时间间隔时候自动生成
     */
    @Value("${auth.newRefreshTokenIntervalSeconds:604800}")
    private Long newRefreshTokenIntervalSeconds;

    /**
     * 生成时使用的加密密钥
     */
    @Value("${auth.secret:leisure-backend-gateway-application}")
    private String secret;

    /**
     * 最大登录设备
     */
    private static final Integer MAX_SIGN_IN_DEVICE = 10;

    /**
     * 生成一个新的 refresh token
     *
     * @param member
     * @return
     */
    protected String storeRefreshToken(MemberAuthorization member) {
        //根据 userid 和 来源，获取 refresh token
        String refreshToken = stringRedisTemplate.opsForValue().get(generateUserRefreshTokenKey(member.getId()));
        //如果为空 生成一个
        if (StringUtils.isNotBlank((refreshToken))) {
            if (ObjectUtils.defaultIfNull(stringRedisTemplate.getExpire(generateRefreshTokenKey(refreshToken), TimeUnit.SECONDS), 0L) > newRefreshTokenIntervalSeconds) {
                return refreshToken;
            }else{
                //清除过期的 refresh token
                stringRedisTemplate.delete(generateRefreshTokenKey(refreshToken));
            }
        }
        //清除历史数据
        stringRedisTemplate.delete(generateUserRefreshTokenKey(member.getId()));

        //创建用户基础信息缓存
        storeUserInfo(member);

        //创建用户 refresh token 关联信息
        String userRefreshTokenKey = generateUserRefreshTokenKey(member.getId());
        refreshToken = randomStringByUserId(member.getId());
        stringRedisTemplate.opsForValue().set(userRefreshTokenKey, refreshToken, refreshTokenValidateSeconds, TimeUnit.SECONDS);

        //创建用户 refresh token
        stringRedisTemplate.opsForValue().set(generateRefreshTokenKey(refreshToken), String.valueOf(member.getId()), refreshTokenValidateSeconds, TimeUnit.SECONDS);
        return refreshToken;
    }

    public AuthTokenModel storeAccessToken(MemberAuthorization member) {
        //创建用户 token
        AuthTokenModel authTokenModel = new AuthTokenModel();
        String accessToken = randomStringByUserId(member.getId());
        authTokenModel.setAccessToken(accessToken);
        authTokenModel.setNickname(member.getNickName());
        authTokenModel.setTokenExpires(accessTokenValidateSeconds);
        stringRedisTemplate.opsForValue().set(generateAccessTokenKey(accessToken), String.valueOf(member.getId()), accessTokenValidateSeconds, TimeUnit.SECONDS);
        //更新用户token信息列表
        String userAccessTokenKey = generateUserAccessTokenKey(member.getId());
        long size = ObjectUtils.defaultIfNull(stringRedisTemplate.opsForList().size(userAccessTokenKey), 0L);
        //超出最大数量,pop最早进入的token
        if (size >= MAX_SIGN_IN_DEVICE) {
            String oldAccessToken = stringRedisTemplate.opsForList().rightPop(userAccessTokenKey);
            stringRedisTemplate.delete(generateAccessTokenKey(oldAccessToken));
        }
        stringRedisTemplate.opsForList().leftPush(userAccessTokenKey, accessToken);
        stringRedisTemplate.expire(userAccessTokenKey, refreshTokenValidateSeconds, TimeUnit.SECONDS);
        String refreshToken = storeRefreshToken(member);
        authTokenModel.setRefreshTokenExpires(stringRedisTemplate.getExpire(generateRefreshTokenKey(refreshToken)));
        authTokenModel.setRefreshToken(refreshToken);
        return authTokenModel;
    }

    public void clearToken(Long id) {
        //删 refresh token
        stringRedisTemplate.delete(generateRefreshTokenKey(ObjectUtils.defaultIfNull(stringRedisTemplate.opsForValue().get(generateUserRefreshTokenKey(id)), "")));
        stringRedisTemplate.delete(generateUserRefreshTokenKey(id));
        //删 access token
        ObjectUtils.defaultIfNull(stringRedisTemplate.opsForList().range(generateUserAccessTokenKey(id), 0, -1), new ArrayList<String>()).forEach(e -> stringRedisTemplate.delete(generateAccessTokenKey(e)));
        stringRedisTemplate.delete(generateUserAccessTokenKey(id));
        //删 user-info
        stringRedisTemplate.delete(generateUserInfoKey(id));
    }

    public Long findByAccessToken(String accessToken) {
        return Long.valueOf(ObjectUtils.defaultIfNull(stringRedisTemplate.opsForValue().get(generateAccessTokenKey(accessToken)), "0"));
    }

    public Long findByRefreshToken(String accessToken) {
        return Long.valueOf(ObjectUtils.defaultIfNull(stringRedisTemplate.opsForValue().get(generateRefreshTokenKey(accessToken)), "0"));
    }

    protected String randomStringByUserId(Long userId) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            //hash加密
            String code = userId + "MD5" + System.currentTimeMillis() + secret;
            byte[] bytes = digest.digest(code.getBytes(StandardCharsets.UTF_8));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", e);
        }
    }

    protected void storeUserInfo(MemberAuthorization member) {
        //创建用户基础信息缓存
        String userInfoJson = JsonUtils.serialize(member);
        String userInfoKey = generateUserInfoKey(member.getId());
        stringRedisTemplate.opsForValue().set(userInfoKey, userInfoJson, refreshTokenValidateSeconds, TimeUnit.SECONDS);

    }

    protected String generateUserInfoKey(Long id) {
        return USER_INFO_PREFIX + id;
    }

    protected String generateUserAccessTokenKey(Long id) {
        return USER_ACCESS_TOKEN_PREFIX + id;
    }

    protected String generateUserRefreshTokenKey(Long id) {
        return USER_REFRESH_TOKEN_PREFIX + id;
    }

    protected String generateAccessTokenKey(String accessToken) {
        return ACCESS_TOKEN_PREFIX + accessToken;
    }

    protected String generateRefreshTokenKey(String refreshToken) {
        return REFRESH_TOKEN_PREFIX + refreshToken;
    }


}