package com.jodio.biliagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.redisson.spring.starter.RedissonAutoConfigurationV2")
@ActiveProfiles("test")
class BiliAgentApplicationTests {

    @Test
    void contextLoads() {
    }
}
