package io.github.yuricaprini.wordleclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;
import io.github.yuricaprini.winsomeprotocol.remoteinterfaces.UserRegistrationRemoteService;

public class MockServer implements UserRegistrationRemoteService {
  private HashMap<String, String> userMap = new HashMap<String, String>();

  public static void main(String[] args)
      throws RemoteException, NotBoundException, InterruptedException {

    MockServer mockServer = new MockServer();
    Registry registry = LocateRegistry.createRegistry(1099);

    registry.rebind(UserRegistrationRemoteService.class.getName(),
        (UserRegistrationRemoteService) UnicastRemoteObject.exportObject(mockServer, 0));

    System.out.println("MockServerUP");

    try (Scanner scanner = new Scanner(System.in)) {
      boolean quit = false;

      while (!quit) {

        String input = scanner.nextLine();
        switch (input) {
          case "quit":
            quit = true;
            break;

          default:
            break;
        }
      }
    }
    System.out.println("MockServerDOWN");

    //graceful cleanup: without unexporting server keeps running
    registry.unbind(UserRegistrationRemoteService.class.getName());
    UnicastRemoteObject.unexportObject(mockServer, true);
  }

  @Override
  public RegistrationOutcome registerUser(String username, String password) throws RemoteException {

    if (username.length() < 6)
      return RegistrationOutcome.USERNAME_SHORT;

    if (username.length() > 8)
      return RegistrationOutcome.USERNAME_LONG;

    if (username.contains(" "))
      return RegistrationOutcome.USERNAME_SPACE;

    if (password.length() < 8)
      return RegistrationOutcome.PASSWORD_SHORT;

    if (password.length() > 16)
      return RegistrationOutcome.PASSWORD_LONG;

    if (password.contains(" "))
      return RegistrationOutcome.PASSWORD_SPACE;

    if (containsNoDigit(password))
      return RegistrationOutcome.PASSWORD_NO_DIGIT;

    if (containsNoUpperCase(password))
      return RegistrationOutcome.PASSWORD_NO_UC;

    if (this.userMap.put(username, password) != null)
      return RegistrationOutcome.ALREADY_REGISTERED;

    return RegistrationOutcome.OK;
  }

  private boolean containsNoDigit(String input) {
    for (char c : input.toCharArray()) {
      if (Character.isDigit(c))
        return false;
    }
    return true;
  }

  public boolean containsNoUpperCase(String input) {
    for (char c : input.toCharArray()) {
      if (Character.isUpperCase(c))
        return false;
    }
    return true;
  }

}
