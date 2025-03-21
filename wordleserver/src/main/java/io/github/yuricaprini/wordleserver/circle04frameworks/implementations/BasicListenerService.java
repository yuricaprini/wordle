package io.github.yuricaprini.wordleserver.circle04frameworks.implementations;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import io.github.yuricaprini.wordleserver.circle04frameworks.RegistrationFacade;
import io.github.yuricaprini.wordleserver.circle04frameworks.ChannelHandler;
import io.github.yuricaprini.wordleserver.circle04frameworks.ListenerService;

/**
 * A {@code BasicListenerService} is a basic implementation of {@code ListenerService}. 
 * 
 * @author Yuri Caprini
 */
public class BasicListenerService extends ListenerService {

  /**
   * Constructs a new {@code BasicListenerService} which will listen on the provided 
   * {@code listeningChannel} and will register to a dispatcher the established client channels 
   * via the provided {@code registrationFacade} along with their associated channel handlers
   * created with the given {@code handlerFactory}.
   * 
   * @param listeningChannel the listening channel used by the newly created listener service.
   * @param registrationFacade the registration facade used by the newly created listener service.
   * @param handlerFactory the channel handler factory used by the newly created listener service.
   * 
   * @throws NullPointerException if {@code listeningChannel==null || registrationFacade == null || 
   *         handlerFactory == null }  
   * @throws IllegalArgumentException if {@code listeningChannel} is not open or in non-blocking
   *         mode.
   */
  public BasicListenerService(ServerSocketChannel listeningChannel,
      RegistrationFacade registrationFacade, ChannelHandler.Factory handlerFactory) {

    super(listeningChannel, registrationFacade, handlerFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void listen() throws InterruptedException, IOException, Exception {

    SocketChannel channel = this.listeningChannel.accept();
    channel.configureBlocking(false);
    ChannelHandler handler = handlerFactory.getNewChannelHandlerService(channel);
    handler.setChannelInterestOps(SelectionKey.OP_READ);
    this.registrationFacade.register(handler);

    System.out.println("*** Client " + channel.socket().getInetAddress() + ":"
        + channel.socket().getPort() + " connected ***");
  }
}
