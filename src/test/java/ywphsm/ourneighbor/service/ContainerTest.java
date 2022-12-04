//package ywphsm.ourneighbor.service;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.context.ActiveProfiles;
//import org.testcontainers.containers.MariaDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import static org.assertj.core.api.Assertions.*;
//
//@ActiveProfiles("test")
//@Testcontainers
//public class ContainerTest {
//
//    @Container
//    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer("mariadb:latest").withDatabaseName("testDB");
//
//    @Test
//    void test() {
//        assertThat(mariaDBContainer.isRunning()).isTrue();
//    }
//}
