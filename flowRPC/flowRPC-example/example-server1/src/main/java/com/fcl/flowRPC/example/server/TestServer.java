package com.fcl.flowRPC.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.fcl.flowRPC")
public class TestServer {

    public static void main(String[] args) {
        SpringApplication.run(TestServer.class, args);
    }


}
