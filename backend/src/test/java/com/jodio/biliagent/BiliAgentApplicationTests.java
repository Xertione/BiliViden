package com.jodio.biliagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.redisson.spring.starter.RedissonAutoConfigurationV2")
class BiliAgentApplicationTests {

    @Test
    void contextLoads() {
    }
}
