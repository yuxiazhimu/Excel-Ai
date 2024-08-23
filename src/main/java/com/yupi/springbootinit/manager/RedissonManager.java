package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedissonManager {
    @Resource
    private RedissonClient redissonClient;

    public void doLimiter(String key){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL,2, 1, RateIntervalUnit.SECONDS);
        if (!rateLimiter.tryAcquire(1)){
            throw new RuntimeException("操作繁忙");
        }


    }
}
