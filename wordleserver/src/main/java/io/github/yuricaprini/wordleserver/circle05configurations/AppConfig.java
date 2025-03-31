package io.github.yuricaprini.wordleserver.circle05configurations;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.rmi.Remote;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.Top3NotificationRemoteService;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.UserRegistrationRemoteService;
import io.github.yuricaprini.wordleserver.circle02usecases.GameResultSharer;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.SecretWordRepository;
import io.github.yuricaprini.wordleserver.circle02usecases.Top3Notifier;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.LoadEntities;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.RefreshSecretWord;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.RegisterUser;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.StoreEntities;
import io.github.yuricaprini.wordleserver.circle03_adapters.ClientRequestHandler;
import io.github.yuricaprini.wordleserver.circle03_adapters.SecretWordRefresher;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.ClientRequestAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.EntityRepositoryAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.PersistenceRequestAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.RefreshRequestAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.SecretWordRepositoryAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.ShareRequestAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.Top3NotifyAdapter;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.TranslationRequestAdapter;
import io.github.yuricaprini.wordleserver.circle04frameworks.ChannelHandler;
import io.github.yuricaprini.wordleserver.circle04frameworks.DispatcherService;
import io.github.yuricaprini.wordleserver.circle04frameworks.ListenerService;
import io.github.yuricaprini.wordleserver.circle04frameworks.PersistenceService;
import io.github.yuricaprini.wordleserver.circle04frameworks.RegistrationFacade;
import io.github.yuricaprini.wordleserver.circle04frameworks.RemoteExposerService;
import io.github.yuricaprini.wordleserver.circle04frameworks.SecretWordRefresherService;
import io.github.yuricaprini.wordleserver.circle04frameworks.implementations.BasicListenerService;
import io.github.yuricaprini.wordleserver.circle04frameworks.implementations.BasicPersistenceService;
import io.github.yuricaprini.wordleserver.circle04frameworks.implementations.BasicRemoteExposerService;
import io.github.yuricaprini.wordleserver.circle04frameworks.implementations.BasicSecretWordRefresherService;
import io.github.yuricaprini.wordleserver.circle04frameworks.implementations.MultithreadedDispatcherService;
import io.github.yuricaprini.wordleserver.circle05configurations.factories.ChannelHandlerServiceFactory;
import io.github.yuricaprini.wordleserver.circle05configurations.factories.RegisteredUserUseCaseFactory;

public class AppConfig {

  private static ServerConfiguration config;

  public static void init(ServerConfiguration serverConfiguration) {
    config = serverConfiguration;
  }

  public static RegisteredUserUseCase.Factory getNewRegisteredUseCaseFactory() {
    return new RegisteredUserUseCaseFactory();
  }

  public static ChannelHandler.Factory getNewChannelHandlerServiceFactory() {
    return new ChannelHandlerServiceFactory();
  }

  public static ClientRequestHandler getNewClientRequestHandler() {
    return new ClientRequestAdapter(getNewRegisteredUseCaseFactory(),
        ProtocolFactoryProvider.newWordleRequestFactory());
  }

  public static UserRegistrationRemoteService getNewUserRegistrationRemoteService() {
    return new RegisterUser();
  }

  public static Top3NotificationRemoteService getNewTop3NotificationRemoteService() {
    return Top3NotifyAdapter.getInstance();
  }

  public static RemoteExposerService getNewRemoteExposerService(
      Hashtable<String, Remote> remoteObjs, int registryPort) {
    return new BasicRemoteExposerService(remoteObjs, registryPort);
  }

  public static DispatcherService getNewDispatcherService(Selector selector) {
    return new MultithreadedDispatcherService(selector,
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2),
        new LinkedBlockingDeque<ChannelHandler>());
  }

  public static ListenerService getNewListenerService(ServerSocketChannel listeningChannel,
      RegistrationFacade registrationFacade) {
    return new BasicListenerService(listeningChannel, registrationFacade,
        getNewChannelHandlerServiceFactory());
  }

  public static SecretWordRefresherService getNewSecretWordRefresherService(
      Long secretWordRefreshTime) {
    return new BasicSecretWordRefresherService(secretWordRefreshTime, getNewSecretWordRefresher());
  }

  private static SecretWordRefresher getNewSecretWordRefresher() {
    return new RefreshRequestAdapter(new RefreshSecretWord(
        SecretWordRepositoryAdapter.getInstance(), new TranslationRequestAdapter()));
  }

  public static SecretWordRepository getNewSecretWordRepository() {
    return SecretWordRepositoryAdapter.getInstance();
  }

  public static Top3Notifier getNewTop3Notifier() {
    return Top3NotifyAdapter.getInstance();
  }

  public static GameResultSharer getNewGameResultSharer() {
    return new ShareRequestAdapter(config.multicastGroup, config.multicastPort);
  }

  public static PersistenceService getNewPersistenceService() {
    return new BasicPersistenceService(new PersistenceRequestAdapter(
        new LoadEntities(new EntityRepositoryAdapter(config.usersFileName)),
        new StoreEntities(new EntityRepositoryAdapter(config.usersFileName))));
  }
}
