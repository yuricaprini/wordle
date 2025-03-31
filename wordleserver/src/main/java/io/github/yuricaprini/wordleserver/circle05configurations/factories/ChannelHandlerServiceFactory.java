package io.github.yuricaprini.wordleserver.circle05configurations.factories;

import java.nio.channels.SelectableChannel;
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleserver.circle04frameworks.ChannelHandler;
import io.github.yuricaprini.wordleserver.circle04frameworks.implementations.ClientChannelHandler;
import io.github.yuricaprini.wordleserver.circle05configurations.AppConfig;

public class ChannelHandlerServiceFactory implements ChannelHandler.Factory {

  @Override
  public ChannelHandler getNewChannelHandlerService(SelectableChannel channel) {
    return new ClientChannelHandler(channel, AppConfig.getNewClientRequestHandler(),
        ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(WordleRequest.MAX_SIZE + 1),
        ProtocolFactoryProvider.newOutputQueueFactory().createOutputQueue());
  }

}
