package com.yupi.springbootinit.bizMq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private BiMessageProducer myMessageProducer;
    @Test
    void sendMessage() {

    }
}