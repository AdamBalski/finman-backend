package com.finman.finmanbackend;

import com.finman.finmanbackend.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@ExtendWith(SpringExtension.class)
//@Import(
//        value = {
//                ApplicationProperties.class
//        }
//)
@SpringBootTest(classes = {FinmanBackendApplication.class, ApplicationProperties.class})
public class FinmanBackendApplicationTests {
    @Test
    void contextLoads() {

    }
}
