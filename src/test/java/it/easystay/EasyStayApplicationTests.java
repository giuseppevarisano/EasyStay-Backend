package it.easystay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2") // <--- Questo dice a Spring: "Ignora MySQL, usa application-test.properties"
class EasyStayApplicationTests {

    @Test
    void contextLoads() {
    }

}
