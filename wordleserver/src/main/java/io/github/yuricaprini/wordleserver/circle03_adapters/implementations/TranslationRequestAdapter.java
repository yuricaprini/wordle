package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.yuricaprini.wordleserver.circle02usecases.WordTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TranslationRequestAdapter implements WordTranslator {

  private static final String MY_MEMORY_API_URL = "https://api.mymemory.translated.net/get";

  @Override
  public String translate(String text) {
    try {

      String response = makeGetRequest(buildRequestUrl(text));
      JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
      return jsonResponse.getAsJsonObject("responseData").get("translatedText").getAsString();

    } catch (Exception e) {
      return "*** NONE ***";
    }
  }

  private String makeGetRequest(String url) throws IOException {

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setConnectTimeout(10000);
    con.setReadTimeout(10000);

    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));) {
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = in.readLine()) != null)
        response.append(inputLine);
      return response.toString();
    }
  }

  private String buildRequestUrl(String text) {
    return MY_MEMORY_API_URL + "?q=" + text + "&langpair=en|it";
  }
}
