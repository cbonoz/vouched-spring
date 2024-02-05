package com.vouched.service;

import com.vouched.error.SoftException;
import io.netty.util.internal.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class EndorsementService {

  static final String RESOURCE_PATH = "classpath:words/bad_words.csv";

  private final Map<String, String[]> words = new HashMap<>();

  private int largestWordLength = 0;

  @Inject
  public EndorsementService(ResourceLoader resourceLoader) {
    try {
      Resource resource = resourceLoader.getResource(RESOURCE_PATH);
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
      String line = "";
      int counter = 0;
      while ((line = reader.readLine()) != null) {
        counter++;
        String[] content;
        try {
          content = line.split(",");
          if (content.length == 0) {
            continue;
          }
          String word = content[0];
          String[] ignore_in_combination_with_words = new String[]{};
          if (content.length > 1) {
            ignore_in_combination_with_words = content[1].split("_");
          }

          if (word.length() > largestWordLength) {
            largestWordLength = word.length();
          }
          words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);

        } catch (Exception e) {
          e.printStackTrace();
        }

      }
      System.out.println("Loaded " + counter + " words to filter out");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  /**
   * Iterates over a String input and checks whether a cuss word was found in a list, then
   * checks if the word should be ignored (e.g. bass contains the word *ss).
   *
   * @param input text to examine
   * @return list of bad words
   */
  private ArrayList<String> badWordsFound(String input) {
    if (input == null) {
      return new ArrayList<>();
    }

    // don't forget to remove leetspeak, probably want to move this to its own function and use regex if you want to use this

    input = input.replaceAll("1", "i");
    input = input.replaceAll("!", "i");
    input = input.replaceAll("3", "e");
    input = input.replaceAll("4", "a");
    input = input.replaceAll("@", "a");
    input = input.replaceAll("5", "s");
    input = input.replaceAll("7", "t");
    input = input.replaceAll("0", "o");
    input = input.replaceAll("9", "g");

    ArrayList<String> badWords = new ArrayList<>();
    input = input.toLowerCase().replaceAll("[^a-zA-Z ]", "");

    // iterate over each letter in the word
    for (int start = 0; start < input.length(); start++) {
      // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
      for (int offset = 1;
          offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
        String wordToCheck = input.substring(start, start + offset);
        if (words.containsKey(wordToCheck)) {
          // for example, if you want to say the word bass, that should be possible.
          String[] ignoreCheck = words.get(wordToCheck);
          boolean ignore = false;
          for (String value : ignoreCheck) {
            if (input.contains(value)) {
              ignore = true;
              break;
            }
          }
          if (!ignore) {
            badWords.add(wordToCheck);
          }
        }
      }
    }

    for (String s : badWords) {
      System.out.println(s + " qualified as a bad word in a username");
    }
    return badWords;

  }

  // Validate comment
  public void validateComment(String input) {
    if (Strings.isBlank(input)) {
      throw new SoftException("Text cannot be empty");
    }
    if (input.length() > 1000) {
      throw new SoftException("Text cannot be longer than 1000 characters");
    }
    checkProfanity(input);
  }

  private void checkProfanity(String input) {
    ArrayList<String> badWords = badWordsFound(input);
    if (!badWords.isEmpty()) {
      throw new SoftException(
          "Please remove these words: " + StringUtil.join(",", badWords));
    }
  }

}
