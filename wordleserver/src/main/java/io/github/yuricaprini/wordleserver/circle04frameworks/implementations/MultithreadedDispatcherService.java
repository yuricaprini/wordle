package io.github.yuricaprini.wordleserver.circle04frameworks.implementations;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import io.github.yuricaprini.wordleserver.circle04frameworks.ChannelHandler;
import io.github.yuricaprini.wordleserver.circle04frameworks.DispatcherService;

/**
 * A {@code MultithreadedDispatcher} is an implementation of {@code Dispatcher} interface. It 
 * employs a thread pool to execute channel handlers and employs a blocking queue to ensure 
 * synchronization between the dispatcher and these handlers.
 * 
 * @author Yuri Caprini
 */
public class MultithreadedDispatcherService extends DispatcherService {

  private ExecutorService threadPool;
  private BlockingQueue<ChannelHandler> eventQueue;

  /**
   * Constructs a new {@code MultithreadedDispatcherService} which will multiplex the channels 
   * registered to it with the given {@code selector}, will executes the channel handlers with the
   * specified {@code threadPool} and will synchronize with them through the given 
   * {@code eventQueue}.
   * 
   * @param selector the selector used by this dispatcher to multiplex the registered channels.
   * @param threadPool the thread pool used by this dispatcher to executes the channel handlers.
   * @param eventQueue the queue used to provide synchronization between dispatcher and 
   * channel handlers
   * @throws NullPointerException if {@code selector==null || threadPool==null || eventQueue==null}
   * @throws IllegalArgumentException if {@code selector} is not open.
   */
  public MultithreadedDispatcherService(Selector selector, ExecutorService threadPool,
      BlockingQueue<ChannelHandler> eventQueue) {

    super(selector);

    this.threadPool = Objects.requireNonNull(threadPool);
    this.eventQueue = Objects.requireNonNull(eventQueue);
  }

  @Override
  public void dispatch() throws IOException {

    this.selector.select(); // wakes up on wakeup method or interruption

    checkEventQueue(); // synchronization point between channel handlers and dispatcher

    Set<SelectionKey> selectedKeys = this.selector.selectedKeys();

    for (SelectionKey key : selectedKeys) {
      if (key.isValid()) {

        ChannelHandler channelHandler = (ChannelHandler) key.attachment();

        channelHandler.setChannelReadyOps(key.readyOps());
        channelHandler.setChannelInterestOps(key.interestOps());
        key.interestOps(0); // stops selection for this channel

        this.threadPool.execute(new FutureTask<ChannelHandler>(channelHandler) {

          @Override
          protected void done() {

            try {
              this.get(); // ignores returned value

            } catch (ExecutionException ee) {
              ee.printStackTrace();

            } catch (InterruptedException ie) {
              // branch never reached since done() is called after call() completion.
              // This means that get() does not have to wait and can not be interrupted 
              // while waiting, and it can't throw this exception.
            }

            try {
              eventQueue.put(channelHandler);
            } catch (InterruptedException e) {
              // branch never reached: no one sends interruptions to channel handlers.
            }

            selector.wakeup();
          }
        });
      }
    }

    selectedKeys.clear();
  }

  @Override
  protected void cleanup() throws IOException, InterruptedException {

    this.threadPool.shutdown(); // prevents new handlers from being added to execution waiting queue
    this.threadPool.awaitTermination(5, TimeUnit.SECONDS); // leaves a chance to handlers to finish
    this.threadPool.shutdownNow(); // waiting handlers are no more processed, running ones terminate
  }

  @Override
  public void register(ChannelHandler channelHandler) throws InterruptedException, IOException {

    if (Objects.requireNonNull(channelHandler).getChannel().isBlocking())
      throw new IllegalArgumentException();

    eventQueue.put(channelHandler); // actual registration is done when checkEventQueue() is called
    selector.wakeup();
  }

  private void checkEventQueue() throws ClosedChannelException {

    ChannelHandler channelHandler;
    while ((channelHandler = eventQueue.poll()) != null) {

      SelectableChannel channel = channelHandler.getChannel();

      if (channel.keyFor(selector) == null)
        // actual registration of this channel to this selector
        channel.register(selector, channelHandler.getChannelInterestOps(), channelHandler);

      if (channelHandler.isDead()) {
        // cancel selection
        channel.keyFor(selector).cancel();
        try {
          channel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else
        // resume selection
        channel.keyFor(selector).interestOps(channelHandler.getChannelInterestOps());
    }
  }
}
