package com.rocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class RocketApplication {

  public static void main(String[] args) {
    new com.rocket.utils.EnvLoader();
    log.info("Rocket Application Started");
    log.info("JWT_SECRET = " + System.getProperty("JWT_SECRET"));

    SpringApplication.run(RocketApplication.class, args);
  }

}
