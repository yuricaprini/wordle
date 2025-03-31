package io.github.yuricaprini.wordleserver.circle05configurations.factories;

import io.github.yuricaprini.wordleprotocol.remoteinterfaces.UserRegistrationRemoteService;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.RegisterUser;

public class UserRegistrationRemoteServiceFactory implements UserRegistrationRemoteService.Factory {

  @Override
  public UserRegistrationRemoteService createUserRegistrationRemoteService() {
    return new RegisterUser();
  }

}
