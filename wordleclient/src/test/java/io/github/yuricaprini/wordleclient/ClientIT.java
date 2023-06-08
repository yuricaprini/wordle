package io.github.yuricaprini.wordleclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClientIT {

  final String java = "java";
  final String jarFlag = "-jar";
  final String clientJar = "target/client-executable-jar-with-dependencies.jar";
  final String mockServerJar = "target/mockserver-executable-test-jar-with-dependencies.jar";
  final String clientConfig = "src/main/resources/client_config.json";
  final static String DEFAULTBUNDLENAME = "CLIClientMessages";
  static ResourceBundle CLIMessages = null;

  @BeforeAll
  public static void loadBundle() {
    CLIMessages = ResourceBundle.getBundle(DEFAULTBUNDLENAME);
  }

  @Test
  void shouldReturnIfMainHasMoreThanOneArg() throws IOException, InterruptedException {
    Process c = new ProcessBuilder(java, jarFlag, clientJar, clientConfig, "dumbarg").start();
    BufferedReader cerrreader = new BufferedReader(new InputStreamReader(c.getErrorStream()));

    assert (CLIMessages.getString("ERR_USAGE").equals(cerrreader.readLine()));
    assert (c.waitFor() == 1);

    cerrreader.close();
  }

  @Test
  void shouldReturnIfConfigurationLoadFail() throws IOException, InterruptedException {
    Process c = new ProcessBuilder(java, jarFlag, clientJar, "dumbConfig").start();
    BufferedReader cerrreader = new BufferedReader(new InputStreamReader(c.getErrorStream()));

    assert (CLIMessages.getString("ERR_CONFIG_LOAD_FAIL").equals(cerrreader.readLine()));
    assert (c.waitFor() == 1);

    cerrreader.close();
  }

  @Test
  void shouldReturnIfConnectionToRemoteServiceFail() throws IOException, InterruptedException {
    Process c = new ProcessBuilder(java, jarFlag, clientJar).start();
    BufferedWriter cwriter = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
    BufferedReader cerrreader = new BufferedReader(new InputStreamReader(c.getErrorStream()));

    writeLine(cwriter, "register username Password1");
    assert (CLIMessages.getString("ERR_CONNECTION_TO_REMOTE_SERVICE_FAIL")
        .equals(cerrreader.readLine()));
    assert (c.waitFor() == 1);

    cwriter.close();
    cerrreader.close();
  }

  @Test
  void shouldReturnIfRegisterHasLessOrMoreThanTwoArgs() throws IOException, InterruptedException {
    Process c = new ProcessBuilder(java, jarFlag, clientJar).start();
    BufferedWriter cwriter = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
    BufferedReader cerrreader = new BufferedReader(new InputStreamReader(c.getErrorStream()));

    writeLine(cwriter, "register username");
    writeLine(cwriter, "register username Password1 dumbarg");
    writeLine(cwriter, "quit");

    assert (CLIMessages.getString("ERR_REGISTER_N_ARGS").equals(cerrreader.readLine()));
    assert (CLIMessages.getString("ERR_REGISTER_N_ARGS").equals(cerrreader.readLine()));
    assert (c.waitFor() == 0);

    cwriter.close();
    cerrreader.close();
  }

  @Test
  void shouldReturnIfRegisterGetsRightMsgsFromServer() throws IOException, InterruptedException {
    Process ms = new ProcessBuilder(java, jarFlag, mockServerJar).start();
    BufferedWriter mswriter = new BufferedWriter(new OutputStreamWriter(ms.getOutputStream()));
    BufferedReader msreader = new BufferedReader(new InputStreamReader(ms.getInputStream()));
    Process c = new ProcessBuilder(java, jarFlag, clientJar).start();
    BufferedWriter cwriter = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
    BufferedReader coutreader = new BufferedReader(new InputStreamReader(c.getInputStream()));
    BufferedReader cerrreader = new BufferedReader(new InputStreamReader(c.getErrorStream()));

    assert (msreader.readLine().equals("MockServerUP"));
    assert (coutreader.readLine().equals(CLIMessages.getString("OUT_CLIENT_RUNNING")));

    writeLine(cwriter, "register username Password1");
    assert (CLIMessages.getString("OUT_REGISTER_OK").equals(coutreader.readLine()));
    writeLine(cwriter, "register <6Cha Password1");
    assert (CLIMessages.getString("ERR_REGISTER_USERNAME_SHORT").equals(cerrreader.readLine()));
    writeLine(cwriter, "register Ismoreth8 Password1");
    assert (CLIMessages.getString("ERR_REGISTER_USERNAME_LONG").equals(cerrreader.readLine()));
    writeLine(cwriter, "register username <8Chars");
    assert (CLIMessages.getString("ERR_REGISTER_PASSWORD_SHORT").equals(cerrreader.readLine()));
    writeLine(cwriter, "register username Ismorethan16chars");
    assert (CLIMessages.getString("ERR_REGISTER_PASSWORD_LONG").equals(cerrreader.readLine()));
    writeLine(cwriter, "register username noDigits");
    assert (CLIMessages.getString("ERR_REGISTER_PASSWORD_NO_DIGIT").equals(cerrreader.readLine()));
    writeLine(cwriter, "register username no1uppercase");
    assert (CLIMessages.getString("ERR_REGISTER_PASSWORD_NO_UC").equals(cerrreader.readLine()));
    writeLine(cwriter, "register username Password1"); //already registered
    assert (CLIMessages.getString("ERR_REGISTER_ALREADY_REGISTERED").equals(cerrreader.readLine()));

    /* 
    Following two are handled before on client side because register command wants 2 args only.
    Despite this, these cases are also managed on the server side.
      
    writeLine(cwriter, "register 1 Blank Password1");
    assert (CLIMessages.getString("ERR_REGISTER_USERNAME_SPACE").equals(creader.readLine()));
    writeLine(cwriter, "register username 1 Blank");
    assert (CLIMessages.getString("ERR_REGISTER_PASSWORD_SPACE").equals(creader.readLine()));
    
    This never occurs, since the outcomes of the register command are constrained by compiler 
    
    assert (CLIMessages.getString("ERR_REGISTER_UNKNOWN_OUTCOME").equals(creader.readLine()));
    */

    writeLine(cwriter, "quit");
    assert (c.waitFor() == 0);
    writeLine(mswriter, "quit");
    assert (ms.waitFor() == 0);

    msreader.close();
    mswriter.close();
    coutreader.close();
    cerrreader.close();
    cwriter.close();
  }


  private void writeLine(BufferedWriter writer, String command) throws IOException {
    writer.write(command + System.lineSeparator());
    writer.flush();
  }

}
