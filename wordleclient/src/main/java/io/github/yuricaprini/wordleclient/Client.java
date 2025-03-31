package io.github.yuricaprini.wordleclient;

import static io.github.yuricaprini.wordleprotocol.messages.WordleResponse.Type.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi.Color;
import com.google.gson.Gson;
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.SecretWordDTO;
import io.github.yuricaprini.wordleprotocol.dtos.SharedGameResultDTO;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadResponseException;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseTooLargeException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.Top3NotificationRemoteService;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.Top3NotifyEventRemote;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.UserRegistrationRemoteService;

/**
 * A {@code Client} is responsible for handling user input, and sending the corresponding requests
 * to the remote server.
 */
public class Client implements Top3NotifyEventRemote {

  static {
    AnsiConsole.systemInstall();
  }
  private static final String GREEN = Ansi.ansi().fg(Color.GREEN).bg(Color.BLACK).toString();
  private static final String YELLOW = Ansi.ansi().fg(Color.YELLOW).bg(Color.BLACK).toString();
  private static final String RED = Ansi.ansi().fg(Color.RED).bg(Color.BLACK).toString();
  private static final String RESET = Ansi.ansi().reset().toString();
  private static final int MAX_ATTEMPTS = 12;
  private static final char GREEN_SIMBOL = '+';
  private static final char YELLOW_SIMBOL = '?';
  private static final char RED_SIMBOL = 'X';

  private ClientConfiguration config;
  private ResourceBundle appStrings;
  private OutputQueue outputQueue;
  private InputQueue inputQueue;
  private WordleRequest.Factory requestFactory;
  private WordleResponse.Factory responseFactory;
  private boolean shutdown;

  private AuthToken authToken;
  private String loggedAs;
  private Top3NotifyEventRemote stub;
  private Top3NotificationRemoteService notificationService;
  private UserRegistrationRemoteService registrationService;

  private SocketChannel clientChannel;
  private volatile PlayerDTO[] top3;
  private BlockingQueue<SharedGameResultDTO> sharedResults;
  private SharedResultsListener sharedResultsThread;

  public Client(ResourceBundle appStrings) {

    this.appStrings = appStrings;
    this.outputQueue = ProtocolFactoryProvider.newOutputQueueFactory().createOutputQueue();
    this.inputQueue = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(1024);
    this.requestFactory = ProtocolFactoryProvider.newWordleRequestFactory();
    this.responseFactory = ProtocolFactoryProvider.newWordleResponseFactory();
    this.sharedResults = new ArrayBlockingQueue<SharedGameResultDTO>(100);
  }

