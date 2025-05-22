package backend;

import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.boot.test.context.SpringBootTest;

@Configuration
public class TestConfiguration {

    private int port;

    public void setPort(int port) {
        this.port = port;
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

}
