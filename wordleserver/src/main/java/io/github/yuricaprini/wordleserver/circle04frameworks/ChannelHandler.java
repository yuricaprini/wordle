package io.github.yuricaprini.wordleserver.circle04frameworks;

import java.nio.channels.SelectableChannel;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A {@code ChannelHandler} is a {@link Callable} called by a {@link DispatcherService} in order to 
 * perform low-level I/O operations on the non-blocking {@link SelectableChannel} to which is
 * paired, in accordance with the channel readiness to perform them and with the application 
 * business rules.
 * <p>
 * A channel handler is invoked at the end of a dispatcher selection-cycle, when its paired channel
 * has been selected as ready to perform some I/O operation.
 * <p>
 * Before execution a channel handler gets a copy of the channel {@code ready-operation set} and
 * {@code interest-operation set}.
 * <p>
 * When executed a channel handler performs I/O operations on its paired channel according to the
 * copy of the channel {@code ready-operation set} and to the application business rules.
 * <p>
 * Prior to termination, a channel handler updates its copy of the channel 
 * {@code interest-operation set}: in this way the dispatcher knows what are the next I/O 
 * operations the channel should be selected for, when ready to perform them.
 * <p>
 * After termination a channel handler can be called again in order to handle new I\O operations.
 * This can be done until the channel handler dies due to an exception or to the natural cessation * of communication on the channel (it is no longer possible to read from the channel and
 * everything that should have been sent has been sent).
 * <p>
 * If a channel handler is called after its death, nothing is done.
 * 
 * @author Yuri Caprini
 */
public abstract class ChannelHandler implements Callable<ChannelHandler> {

  protected SelectableChannel channel;
  protected int channelReadyOps;
  protected int channelInterestOps;

  /**
   * Abstract {@code ChannelHandler} constructor. Its role is to enforce a minimum setup for
   * handlers constructed by subclasses. Each newly constructed handler must be paired with a 
   * channel at construction time.
   * 
   * @param channel the non-blocking selectable channel paired with this channel handler,
   * @throws NullPointerException if {@code channel==null}
   * @throws IllegalArgumentException if {@code channel} is in blocking mode
   * 
   */
  public ChannelHandler(SelectableChannel channel) {

    if (Objects.requireNonNull(channel).isBlocking())
      throw new IllegalArgumentException();

    this.channel = channel;
    this.channelReadyOps = 0;
    this.channelInterestOps = 0;
  }

  /**
   * Runs the handling-cycle of the channel coupled with this channel handler, then returns this
   * channel handler. If an exception occurs the channel handler is marked as dead. If a channel
   * handler is dead, it's execution returns immediately.
   * 
   * @throws Exception if an error occurs during the execution of this channel handler.
   * @return this channel handler.
   */
  public ChannelHandler call() throws Exception {

    if (isDead())
      return this;

    try {
      this.handleChannel();

    } catch (Exception e) {
      this.die();
      throw e;
    }

    return this;
  }

  /**
   * Performs the handling-cycle of the channel coupled to this channel handler:
   * <ul>
   * 
   * <li>Checks the channel {@code ready-operation set} to know which I/O operations the channel is
   * ready to perform.
   * 
   * <li>Performs these operations in accordance with the application business rules (i.e. protocol
   * rules). Each I/O operation (i.e. read/write ) is executed only once, hence a succession of
   * operations requires a succession of handler executions.
   * 
   * <li>Modifies the channel {@code interest-operation set} in accordance with the application
   * business rules in order to tell the caller for which I/O operations the channel should be
   * selected for, when ready to perform them.
   * </ul>
   * 
   * @throws Exception if an error occurs while handling.
   */
  public abstract void handleChannel() throws Exception;

  /**
   * Sets the handled channel {@code ready-operation set}.
   * 
   * @param readyOps the handled channel {@code ready-operation set}
   */
  public abstract void setChannelReadyOps(int readyOps);

  /**
   * Sets the handled channel {@code interest-operation set}.
   * 
   * @param interestOps the handled channel {@code interest-operation set}
   */
  public abstract void setChannelInterestOps(int interestOps);

  /**
   * Gets the handled channel {@code interest-operation set}.
   * 
   * @return the handled channel {@code interest-operation set}
   */
  public abstract int getChannelInterestOps();

  /**
   * Gets the channel handled by this channel handler.
   * 
   * @return the channel handled by this channel handler
   */
  public abstract SelectableChannel getChannel();

  /**
   * Returns {@code true} if the channel handler is dead, {@code false} otherwise.
   * 
   * @return {@code true} if the channel handler is dead, {@code false} otherwise.
   */
  public abstract boolean isDead();

  /**
   * Marks this channel handler as dead.
   */
  protected abstract void die();

  /**
   * Abstract factory for {@code ChannelHandler} objects
   * 
   * @author Yuri Caprini
   *
   */
  public interface Factory {

    /**
     * Creates a new instance of {@code ChannelHandler}
     * 
     * @param channel the channel paired with this channel handler.
     * @throws NullPointerException if {@code channel==null}
     * @throws IllegalArgumentException if {@code channel} is in blocking mode
     * @return a new istance of {@code ChannelHandler}
     */
    public ChannelHandler getNewChannelHandlerService(SelectableChannel channel);
  }
}
