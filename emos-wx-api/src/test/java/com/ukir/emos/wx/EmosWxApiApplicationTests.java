package com.ukir.emos.wx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmosWxApiApplicationTests {

    @Value("${emos.jwt.expire}")
    private int expire;

    @Test
    void contextLoads() {
        System.out.println(expire);
    }




}
