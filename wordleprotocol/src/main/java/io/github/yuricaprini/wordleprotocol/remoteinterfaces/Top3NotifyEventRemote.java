package io.github.yuricaprini.wordleprotocol.remoteinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;

public interface Top3NotifyEventRemote extends Remote {

  public void notify(PlayerDTO[] top3) throws RemoteException;
}
