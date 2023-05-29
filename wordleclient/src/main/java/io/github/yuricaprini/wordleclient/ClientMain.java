package io.github.yuricaprini.wordleclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class ClientMain {

  public static void main(String[] args) {

    System.out.println("Hello");
    
    //load client configuration
    ResourceBundle bundle = ResourceBundle.getBundle("ClientMessages");
    
    if (args.length != 1)
      System.err.println(bundle.getString("ERR_CLI_INVALIDARGSNUMBER") + "\n");

  //   ClientConfiguration clientConfiguration = null;
  //   try {
  //     clientConfiguration = loadConfiguration(args[0]);

  //   } catch (InvalidPathException ipe) {
  //     System.err.println("Error: " + ERR_CNF_INVALIDPATH);
  //     System.err.println("Cause: " + ipe.getMessage());
  //     System.err.println(ERR_CLI_USAGE);
  //     return;

  //   } catch (IOException ioe) {
  //     System.err.println("Error: " + ERR_CNF_OPENINGJSONFILE);
  //     System.err.println("Cause: " + ioe.getMessage());
  //     System.err.println(ERR_CLI_USAGE);
  //     return;

  //   } catch (SecurityException se) {
  //     System.err.println("Error: " + ERR_CNF_SECURITY);
  //     System.err.println("Cause: " + se.getMessage());
  //     System.err.println(ERR_CLI_USAGE);
  //     return;

  //   } catch (JsonIOException jIOe) {
  //     System.err.println("Error: " + ERR_CNF_READINGJSONFILE);
  //     System.err.println("Cause: " + jIOe.getMessage());
  //     System.err.println(ERR_CLI_USAGE);
  //     return;

  //   } catch (JsonSyntaxException jse) {
  //     System.err.println("Error: " + ERR_CNF_MALFORMEDJSON);
  //     System.err.println("Cause: " + jse.getMessage());
  //     System.err.println(ERR_CLI_USAGE);
  //     return;
  //   }

  //   if (clientConfiguration == null) {
  //     System.err.println("Error: " + ERR_CNF_EMPTYFILE);
  //     System.err.println(ERR_CLI_USAGE);
  //     return;
  //   }

  //   if (!checkConfigurationFields(clientConfiguration)) {
  //     System.err.println("Error: " + ERR_CNF_INCORRECT);
  //     System.err.println(ERR_CLI_USAGE);
  //     return;
  //   }

  //   // ---------------------------------------------------------------------------------------------

  //   System.out.println(OUT_CLI_HELLO);

  //   // -------------------------------------------------------------------------------------------
  //   // Client input parsing loop
  //   // -------------------------------------------------------------------------------------------
  //   Request.Factory requestFactory = ProtocolFactoryProvider.getRequestFactory();
  //   RequestWriter requestWriter =
  //       ProtocolFactoryProvider.getRequestWriterFactory().createRequestWriter();
  //   ResponseReader responseReader =
  //       ProtocolFactoryProvider.getResponseReaderFactory().createResponseReader();
  //   ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFERSIZE);
  //   ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFERSIZE);
  //   SocketChannel socketChannel = null;
  //   String token = null;

  //   try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));) {
  //     String input = null;
  //     while ((input = reader.readLine()) != null) {

  //       String[] inputs = input.split(" ", 2);
  //       String command = inputs[0];
  //       String remainingInput = (inputs.length > 1) ? inputs[1] : null;

  //       switch (command) {

  //         case REGISTER_COMMAND:

  //           // parse register command args
  //           String[] registerArgs = remainingInput.split(" ", -1);
  //           if (registerArgs.length < 2) {
  //             System.err.println("Error: " + ERR_REGCOMM_NO_ARGS);
  //             break;
  //           }
  //           String username = registerArgs[0];
  //           String password = registerArgs[1];
  //           String[] tags = new String[registerArgs.length - 2];
  //           for (int i = 0; i < tags.length; i++)
  //             tags[i] = registerArgs[i + 2];

  //           // connect to remote service and register
  //           try {
  //             Registry registry = LocateRegistry.getRegistry(clientConfiguration.registryHost);
  //             UserRegistrationRemoteService remoteService = (UserRegistrationRemoteService) registry
  //                 .lookup(UserRegistrationRemoteService.class.getName());

  //             remoteService.registerUser(username, password, tags);

  //           } catch (CannotRegisterUserException cr) {
  //             System.err.println("Error: " + ERR_REGISTER_NO);
  //             System.err.println("Cause: " + cr.getMessage());
  //             break;
  //           }

  //           System.out.println(OUT_REGISTER_OK);
  //           break;

  //         case LOGIN_COMMAND:

  //           // parse login args
  //           String[] loginArgs = remainingInput.split(" ", -1);
  //           if (loginArgs.length < 2) {
  //             System.err.println("Error: " + ERR_LOGINCOMM_NO_ARGS);
  //             break;
  //           }
  //           String loginUsername = loginArgs[0];
  //           String loginPassword = loginArgs[1];

  //           // connect to server
  //           if (socketChannel != null) {
  //             System.err.println("Error: Login failed");
  //             System.err.println("Cause: Already logged in. You must logout");
  //             break;
  //           }

  //           socketChannel = SocketChannel.open();
  //           socketChannel.connect(
  //               new InetSocketAddress(InetAddress.getByName(clientConfiguration.serverAddress),
  //                   clientConfiguration.serverPort));

  //           // send login request
  //           Request loginRequest = requestFactory.createLogin(loginUsername, loginPassword);
  //           sendRequest(loginRequest, requestWriter, outputBuffer, socketChannel);

  //           // get login response
  //           Response loginResponse = receiveResponse(socketChannel, inputBuffer, responseReader);
  //           token = loginResponse.getToken();
  //           switch (loginResponse.getType()) {
  //             case LOGIN_OK:
  //               System.out.println("Login successful. Welcome back!");
  //               break;
  //             case LOGIN_NO:
  //               System.err.println("Error: Login failed");
  //               System.err.println("Cause: " + loginResponse.getError());
  //               break;
  //             default:
  //               throw new IllegalResponseException("Illegal response type");
  //           }

  //           break;

  //         case LOGOUT_COMMAND:

  //           if (remainingInput != null) {
  //             System.err.println("Error: invalid number of arguments for 'logout'");
  //             break;
  //           }
  //           if (socketChannel == null) {
  //             System.err.println("Error: Logout failed");
  //             System.err.println("Cause: You are not logged in");
  //             break;
  //           }

  //           socketChannel.close();
  //           socketChannel = null;
  //           token = null;
  //           System.out.println("Logout successful. Goodbye.'");
  //           break;

  //         case LIST_COMMAND:
  //           String[] listArgs = remainingInput.split(" ", -1);

  //           if (listArgs.length != 1) {
  //             System.err.println("Error: invalid number of arguments for 'list'");
  //             break;
  //           }

  //           switch (listArgs[0]) {
  //             case USERS_ARG:

  //               Request listusersRequest = requestFactory.createListUsers(token);
  //               sendRequest(listusersRequest, requestWriter, outputBuffer, socketChannel);
  //               System.out.println("SENT: listusers");

  //               while (true) {
  //                 Response listusersResponse =
  //                     receiveResponse(socketChannel, inputBuffer, responseReader);
  //                 System.out.println(listusersResponse.getType());
  //                 System.out.println(listusersResponse.getUserID());
  //                 String[] strtags = listusersResponse.getTags();
  //                 for (String tag : strtags) {
  //                   System.out.println(tag);
  //                 }

  //                 if (listusersResponse.isLastChunk())
  //                   break;
  //               }
  //               System.out.println("RECEIVED: listusers");
  //               break;

  //             case FOLLOWING_ARG:

  //               Request lfollowingRequest = requestFactory.createListFollowing(token);
  //               sendRequest(lfollowingRequest, requestWriter, outputBuffer, socketChannel);

  //               while (true) {

  //                 Response lfollowingResponse =
  //                     receiveResponse(socketChannel, inputBuffer, responseReader);

  //                 if (lfollowingResponse.getType() == Type.LISTFOLLOWING_OK) {
  //                   System.out.println("User: " + lfollowingResponse.getUserID());
  //                   System.out.println("Tags: " + String.join(" ", lfollowingResponse.getTags()));
  //                 }
  //                 if (lfollowingResponse.isLastChunk())
  //                   break;
  //               }

  //               break;

  //             case FOLLOWERS_ARG:

  //               break;

  //             default:
  //               break;
  //           }
  //           break;
  //         case FOLLOW_COMMAND:

  //           // parse follow arguments
  //           String[] followArgs = remainingInput.split(" ", -1);
  //           if (followArgs.length != 1) {
  //             System.err.println("Error: invalid number of arguments for 'follow'");
  //             break;
  //           }
  //           String userToFollowID = followArgs[0];

  //           // send follow request
  //           Request followRequest = requestFactory.createFollowUser(token, userToFollowID);
  //           sendRequest(followRequest, requestWriter, outputBuffer, socketChannel);

  //           // get follow response
  //           Response followResponse = receiveResponse(socketChannel, inputBuffer, responseReader);

  //           // print response
  //           if (followResponse.getType() == Type.FOLLOWUSER_OK)
  //             System.out.println("Now you follow " + userToFollowID + ".");
  //           if (followResponse.getType() == Type.FOLLOWUSER_NO) {
  //             System.err.println("Error: impossible to follow " + userToFollowID + ".");
  //             System.err.println("Cause: " + followResponse.getError());
  //           }

  //           break;

  //         case UNFOLLOW_COMMAND:

  //           // parse follow arguments
  //           String[] unfollowArgs = remainingInput.split(" ", -1);
  //           if (unfollowArgs.length != 1) {
  //             System.err.println("Error: invalid number of arguments for 'unfollow'");
  //             break;
  //           }
  //           String userToUnfollowID = unfollowArgs[0];

  //           // send unfollow request
  //           Request unfollowRequest = requestFactory.createUnFollowUser(token, userToUnfollowID);
  //           sendRequest(unfollowRequest, requestWriter, outputBuffer, socketChannel);

  //           // get unfollow response
  //           Response unfollowResponse = receiveResponse(socketChannel, inputBuffer, responseReader);

  //           // print response
  //           switch (unfollowResponse.getType()) {
  //             case UNFOLLOWUSER_OK:
  //               System.out.println("Now you unfollow " + userToUnfollowID + ".");
  //               break;
  //             case UNFOLLOWUSER_NO:
  //               System.err.println("Error: impossible to unfollow " + userToUnfollowID + ".");
  //               System.err.println("Cause: " + unfollowResponse.getError());
  //               break;
  //             default: // never in this branch
  //               break;
  //           }
  //           break;

  //         case POST_COMMAND:

  //           // parse post arguments
  //           String[] postArgs = remainingInput.split("\" \"", 0);
  //           if (postArgs.length != 2) {
  //             System.err.println("Error: invalid number of arguments for 'post'");
  //             break;
  //           }
  //           String title = postArgs[0].substring(1);
  //           String content = postArgs[1].substring(0, postArgs[1].length() - 1);

  //           // send request
  //           Request createPostRequest = requestFactory.createCreatePost(token, title, content);
  //           sendRequest(createPostRequest, requestWriter, outputBuffer, socketChannel);

  //           // receive response
  //           Response createPostResponse =
  //               receiveResponse(socketChannel, inputBuffer, responseReader);

  //           // print to console
  //           if (createPostResponse.getType() == Type.CREATEPOST_OK)
  //             System.out.println(
  //                 "Post successfully created. ( id= " + createPostResponse.getPostID() + " )");
  //           if (createPostResponse.getType() == Type.CREATEPOST_NO) {
  //             System.err.println("Error: post creation failed.");
  //             System.err.println("Cause: " + createPostResponse.getError());
  //           }

  //           break;

  //         case BLOG_COMMAND:

  //           Request blogRequest = requestFactory.createViewBlog(token);
  //           sendRequest(blogRequest, requestWriter, outputBuffer, socketChannel);

  //           while (true) {

  //             Response blogResponse = receiveResponse(socketChannel, inputBuffer, responseReader);

  //             if (blogResponse.getType() == Type.VIEWBLOG_NO) {
  //               System.err.println("Error: impossible to view blog.");
  //               System.err.println("Cause: " + blogResponse.getError());
  //             }
  //             if (blogResponse.getType() == Type.VIEWBLOG_OK) {
  //               System.out.println("-------------------------------------------------------------");
  //               System.out.println("PostID: " + blogResponse.getPostID());
  //               System.out.println("Author: " + blogResponse.getAuthor());
  //               System.out.println("Title: " + blogResponse.getTitle());
  //               System.out.println("-------------------------------------------------------------");
  //             }

  //             if (blogResponse.isLastChunk()) {
  //               break;
  //             }
  //           }

  //           break;

  //         case SHOW_COMMAND:
  //           String[] showArgs = remainingInput.split(" ", 2);

  //           if (showArgs.length != 1) {
  //             System.err.println("Error: invalid number of arguments for 'show'");
  //             break;
  //           }

  //           switch (showArgs[0]) {

  //             case FEED_ARG:

  //               Request showFeedRequest = requestFactory.createShowFeed(token);
  //               sendRequest(showFeedRequest, requestWriter, outputBuffer, socketChannel);

  //               while (true) {

  //                 Response showFeedResponse =
  //                     receiveResponse(socketChannel, inputBuffer, responseReader);

  //                 if (showFeedResponse.getType() == Type.SHOWFEED_NO) {
  //                   System.err.println("Error: Impossible to view feed.");
  //                   System.err.println("Cause: " + showFeedResponse.getError());
  //                 }
  //                 if (showFeedResponse.getType() == Type.SHOWFEED_OK) {
  //                   System.out
  //                       .println("-------------------------------------------------------------");
  //                   System.out.println("PostID: " + showFeedResponse.getPostID());
  //                   System.out.println("Author: " + showFeedResponse.getAuthor());
  //                   System.out.println("Title: " + showFeedResponse.getTitle());
  //                   System.out
  //                       .println("-------------------------------------------------------------");
  //                 }

  //                 if (showFeedResponse.isLastChunk()) {
  //                   break;
  //                 }
  //               }
  //               break;

  //             case POST_ARG:
  //               break;
  //           }

  //           break;

  //         // -------------------------------------------------------------------------------------

  //         default:
  //           System.err.println("Error: " + COMMAND_INVALID);
  //           break;

  //       }
  //     }
  //     // -------------------------------------------------------------------------------------------

  //   } catch (

  //   IOException IOe) {
  //     IOe.printStackTrace();
  //   } catch (NotBoundException nbe) {
  //     nbe.printStackTrace();
  //   } catch (IllegalResponseException e) {
  //     e.printStackTrace();
  //   }

  //   System.out.println(OUT_CLI_BYE);
  // }

  // private static Response receiveResponse(SocketChannel socketChannel, ByteBuffer inputBuffer,
  //     ResponseReader responseReader) throws IOException, IllegalResponseException {
  //   Response response = null;
  //   while (response == null) {
  //     socketChannel.read(inputBuffer);
  //     inputBuffer.flip();
  //     response = responseReader.readResponseFrom(inputBuffer);
  //     inputBuffer.compact();
  //   }
  //   return response;
  // }

  // private static void sendRequest(Request request, RequestWriter requestWriter,
  //     ByteBuffer outputBuffer, SocketChannel socketChannel) throws IOException {

  //   requestWriter.encodeForWriting(request);
  //   while (requestWriter.writeTo(outputBuffer) >= 0) {
  //     outputBuffer.flip();
  //     socketChannel.write(outputBuffer);
  //     outputBuffer.compact(); // better than clear(), works in (non-) blocking mode
  //   }
  // }

  // /**
  //  * Loads the client configuration file from <code>clientConfigurationFilePath</code>
  //  * 
  //  * @param clientConfigurationFilePath the absolute or relative path string for configuration file
  //  * @return the object representing the client configuration
  //  * @throws InvalidPathException if the path string cannot be converted to a <code>Path</code>
  //  * @throws IOException if an I/O error occurs opening the file
  //  * @throws SecurityException if there are no rights to read the file
  //  * @throws JsonIoException if error occurs reading the file
  //  * @throws JsonSyntaxException if the json representation of client configuration is malformed
  //  */
  // private static ClientConfiguration loadConfiguration(String clientConfigurationFilePath)
  //     throws InvalidPathException, IOException, SecurityException, JsonIOException,
  //     JsonSyntaxException {

  //   Gson gson = new Gson();
  //   try (Reader reader = Files.newBufferedReader(Paths.get(clientConfigurationFilePath));) {
  //     return gson.fromJson(reader, ClientConfiguration.class);
  //   }
  // }

  // /**
  //  * 
  //  * @param clientConfiguration
  //  * @return
  //  */
  // private static boolean checkConfigurationFields(ClientConfiguration clientConfiguration) {

  //   return true;
   }
}
