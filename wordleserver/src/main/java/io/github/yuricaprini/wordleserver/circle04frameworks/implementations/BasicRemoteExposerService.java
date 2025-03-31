package io.github.yuricaprini.wordleserver.circle04frameworks.implementations;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;
import io.github.yuricaprini.wordleserver.circle04frameworks.RemoteExposerService;

/**
 * A {@code BasicRemoteExposerService} is a basic implementation of {@code RemoteExposerService}. 
 * 
 * @author Yuri Caprini
 */
public class BasicRemoteExposerService extends RemoteExposerService {

  private int registryPort;

  /**
   * Constructs a new {@code BasicRemoteExposerService} that exposes {@code remoteObjs} references * on the {@link Registry} accepting request on the provided {@code registryPort}.
   * 
   * @param remoteObjs the remote objects to be exposed. 
   * @param registryPort the RMI registry port.
   */
  public BasicRemoteExposerService(Hashtable<String, Remote> remoteObjs, int registryPort) {
    super(remoteObjs);
    this.registryPort = registryPort;
  }

  @Override
  protected void exposeAll() throws Exception {
    Registry registry = LocateRegistry.createRegistry(registryPort);
    for (Map.Entry<String, Remote> entry : remoteObjs.entrySet()) {
      registry.rebind(entry.getKey(), UnicastRemoteObject.exportObject(entry.getValue(), 0));
    }
  }

  @Override
  protected void unexposeAll() throws Exception {
    for (Map.Entry<String, Remote> entry : remoteObjs.entrySet()) {
      LocateRegistry.getRegistry(registryPort).unbind(entry.getKey());
      UnicastRemoteObject.unexportObject(entry.getValue(), true);
    }
  }
}
