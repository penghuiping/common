package com.php25.common.core.util.jwt;

import com.google.common.collect.Lists;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * jwt令牌类
 *
 * @author penghuiping
 * @date 2021/3/6 09:12
 */
public abstract class JwtUtil {

    /**
     * 生成jwt令牌
     *
     * @param jti        令牌id
     * @param username   用户名
     * @param roles      角色列表
     * @param expireTime 令牌有效时长(秒) 如: 1800秒为30分钟
     * @param issuer     签发令牌者署名
     * @param privateKey RSA私钥
     * @return jwt令牌
     */
    public static String generateToken(String jti, String username, List<String> roles, Long expireTime,
                                       String issuer, Key privateKey) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + TimeUnit.SECONDS.toMillis(expireTime));
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", roles);
        return Jwts.builder().signWith(privateKey)
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setSubject(issuer)
                .setId(jti)
                .setExpiration(expired)
                .compact();
    }

    /**
     * 生成refreshToken
     *
     * @param jti        令牌id
     * @param username   用户名
     * @param expireTime 令牌有效时长(秒) 如: 1800秒为30分钟
     * @param issuer     签发令牌者署名
     * @param privateKey RSA私钥
     * @return refreshToken
     */
    public static String generateRefreshToken(String jti, String username, Long expireTime, String issuer, Key privateKey) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + TimeUnit.SECONDS.toMillis(expireTime));
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return Jwts.builder().signWith(privateKey)
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setSubject(issuer)
                .setId(jti)
                .setExpiration(expired)
                .compact();
    }

    /**
     * jwt令牌签名是否合法
     *
     * @param token     jwt令牌
     * @param publicKey RSA公钥
     * @return true:验签成功
     */
    public static boolean isValidSign(String token, Key publicKey) {
        return Jwts.parser().setSigningKey(publicKey).isSigned(token);
    }

    /**
     * 解析jwt令牌，提取用户角色信息
     *
     * @param token     jwt令牌
     * @param publicKey RSA公钥
     * @return 用户角色信息
     */
    public static UserRoleInfo parse(String token, Key publicKey) {
        Jws<Claims> jwtObject = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
        Claims claims = jwtObject.getBody();
        String jti = claims.getId();
        Object username = claims.getOrDefault("username", "");
        Object roleNames = claims.getOrDefault("roles", Lists.newArrayList());

        UserRoleInfo userRoleInfo = new UserRoleInfo();
        userRoleInfo.setUsername(username.toString());
        userRoleInfo.setRoleNames((List) roleNames);
        userRoleInfo.setJti(jti);
        return userRoleInfo;
    }
}
