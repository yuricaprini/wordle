package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.AlreadyRegisteredUserException;

/**
 * {@code RegisteredUsers} represents a set containing all and only the registered users in Wordle.
 * <p>
 * This class implements the singleton pattern and is thread-safe.
 * 
 * @author Yuri Caprini
 */
public class RegisteredUsers {

  private static volatile RegisteredUsers instance;
  private ConcurrentHashMap<Username, User> registeredUsers;

  /**
  * Returns the single instance of the {@code RegisteredUsers} class. If the instance does not
  * exist, it is created and then returned.
  *
  * @return the single instance of the {@code RegisteredUsers} class.
  */
  public static RegisteredUsers getInstance() {
    if (instance == null) {
      synchronized (RegisteredUsers.class) {
        if (instance == null)
          instance = new RegisteredUsers();
      }
    }
    return instance;
  }

  /**
   * Constructs a new empty {@code RegisteredUsers} set.
   */
  private RegisteredUsers() {
    this.registeredUsers = new ConcurrentHashMap<Username, User>();
  }

  /**
   * Adds the specified {@code user} to this set if it is not already present.
   *
   * @param user the user to be added to this set.
   * @throws NullPointerException if {@code user == null}
   * @throws AlreadyRegisteredUserException if a user with the same username already exists.
   */
  public void add(User user) throws AlreadyRegisteredUserException {
    if (registeredUsers.putIfAbsent(user.getUsername(), user) != null)
      throw new AlreadyRegisteredUserException();
  }

  /**
   * Gets the user with the specified username.
   * 
   * @param username the name of the user to be returned.
   * @return the user with the specified username or null if it doesn't exist.
   * @throws NullPointerException if {@code username == null}
   */
  public User getBy(Username username) {
    return registeredUsers.get(username);
  }

  /**
   * Gets an unmodifiable iterator over the registered users in this set.
   * <p>
   * The returned iterator guarantees that any modifications made to the underlying set while 
   * iterating will not affect the iteration. However, it is important to note that the user 
   * objects themselves can still be modified.
   *
   * @return an unmodifiable iterator over the registered users in this set.
   */
  public Iterator<User> getUserIterator() {
    return Collections.unmodifiableCollection(registeredUsers.values()).iterator();
  }
}
