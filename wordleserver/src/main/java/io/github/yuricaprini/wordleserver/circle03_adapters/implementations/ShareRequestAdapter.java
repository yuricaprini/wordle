package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import io.github.yuricaprini.wordleprotocol.dtos.SharedGameResultDTO;
import io.github.yuricaprini.wordleserver.circle02usecases.GameResultSharer;

public class ShareRequestAdapter implements GameResultSharer {

  private String multicastAddr;
  private int multicastPort;

  public ShareRequestAdapter(String multicastAddr, int multicastPort) {
    this.multicastAddr = multicastAddr;
    this.multicastPort = multicastPort;
  }

  @Override
  public void share(SharedGameResultDTO sharedGameResultDTO) throws Exception {

    try (MulticastSocket multicastSocket = new MulticastSocket()) {

      byte[] data = new Gson().toJson(sharedGameResultDTO).getBytes(StandardCharsets.UTF_8);
      InetAddress multicastGroup = InetAddress.getByName(multicastAddr);
      DatagramPacket packet = new DatagramPacket(data, data.length, multicastGroup, multicastPort);
      multicastSocket.send(packet);
    }
  }
}