  /**
  * Loads the client configuration from a JSON file.
  * 
  * @param isCustomConfig true if the name of a custom configuration file is provided, false ow
  * @param configName the name of the configuration file
  * @throws NullPointerException if the configuration file can not be found
  * @throws IOException if an I/O error occurs while reading the configuration file
  */
  public void loadConfiguration(boolean isCustomConfig, String configName)
      throws NullPointerException, IOException {

    if (isCustomConfig)
      try (Reader reader = Files.newBufferedReader(Paths.get(configName));) {
        this.config = new Gson().fromJson(reader, ClientConfiguration.class);
      }
    else {
      try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(configName);
          Reader reader = new BufferedReader(new InputStreamReader(is));) {
        this.config = new Gson().fromJson(reader, ClientConfiguration.class);
      }
    }
  }

  /**
  * Executes an interaction loop reading user input, interpreting the commands, sending the
  * corresponding requests to the remote server, and receiving its responses.
  *
  * @throws AccessException if a security issue occurs while accessing the remote service
  * @throws RemoteException if a communication-related issue occurs during the remote method call
  * @throws NotBoundException if the specified name is not currently bound
  * @throws IOException if not able to open the client channel
   * @throws InvalidTokenException
  * @throws IllegalStateException if client configuration is not loaded when this method is called.
  */
  public void executeInteractionLoop()
      throws AccessException, RemoteException, NotBoundException, IOException {

    if (config == null)
      throw new IllegalStateException();

    try (Scanner scanner = new Scanner(System.in)) {

      this.clientChannel = SocketChannel.open();
      clientChannel.connect(new InetSocketAddress(config.serverAddress, config.serverPort));

      printOutWordleBanner();
      printlnOutValueOf("WELCOME");

      while (!shutdown) {

        printlnOut();

        if (loggedAs != null)
          printOut(loggedAs + " > ");
        printOutValueOf("COMMAND_LABEL");

        String input = scanner.nextLine();

        printlnOutSeparator();

        String[] tokens = input.split(" ");
        String command = tokens[0];
        String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (command.equals(appStrings.getString("COMMAND_REGISTER"))) {

          if (commandArgs.length != 2)
            printlnOutValueOf("ERR_REGISTER_N_ARGS");
          else {
            if (authToken != null) {
              printlnOutValueOf("ERR_REGISTER_LOGOUTFIRST");
            } else {

              if (registrationService == null)
                registrationService = (UserRegistrationRemoteService) LocateRegistry
                    .getRegistry(config.registryHost, config.registryPort)
                    .lookup(UserRegistrationRemoteService.class.getSimpleName());

              switch (registrationService.registerUser(commandArgs[0], commandArgs[1])) {
                case OK:
                  printlnOutValueOf("OUT_REGISTER_OK");
                  break;
                case USERNAME_SHORT:
                  printlnOutValueOf("ERR_REGISTER_USERNAME_SHORT");
                  break;
                case USERNAME_LONG:
                  printlnOutValueOf("ERR_REGISTER_USERNAME_LONG");
                  break;
                case USERNAME_SPACE:
                  printlnOutValueOf("ERR_REGISTER_USERNAME_SPACE");
                  break;
                case PASSWORD_SHORT:
                  printlnOutValueOf("ERR_REGISTER_PASSWORD_SHORT");
                  break;
                case PASSWORD_LONG:
                  printlnOutValueOf("ERR_REGISTER_PASSWORD_LONG");
                  break;
                case PASSWORD_SPACE:
                  printlnOutValueOf("ERR_REGISTER_PASSWORD_SPACE");
                  break;
                case PASSWORD_NO_DIGIT:
                  printlnOutValueOf("ERR_REGISTER_PASSWORD_NO_DIGIT");
                  break;
                case PASSWORD_NO_UC:
                  printlnOutValueOf("ERR_REGISTER_PASSWORD_NO_UC");
                  break;
                case ALREADY_REGISTERED:
                  printlnOutValueOf("ERR_REGISTER_ALREADY_REGISTERED");
                  break;
                default: //handled, but never occurs
                  printlnOutValueOf("ERR_REGISTER_UNKNOWN_OUTCOME");
                  break;
              }
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_LOGIN"))) {

          if (commandArgs.length != 2)
            printlnOutValueOf("ERR_LOGIN_N_ARGS");
          else if (authToken != null) {
            printlnOutValueOf("ERR_LOGIN_ALREADY");
          } else {

            WordleResponse loginResponse = sendRequest(requestFactory
                .createLoginRequest(CredentialsDTO.newInstance(commandArgs[0], commandArgs[1])));

            if (loginResponse.getType() == LOGIN_OK) {

              authToken = loginResponse.getAuthToken();
              loggedAs = commandArgs[0];

              if (notificationService == null) {
                notificationService = (Top3NotificationRemoteService) LocateRegistry
                    .getRegistry(config.registryHost, config.registryPort)
                    .lookup(Top3NotificationRemoteService.class.getSimpleName());

                stub = (Top3NotifyEventRemote) UnicastRemoteObject.exportObject(this, 0);
                try {
                  notificationService.subscribe(stub, authToken);
                } catch (InvalidTokenException e) {
                  e.printStackTrace(); // handled but never happens, print just for debug... 
                }
              }

              if (sharedResultsThread == null) {
                sharedResultsThread = new SharedResultsListener(config, sharedResults);
                sharedResultsThread.start();
              }

              printlnOutValueOf("OUT_LOGIN_OK");

            } else if (loginResponse.getType() == LOGIN_NO) {

              switch (loginResponse.getErrorCode()) {
                case USERNAME_SHORT:
                  printlnOutValueOf("ERR_LOGIN_USERNAME_SHORT");
                  break;
                case USERNAME_LONG:
                  printlnOutValueOf("ERR_LOGIN_USERNAME_LONG");
                  break;
                case USERNAME_SPACE: //handled, but it never occurs due to client-side validation.
                  printlnOutValueOf("ERR_LOGIN_USERNAME_SPACE");
                  break;
                case PASSWORD_SHORT:
                  printlnOutValueOf("ERR_LOGIN_PASSWORD_SHORT");
                  break;
                case PASSWORD_LONG:
                  printlnOutValueOf("ERR_LOGIN_PASSWORD_LONG");
                  break;
                case PASSWORD_SPACE: //handled, but it never occurs due to client-side validation.
                  printlnOutValueOf("ERR_LOGIN_PASSWORD_SPACE");
                  break;
                case PASSWORD_NO_DIGIT:
                  printlnOutValueOf("ERR_LOGIN_PASSWORD_NO_DIGIT");
                  break;
                case PASSWORD_NO_UC:
                  printlnOutValueOf("ERR_LOGIN_PASSWORD_NO_UC");
                  break;
                case NOT_REGISTERED_USER:
                  printlnOutValueOf("ERR_LOGIN_NOT_REGISTERED_USER");
                  break;
                case INVALID_CREDENTIALS:
                  printlnOutValueOf("ERR_LOGIN_INVALID_CREDENTIALS");
                  break;
                default: //handled, but server should never send other error codes for login
                  printlnOutValueOf("ERR_LOGIN_UNKNOWN_OUTCOME");
                  break;
              }
            } else //handled, but server should never send other responses
              printlnErrValueOf("ERR_INVALID_RESPONSE");
          }

        } else if (command.equals(appStrings.getString("COMMAND_LOGOUT"))) {

          if (commandArgs.length != 0)
            printlnOutValueOf("ERR_LOGOUT_N_ARGS");
          else {
            if (authToken == null) {
              printlnOutValueOf("ERR_LOGOUT_NOTLOGGED");
            } else {

              notificationService.unsubscribe(stub);
              sharedResultsThread.terminate();
              sharedResultsThread = null;
              authToken = null;
              loggedAs = null;
              printlnOutValueOf("OUT_LOGOUT_OK");
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_PLAYWORDLE"))) {

          if (commandArgs.length != 0) {
            printlnOutValueOf("ERR_PLAYWORDLE_N_ARGS");
          } else {
            if (authToken == null)
              printlnOutValueOf("ERR_PLAYWORDLE_UNAUTHORIZED");
            else {

              WordleResponse playwordleResponse =
                  sendRequest(requestFactory.createPlayWordleRequest(authToken));

              if (playwordleResponse.getType() == PLAYWORDLE_OK) {

                if (playwordleResponse.getCluesDTO() == null)
                  printlnOutValueOf("OUT_PLAYWORDLE_GAMESTARTED");
                else {
                  printlnOutValueOf("OUT_PLAYWORDLE_GAMEINPROGRESS");
                  printlnOutClues(playwordleResponse.getCluesDTO());
                }

              } else if (playwordleResponse.getType() == PLAYWORDLE_NO) {

                switch (playwordleResponse.getErrorCode()) {
                  case INVALID_AUTHTOKEN:
                    printlnOutValueOf("ERR_PLAYWORDLE_UNAUTHORIZED");
                    break;
                  case GAME_ALREADY_PLAYED:
                    printlnOutValueOf("ERR_PLAYWORDLE_GAMEPLAYED");
                    break;
                  default: //handled, but server should never send other error codes 
                    printlnOutValueOf("ERR_PLAYWORDLE_UNKNOWN_OUTCOME");
                    break;
                }
              } else//handled, but server should never send other responses
                printlnErrValueOf("ERR_INVALID_RESPONSE");
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_SENDWORD"))) {

          if (commandArgs.length != 1) {
            printlnOutValueOf("ERR_SENDWORD_N_ARGS");
          } else {
            if (authToken == null) {
              printlnOutValueOf("ERR_SENDWORD_UNAUTHORIZED");
            } else {

              WordleResponse sendwordResponse = sendRequest(requestFactory
                  .createSendWordRequest(authToken, WordDTO.newInstance(commandArgs[0])));

              if (sendwordResponse.getType() == SENDWORD_OK) {

                int attemptsLeft =
                    MAX_ATTEMPTS - sendwordResponse.getGameStateDTO().getCluesDTO().length;
                if (attemptsLeft > 0) {
                  if (isGameWon(sendwordResponse.getGameStateDTO().getCluesDTO())) {
                    printlnOutValueOf("OUT_SENDWORD_GAMEOVER_WON");
                    printlnOutSolution(sendwordResponse.getGameStateDTO().getSecretWordDTO());
                    printlnOutClues(sendwordResponse.getGameStateDTO().getCluesDTO());
                    printlnOutStats(sendwordResponse.getGameStateDTO().getStatsDTO());
                  } else {
                    printOutValueOf("OUT_SENDWORD_ATTEMPTS_LEFT");
                    printlnOut(attemptsLeft);
                    printlnOutClues(sendwordResponse.getGameStateDTO().getCluesDTO());
                  }

                } else {

                  if (isGameWon(sendwordResponse.getGameStateDTO().getCluesDTO()))
                    printlnOutValueOf("OUT_SENDWORD_GAMEOVER_WON");
                  else
                    printlnOutValueOf("OUT_SENDWORD_GAMEOVER_LOSE");

                  printlnOutSolution(sendwordResponse.getGameStateDTO().getSecretWordDTO());
                  printlnOutClues(sendwordResponse.getGameStateDTO().getCluesDTO());
                  printlnOutStats(sendwordResponse.getGameStateDTO().getStatsDTO());
                }



              } else if (sendwordResponse.getType() == SENDWORD_NO) {

                switch (sendwordResponse.getErrorCode()) {
                  case INVALID_AUTHTOKEN:
                    printlnOutValueOf("ERR_SENDWORD_UNAUTHORIZED");
                    break;
                  case ILLEGAL_WORD_LENGTH:
                    printlnOutValueOf("ERR_SENDWORD_ILLEGALWORDLEN");
                    break;
                  case ILLEGAL_WORD_VOCABULARY:
                    printlnOutValueOf("ERR_SENDWORD_ILLEGALWORDVOCABULARY");
                    break;
                  case GAME_NOT_STARTED:
                    printlnOutValueOf("ERR_SENDWORD_GAMENOTSTARTED");
                    break;
                  case GAME_ALREADY_PLAYED:
                    printlnOutValueOf("ERR_SENDWORD_GAMEPLAYED");
                    break;
                  default: //handled, but server should never send other error codes
                    printlnOutValueOf("ERR_SENDWORD_UNKNOWN_OUTCOME");
                    break;
                }
              } else//handled, but server should never send other responses
                printlnErrValueOf("ERR_INVALID_RESPONSE");
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_SHOWMESTATS"))) {

          if (commandArgs.length != 0) {
            printlnOutValueOf("ERR_SHOWMESTATS_N_ARGS");
          } else {
            if (authToken == null) {
              printlnOutValueOf("ERR_SHOWMESTATS_UNAUTHORIZED");
            } else {

              WordleResponse showMeStatsResponse =
                  sendRequest(requestFactory.createShowMeStatsRequest(authToken));

              if (showMeStatsResponse.getType() == SHOWMESTATS_OK) {

                printlnOutValueOf("OUT_SHOWMESTATS_OK");
                printlnOutStats(showMeStatsResponse.getStatsDTO());

              } else if (showMeStatsResponse.getType() == SHOWMESTATS_NO) {

                switch (showMeStatsResponse.getErrorCode()) {
                  case INVALID_AUTHTOKEN:
                    printlnOutValueOf("ERR_SHOWMESTATS_UNAUTHORIZED");
                    break;
                  default://handled, but server should never send other error codes
                    printlnOutValueOf("ERR_SHOWMESTATS_UNKNOWN_OUTCOME");
                    break;
                }
              } else//handled, but server should never send other responses
                printlnErrValueOf("ERR_INVALID_RESPONSE");
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_SHOWMERANKING"))) {

          if (commandArgs.length != 0) {
            printlnOutValueOf("ERR_SHOWMERANKING_N_ARGS");
          } else {
            if (authToken == null) {
              printlnOutValueOf("ERR_SHOWMERANKING_UNAUTHORIZED");
            } else {

              LinkedList<PlayerDTO> ranking = new LinkedList<PlayerDTO>();
              HashSet<PlayerDTO> seenPlayerDTOs = new HashSet<PlayerDTO>();

              WordleResponse showMeRankingResponse =
                  sendRequest(requestFactory.createShowMeRankingRequest(authToken, null));

              if (showMeRankingResponse.getType() == SHOWMERANKING_OK) {

                PlayerDTO[] playerDTOs = showMeRankingResponse.getPlayerDTOs();

                while (playerDTOs != null) {
                  for (PlayerDTO playerDTO : playerDTOs) {
                    if (seenPlayerDTOs.add(playerDTO))
                      ranking.addFirst(playerDTO);
                  }

                  showMeRankingResponse = sendRequest(requestFactory
                      .createShowMeRankingRequest(authToken, playerDTOs[playerDTOs.length - 1]));

                  if (showMeRankingResponse.getType() != SHOWMERANKING_OK)
                    break;
                  playerDTOs = showMeRankingResponse.getPlayerDTOs();
                }

                if (showMeRankingResponse.getType() == SHOWMERANKING_OK) {
                  printlnOutValueOf("OUT_SHOWMERANKING_OK");
                  printRanking(ranking);
                }

              } else if (showMeRankingResponse.getType() == SHOWMERANKING_NO) {

                switch (showMeRankingResponse.getErrorCode()) {
                  case INVALID_AUTHTOKEN:
                    printlnOutValueOf("ERR_SHOWMERANKING_UNAUTHORIZED");
                    break;
                  case INVALID_CURSOR: //handled, but never occurs if client send proper cursor
                    printlnOutValueOf("ERR_SHOWMERANKING_CURSOR");
                  default://handled, but server should never send other error codes
                    printlnOutValueOf("ERR_SHOWMERANKING_UNKNOWN_OUTCOME");
                    break;
                }
              } else//handled, but server should never send other responses
                printlnErrValueOf("ERR_INVALID_RESPONSE");
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_SHARE"))) {

          if (commandArgs.length != 0) {
            printlnOutValueOf("ERR_SHARE_N_ARGS");
          } else {
            if (authToken == null) {
              printlnOutValueOf("ERR_SHARE_UNAUTHORIZED");
            } else {

              WordleResponse shareResponse =
                  sendRequest(requestFactory.createShareRequest(authToken));

              if (shareResponse.getType() == SHARE_OK) {
                printlnOutValueOf("OUT_SHARE_OK");

              } else if (shareResponse.getType() == SHARE_NO) {

                switch (shareResponse.getErrorCode()) {
                  case INVALID_AUTHTOKEN:
                    printlnOutValueOf("ERR_SHARE_UNAUTHORIZED");
                    break;
                  case INTERNAL_ERROR:
                    printOutValueOf("ERR_SHARE_FAILED");
                    break;
                  case NO_GAMES_PLAYED:
                    printOutValueOf("ERR_SHARE_NOGAMES");
                    break;
                  default://handled, but server should never send other error codes
                    printlnOutValueOf("ERR_SHARE_UNKNOWN_OUTCOME");
                    break;
                }
              } else//handled, but server should never send other responses
                printlnErrValueOf("ERR_INVALID_RESPONSE");
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_SHOWMESHARINGS"))) {

          if (commandArgs.length != 0) {
            printlnOutValueOf("ERR_SHOWMESHARINGS_N_ARGS");
          } else {
            if (authToken == null)
              printlnOutValueOf("ERR_SHOWMESHARINGS_UNAUTHORIZED");
            else {

              if (sharedResults.isEmpty())
                printlnOutValueOf("ERR_SHOWMESHARINGS_NOSHARINGS");
              else
                printlnOutSharedResults();
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_SHOWMETOP3"))) {

          if (commandArgs.length != 0) {
            printlnOutValueOf("ERR_SHOWMETOP3_N_ARGS");
          } else {
            if (authToken == null)
              printlnOutValueOf("ERR_SHOWMETOP3_UNAUTHORIZED");
            else {
              if (top3 == null)
                printlnOutValueOf("ERR_NOTOP3");
              else {
                printlnOutValueOf("TOP3RANKINGS");
                printlnOutTop3();
              }
            }
          }

        } else if (command.equals(appStrings.getString("COMMAND_HELP"))) {
          printlnOutValueOf("HELP");
        } else if (command.equals(appStrings.getString("COMMAND_QUIT"))) {

          if (sharedResultsThread != null)
            sharedResultsThread.terminate();
          if (notificationService != null) {
            notificationService.unsubscribe(stub);
            UnicastRemoteObject.unexportObject(this, true);
          }
          shutdown = true;

        } else {
          printlnOutValueOf("ERR_UNKNOWN_COMMAND");
        }
      }
    } finally {
      clientChannel.close();
    }
  }

  @Override
  public void notify(PlayerDTO[] top3) {
    this.top3 = top3;
  }

  private WordleResponse sendRequest(WordleRequest request) throws IOException {

    request.serializeTo(outputQueue);
    outputQueue.drainTo(clientChannel);

    WordleResponse response = responseFactory.createEmptyResponse();
    while (!response.isFullyPopulated()) {
      inputQueue.fillFrom(clientChannel);
      try {
        response.populateFrom(inputQueue);
      } catch (ResponseTooLargeException | BadResponseException e) {
        e.printStackTrace(); //handled but never happens, leave print only for debug..
      }
    }
    return response;
  }

  private boolean isGameWon(ClueDTO[] cluesDTO) {
    ClueDTO lastClue = cluesDTO[cluesDTO.length - 1];
    String wordColors = lastClue.getWordColors();
    for (int i = 0; i < wordColors.length(); i++) {
      if (wordColors.charAt(i) != '+')
        return false;
    }
    return true;
  }

  private String colorizeWord(String word, String wordColors) {
    StringBuilder coloredWord = new StringBuilder();
    for (int i = 0; i < word.length(); i++) {
      char letter = word.charAt(i);
      char colorCode = wordColors.charAt(i);

      switch (colorCode) {
        case GREEN_SIMBOL:
          coloredWord.append(GREEN).append(letter).append(" ").append(RESET);
          break;
        case YELLOW_SIMBOL:
          coloredWord.append(YELLOW).append(letter).append(" ").append(RESET);
          break;
        case RED_SIMBOL:
          coloredWord.append(RED).append(letter).append(" ").append(RESET);
          break;
        default:
          coloredWord.append(letter);
      }
    }
    return coloredWord.toString();
  }

  private String convertToClueString(String originalString) {
    if (originalString == null)
      return null;

    StringBuilder clueStringBuilder = new StringBuilder();
    for (int i = 0; i < originalString.length(); i++) {
      clueStringBuilder.append("■");
    }

    return clueStringBuilder.toString();
  }

  private void printlnOutSolution(SecretWordDTO secretWordDTO) {
    if (secretWordDTO == null)
      return;
    printlnOutEqualSeparator();
    printOutValueOf("WORDLE_N_LABEL");
    printlnOut(secretWordDTO.getWordNumber());
    printOutValueOf("SECRETWORD_LABEL");
    printlnOut(secretWordDTO.getSecretWord());
    printOutValueOf("TRANSLATION_LABEL");
    printlnOut(secretWordDTO.getTranslatedSecretWord());
    printlnOutEqualSeparator();
  }

  private void printlnOutClues(ClueDTO[] clueDTOs) {
    printlnOutEqualSeparator();
    printlnOutValueOf("CLUES_LABEL");
    printlnOut();

    for (ClueDTO clueDTO : clueDTOs)
      printlnOut(colorizeWord(clueDTO.getWord(), clueDTO.getWordColors()));

    printlnOutEqualSeparator();
  }

  private void printlnOutStats(StatsDTO stats) {
    if (stats == null)
      return;

    printlnOutEqualSeparator();
    printlnOutValueOf("STATS_LABEL");
    printOutValueOf("PLAYED_LABEL");
    printlnOut(stats.getPlayed());
    printOutValueOf("WINPERCENTAGE_LABEL");
    printlnOut(stats.getWinPercentage() + "%");
    printOutValueOf("CURRENTSTREAK_LABEL");
    printlnOut(stats.getCurrentStreak());
    printOutValueOf("MAXSTREAK_LABEL");
    printlnOut(stats.getMaxStreak());
    printHistogram(stats);
    printlnOutEqualSeparator();
  }

  private void printHistogram(StatsDTO stats) {

    printlnOutValueOf("GUESSDISTRIBUTION_LABEL");
    printlnOut();
    Map<Integer, Integer> guessDistribution = stats.getGuessDistribution();
    int maxAttempts = guessDistribution.keySet().stream().max(Integer::compareTo).orElse(0);
    for (int i = maxAttempts; i > 0; i--) {
      printOut(i);
      if (i > 9)
        printOut(" ");
      else
        printOut("  ");

      if (guessDistribution.get(i) > 0) {
        for (int j = 1; j <= guessDistribution.get(i); j++)
          printOut("|");
        printOut(" ");
      }
      printOut(guessDistribution.get(i));
      printlnOut();
    }
  }

  private void printlnOutTop3() {

    System.out.printf("%-10s %-20s %-10s%n", "Position", "Player", "Score");
    printlnOutEqualSeparator();
    int position = 1;
    for (PlayerDTO player : top3)
      System.out.printf("%-10d %-20s %-10d%n", position++, player.getName(), player.getScore());


    printlnOutEqualSeparator();
  }

  private void printRanking(LinkedList<PlayerDTO> ranking) {
    printlnOutEqualSeparator();
    System.out.printf("%-10s %-20s %-10s%n", "Position", "Player", "Score");
    printlnOutSeparator();

    int position = 1;
    for (PlayerDTO player : ranking)
      System.out.printf("%-10d %-20s %-10d%n", position++, player.getName(), player.getScore());
    printlnOutEqualSeparator();
  }

  private void printlnOutSharedResults() {
    for (SharedGameResultDTO sharedGameResultDTO : sharedResults) {

      printlnOutEqualSeparator();
      printOutValueOf("PLAYER_LABEL");
      printlnOut(sharedGameResultDTO.getPlayername());
      printOutValueOf("WORDLE_N_LABEL");
      printOut(sharedGameResultDTO.getWordleNumber());
      printlnOut(": " + sharedGameResultDTO.getWordsColors().length + "/" + MAX_ATTEMPTS);

      for (String wordColors : sharedGameResultDTO.getWordsColors()) {
        String colorized = colorizeWord(convertToClueString(wordColors), wordColors);
        printlnOut(colorized);
      }
      printlnOutEqualSeparator();

    }
  }

  private void printOutWordleBanner() {
    printlnOut("=================================================================");
    printlnOut("                W   W  OOO  RRRR  DDD  L     EEEE                ");
    printlnOut("                W   W O   O R   R D  D L     E                   ");
    printlnOut("                W W W O   O RRRR  D  D L     EEE                 ");
    printlnOut("                W W W O   O R   R D  D L     E                   ");
    printlnOut("                 W W   OOO  R   R DDD  LLLLL EEEE                ");
    printlnOut("=================================================================");
  }

  private void printlnOutEqualSeparator() {
    printlnOut("=================================================================");
  }

  private void printlnOutSeparator() {
    printlnOut("-----------------------------------------------------------------");
  }

  private void printlnOut(String string) {
    System.out.println(string);
  }

  private void printOutValueOf(String key) {
    System.out.print(appStrings.getString(key));
  }

  private void printlnOutValueOf(String key) {
    System.out.println(appStrings.getString(key));
  }

  private void printlnErrValueOf(String key) {
    System.err.println(appStrings.getString(key));
  }

  private void printlnOut(int number) {
    System.out.println(number);
  }

  private void printlnOut() {
    System.out.println();
  }

  private void printOut(String string) {
    System.out.print(string);
  }

  private void printOut(int number) {
    System.out.print(number);
  }
}
