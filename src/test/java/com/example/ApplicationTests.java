package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.sql.init.mode=never",
        "spring.datasource.hikari.initialization-fail-timeout=-1"
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }
}
