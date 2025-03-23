package com.rocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RocketApplication {

  public static void main(String[] args) {
    new com.rocket.utils.EnvLoader();

    SpringApplication.run(RocketApplication.class, args);
  }

}
