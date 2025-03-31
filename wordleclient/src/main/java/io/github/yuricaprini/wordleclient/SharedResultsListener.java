package io.github.yuricaprini.wordleclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.yuricaprini.wordleprotocol.dtos.SharedGameResultDTO;

public class SharedResultsListener extends Thread {

  private static final int PACKET_LEN = 1024 * 2;
  private ClientConfiguration config;
  private MulticastSocket multicastSocket;
  private BlockingQueue<SharedGameResultDTO> sharedResultsQueue;

  public SharedResultsListener(ClientConfiguration config,
      BlockingQueue<SharedGameResultDTO> sharedResultsQueue) {
    this.config = config;
    this.sharedResultsQueue = sharedResultsQueue;
  }

  @Override
  public void run() {
    try {

      boolean onAllInterfaces = true;
      InetAddress group = InetAddress.getByName(config.multicastGroup);
      try {
        multicastSocket = new MulticastSocket(config.multicastPort);
        multicastSocket.joinGroup(group);
      } catch (Exception e) {
        onAllInterfaces = false;
      }

      if (onAllInterfaces == false) {
        NetworkInterface networkInterface = NetworkInterface.getByName(config.multicastIF);
        multicastSocket = new MulticastSocket(new InetSocketAddress(group, config.multicastPort));
        multicastSocket.joinGroup(new InetSocketAddress(group, config.multicastPort),
            networkInterface);
      }

      while (!Thread.interrupted()) {
        DatagramPacket packet = new DatagramPacket(new byte[PACKET_LEN], PACKET_LEN);
        multicastSocket.receive(packet);

        try {
          SharedGameResultDTO sharedGameResultDTO =
              new Gson().fromJson(new String(packet.getData(), StandardCharsets.UTF_8).trim(),
                  SharedGameResultDTO.class);

          if (!sharedResultsQueue.offer(sharedGameResultDTO)) {
            sharedResultsQueue.poll(); // discard oldest
            sharedResultsQueue.offer(sharedGameResultDTO);
          }
        } catch (JsonSyntaxException e) {
          continue; // discard wrong packets 
        }
      }
    } catch (IOException e) {
    } finally {
      multicastSocket.close();
    }
  }

  public void terminate() {
    multicastSocket.close();
  }
}
