package io.github.yuricaprini.wordleserver.circle04frameworks.implementations;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleserver.circle03_adapters.ClientChannelFacade;
import io.github.yuricaprini.wordleserver.circle03_adapters.ClientRequestHandler;
import io.github.yuricaprini.wordleserver.circle04frameworks.ChannelHandler;

/**
 * A {@code ClientChannelHandler} is an implementation of {@code ChannelHandler} which handles the
 * channel resulting from the acceptance of a connection request made by a Wordle client.
 * <p>
 * After performing I/O operations (read/write) on its handled channel, this channel handler make
 * use of a {@link ClientRequestHandler} to parse and fullfill the incoming requests from client 
 * and sets the channel {@code interest-operation set} consequentially.
 * 
 * @author Yuri Caprini
 */
public class ClientChannelHandler extends ChannelHandler implements ClientChannelFacade {

  private InputQueue inputQueue;
  private OutputQueue outputQueue;
  private SocketChannel clientChannel;
  private ClientRequestHandler requestHandler;
  private boolean shutDownByClient;
  private boolean dead;

  /**
  * Constructs a new {@code ClientChannelHandler}.
  * 
  * @param channel          the client channel handled by this client channel handler.
  * @param requestHandler   the handler used to fulfill incoming requests from {@code channel}.
  * @param inputQueue       the queue for managing incoming data.
  * @param outputQueue      the queue for managing outgoing data.
  *
  * @throws NullPointerException if {@code channel==null || requestHandler==null || 
  * inputQueue == null || outputQueue == null} 
  * @throws IllegalArgumentException if {@code channel} is in blocking mode
  */
  public ClientChannelHandler(SelectableChannel channel, ClientRequestHandler requestHandler,
      InputQueue inputQueue, OutputQueue outputQueue) {

    super(channel);
    this.inputQueue = Objects.requireNonNull(inputQueue);
    this.outputQueue = Objects.requireNonNull(outputQueue);
    this.clientChannel = (SocketChannel) Objects.requireNonNull(channel);
    this.requestHandler = Objects.requireNonNull(requestHandler);
    this.shutDownByClient = false;
    this.dead = false;
  }

  @Override
  public void handleChannel() throws IOException {

    if (isChannelReadyToWrite())
      outputQueue.drainTo(clientChannel);

    if (isChannelReadyToRead())
      if (inputQueue.fillFrom(clientChannel) == -1)
        shutDownByClient = true;

    try {
      requestHandler.handleRequest(this);
    } catch (Exception e) { // bad request or too large request or unlikely exceptions
      e.printStackTrace();
      this.die();
    }

    setChannelInterestedToRead(!shutDownByClient);

    setChannelInterestedToWrite(!outputQueue.isEmpty());

    if (shutDownByClient && outputQueue.isEmpty()) // nothing to read and nothing to write
      this.die();
  }

  @Override
  public SelectableChannel getChannel() {
    return channel;
  }

  @Override
  public void setChannelReadyOps(int readyOps) {
    this.channelReadyOps = readyOps;
  }

  @Override
  public void setChannelInterestOps(int interestOps) {
    this.channelInterestOps = interestOps;
  }

  @Override
  public int getChannelInterestOps() {
    return this.channelInterestOps;
  }

  @Override
  protected void die() {
    this.dead = true;
    System.out.println("*** Client " + clientChannel.socket().getInetAddress() + ":"
        + clientChannel.socket().getPort() + " disconnected ***");
  }

  @Override
  public boolean isDead() {
    return this.dead;
  }

  @Override
  public InputQueue getInputQueue() {
    return inputQueue;
  }

  @Override
  public OutputQueue getOutputQueue() {
    return outputQueue;
  }

  /**
   * Checks the handled channel {@code ready-operation set} to know if it's ready to write.
   * 
   * @return {@code true} if the handled channel is ready to write, {@code false} otherwise.
   */
  private boolean isChannelReadyToWrite() {
    return (this.channelReadyOps & SelectionKey.OP_WRITE) != 0;
  }

  /**
   * Checks the handled channel {@code ready-operation set} to know if it's ready to read.
   * 
   * @return {@code true} if the handled channel is ready to read, {@code false} otherwise.
   */
  private boolean isChannelReadyToRead() {
    return (this.channelReadyOps & SelectionKey.OP_READ) != 0;
  }

  /**
   * Modifies the handled channel {@code interest-operation set} marking the channel as ready to
   * write.
   * 
   * @param isInterestedToWrite if {@code true} the handled channel is marked as ready to write.
   */
  private void setChannelInterestedToWrite(boolean isInterestedToWrite) {
    if (isInterestedToWrite)
      modifyInterestOps(SelectionKey.OP_WRITE, 0);
    else
      modifyInterestOps(0, SelectionKey.OP_WRITE);
  }

  /**
   * Modifies the handled channel {@code interest-operation set} marking the channel as ready to
   * read.
   * 
   * @param isInterestedToRead if {@code true} the handled channel ise marked as ready to read.
   */
  private void setChannelInterestedToRead(boolean isInterestedToRead) {
    if (isInterestedToRead)
      modifyInterestOps(SelectionKey.OP_READ, 0);
    else
      modifyInterestOps(0, SelectionKey.OP_READ);
  }

  /**
   * Modifies the handled channel {@code interest-operation set} marking the channel as interested
   * to perform the {@code opsToSet} and uninterested to perform the {@code opsToReset}.
   * 
   * @param opsToSet the set of I/O operations to insert in the interest-operation set.
   * @param opsToReset the set of I/O operations to remove from the interest-operation set.
   */
  private void modifyInterestOps(int opsToSet, int opsToReset) {
    this.channelInterestOps = (this.channelInterestOps | opsToSet) & (~opsToReset);
  }
}
