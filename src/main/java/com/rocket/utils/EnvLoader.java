package com.rocket.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnvLoader {

  static {
    try {
      Path path = Paths.get(".env");
      if (Files.exists(path)) {
        Files.lines(path)
            .filter(line -> line.contains("="))
            .forEach(line -> {
              String[] parts = line.split("=", 2);
              System.setProperty(parts[0], parts[1]);
            });
      }
    } catch (IOException e) {
      throw new RuntimeException(".env 파일을 읽어올 수 없습니다.", e);
    }
  }
}
