package com.szxx.recruit.shiro;

import com.szxx.recruit.constants.Constant;
import com.szxx.recruit.exception.BusinessException;
import com.szxx.recruit.exception.code.BaseResponseCode;
import com.szxx.recruit.service.RedisService;
import com.szxx.recruit.utils.JwtTokenUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: CustomHashedCredentialsMatcher
 * TODO:类文件简单描述
 * @Author: yws
 * @CreateDate: 2020/9/7 13:42
 * @UpdateUser: yws
 * @UpdateDate: 2020/9/7 13:42
 * @Version: 0.0.1
 */
public class CustomHashedCredentialsMatcher extends SimpleCredentialsMatcher {

    @Autowired
    private RedisService redisService;
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        CustomPasswordToken customPasswordToken= (CustomPasswordToken) token;
        String accessToken = (String) customPasswordToken.getPrincipal();
        String userId= JwtTokenUtil.getUserId(accessToken);
        if(redisService.hasKey(Constant.ACCOUNT_LOCK_KEY+userId)){
            throw new BusinessException(BaseResponseCode.ACCOUNT_LOCK_ERROR);
        }
        if(redisService.hasKey(Constant.DELETED_USER_KEY+userId)){
            throw new BusinessException(BaseResponseCode.ACCOUNT_ERROR);
        }
        if(redisService.hasKey(Constant.JWT_REFRESH_TOKEN_BLACKLIST+accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_ERROR);
        }
        if(redisService.hasKey(Constant.JWT_REFRESH_STATUS+accessToken)){
            return true;
        }
        if(JwtTokenUtil.isTokenExpired(accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_PAST_DUE);
        }
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY+userId)&&redisService.getExpire(Constant.JWT_REFRESH_KEY+userId, TimeUnit.MILLISECONDS)>JwtTokenUtil.getRemainingTime(accessToken)){
            if(!redisService.hasKey(Constant.JWT_REFRESH_IDENTIFICATION+accessToken)){
                throw new BusinessException(BaseResponseCode.TOKEN_PAST_DUE);
            }
        }
        return true;
    }
}
