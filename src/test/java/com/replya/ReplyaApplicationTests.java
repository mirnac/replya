package com.replya;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// Levanta un Postgres real (Flyway corre las migraciones de db/migration contra él).
// @ServiceConnection cablea el datasource del contexto al contenedor.
// Los tokens dummy de WhatsApp/Anthropic viven en src/test/resources/application.properties.
// disabledWithoutDocker = true: en Railway/CI sin Docker el test se SALTEA (no falla),
// así `./gradlew build` pasa en el deploy. Local (con Docker) corre normal.
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class ReplyaApplicationTests {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void contextLoads() {
    }

}
