package com.jodio.biliagent.schema;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FlywaySchemaTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateCoreTables() {
        Integer count = jdbcTemplate.queryForObject(
            "select count(*) from information_schema.tables where table_name in ('user','video','video_analysis_task','knowledge_card')",
            Integer.class
        );
        assertThat(count).isEqualTo(4);
    }
}
