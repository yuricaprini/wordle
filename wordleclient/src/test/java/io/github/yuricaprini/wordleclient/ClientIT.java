package io.github.yuricaprini.wordleclient;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class ClientIT {

  final String command = "java";
  final String jarFlag = "-jar";
  final String clientJar = "target/client-executable-jar-with-dependencies.jar";
  final String clientConfig = "src/main/resources/client_config.json";

  @Test
  void shouldReturnIfMainHasMoreThanOneArg() throws IOException, InterruptedException {
    
    Process p = new ProcessBuilder(command, jarFlag, clientJar, clientConfig, "dumbarg").start();
    assert(p.waitFor()==1);

  }
}
