package io.github.yuricaprini.wordleprotocol.remoteinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;

public interface Top3NotificationRemoteService extends Remote {

  public void subscribe(Top3NotifyEventRemote clientRemote, AuthToken token)
      throws RemoteException, InvalidTokenException;

  public void unsubscribe(Top3NotifyEventRemote stub) throws RemoteException;
}
