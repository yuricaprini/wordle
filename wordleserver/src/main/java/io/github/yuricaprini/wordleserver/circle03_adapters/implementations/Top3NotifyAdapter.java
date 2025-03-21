package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.Top3NotificationRemoteService;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.Top3NotifyEventRemote;
import io.github.yuricaprini.wordleserver.circle02usecases.Top3Notifier;

public class Top3NotifyAdapter implements Top3NotificationRemoteService, Top3Notifier {

  private static volatile Top3NotifyAdapter instance;
  private List<Top3NotifyEventRemote> clientsRemote;

  public static Top3NotifyAdapter getInstance() {
    if (instance == null) {
      synchronized (Top3NotifyAdapter.class) {
        if (instance == null)
          instance = new Top3NotifyAdapter();
      }
    }
    return instance;
  }

  private Top3NotifyAdapter() {
    this.clientsRemote = new ArrayList<Top3NotifyEventRemote>();
  }

  @Override
  public synchronized void notify(PlayerDTO[] top3) {
    Iterator<Top3NotifyEventRemote> iterator = clientsRemote.iterator();
    while (iterator.hasNext()) {
      Top3NotifyEventRemote clientRemote = iterator.next();
      try {
        clientRemote.notify(top3);
      } catch (RemoteException e) {
        iterator.remove();
      }
    }
  }

  @Override
  public synchronized void subscribe(Top3NotifyEventRemote clientRemote, AuthToken token)
      throws RemoteException, InvalidTokenException {

    token.validate();
    if (!clientsRemote.contains(clientRemote))
      clientsRemote.add(clientRemote);
  }

  @Override
  public synchronized void unsubscribe(Top3NotifyEventRemote stub) {
    clientsRemote.remove(stub);
  }
}
