package io.github.yuricaprini.wordleserver.circle03_adapters;

public interface ServerStatus {

  public boolean load() throws Exception;

  public boolean store() throws Exception;
}
