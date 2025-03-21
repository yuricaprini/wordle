package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle02usecases.EntityRepository;

public class EntityRepositoryAdapter implements EntityRepository {

  private String filename;
  private Gson gson;

  public EntityRepositoryAdapter(String fileName) {
    this.filename = fileName;
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Override
  public Iterator<User> loadAll() throws Exception {
    try (Reader reader = new FileReader(filename)) {
      User[] users = gson.fromJson(reader, User[].class);
      return Collections.unmodifiableCollection(Arrays.asList(users)).iterator();
    } catch (FileNotFoundException e) {
      return Collections.unmodifiableCollection(new ArrayList<User>()).iterator();
    }
  }

  @Override
  public void pushAll(Iterator<User> iterator) throws IOException {
    String tempFileName = filename + ".temp";
    deleteFile(tempFileName);

    try (Writer tempWriter = new FileWriter(tempFileName)) {
      tempWriter.write("[\n");

      while (iterator.hasNext()) {
        User user = iterator.next();
        String json = gson.toJson(user);
        tempWriter.write(json);

        if (iterator.hasNext()) {
          tempWriter.write(",\n");
        }
      }

      tempWriter.write("\n]");
    }

    replaceFile(tempFileName);
  }

  private void deleteFile(String filePath) {
    File fileToDelete = new File(filePath);
    if (fileToDelete.exists())
      fileToDelete.delete();
  }

  private void replaceFile(String tempFileName) {
    File originalFile = new File(filename);
    File tempFile = new File(tempFileName);

    if (originalFile.exists())
      originalFile.delete();

    tempFile.renameTo(originalFile);
  }

}
