package io.fourfinanceit.controller;

import io.fourfinanceit.HomeworkApplication;
import io.fourfinanceit.model.Home;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(HomeworkApplication.class)
@WebIntegrationTest
public class HomeControllerWebIntegrationTest {

    @Test
    public void testHomeGet() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        ResponseEntity<Home> homeResponseEntity =
                restTemplate.getForEntity("http://localhost:8080/", Home.class);

        Home home = homeResponseEntity.getBody();
        assertNotNull(home);
        assertThat(home.getMessage(), Matchers.equalToIgnoringCase("Welcome to Micro lend API"));
    }

}
