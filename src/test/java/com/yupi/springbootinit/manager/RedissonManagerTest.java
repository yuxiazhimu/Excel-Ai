package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RedissonManagerTest {

    @Resource
    private RedissonManager redissonManager;
    @Test
    void doLimiter() {
        String userId = "1";
        // 瞬间执行2次,每成功一次,就打印'成功'
//        for (int i = 0; i < 2; i++) {
//            redissonManager.doLimiter(userId);
//            System.out.println("成功");
//        }
//        // 睡1秒
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//
//        }
        // 瞬间执行5次,每成功一次,就打印'成功'
        for (int i = 0; i < 5; i++) {
            redissonManager.doLimiter(userId);
            System.out.println("成功");
        }
    }
}