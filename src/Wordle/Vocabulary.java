// File: src/Wordle/Vocabulary.java
package Wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Vocabulary class is responsible for loading and managing the dictionary words.
 * It provides methods to load words from a text file and check if a word exists in the dictionary.
 * 
 * @version 1.0
 * @created 14-Feb-2025 1:31:08 PM
 */
public class Vocabulary {

    private String filePath;
    private Set<String> words;
    private Set<String> validReferenceWords;
    private static final Vocabulary INSTANCE = new Vocabulary();

    private String referenceFilePath = "";
    private String guessFilePath = "";

    private final List<String> referenceWords = new ArrayList<>();
    private final List<String> guessableWords = new ArrayList<>();

    private final Random random = new Random();

    public Vocabulary() {
        words = new HashSet<>();
        validReferenceWords = words;
    }
    public static Vocabulary getVocabulary() {
        return INSTANCE;
    }

    public String getReferenceFilePath() {
        return referenceFilePath;
    }

    // Loads reference words from the given file path and stores them in referenceWords.
    public void loadWords(String filePath) {
        this.referenceFilePath = filePath;
        referenceWords.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String word = line.trim();
                if (!word.isEmpty() && !referenceWords.contains(word)) {
                    referenceWords.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads guessable words from the given file path.
    public void loadGuessWords(String filePath) {
        this.guessFilePath = filePath;
        guessableWords.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String word = line.trim();
                if (!word.isEmpty() && !guessableWords.contains(word)) {
                    guessableWords.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getReferenceWords() {
        return new ArrayList<>(referenceWords);
    }

    public List<String> getGuessableWords() {
        return new ArrayList<>(guessableWords);
    }

    // Adds new words to the guessable words list.
    public void addGuessWords(List<String> newWords) {
        for (String word : newWords) {
            String trimmed = word.trim();
            if (!trimmed.isEmpty() && !guessableWords.contains(trimmed)) {
                guessableWords.add(trimmed);
            }
        }
    }

    // Removes a word from the guessable words list.
    public void removeGuessWord(String word) {
        guessableWords.remove(word);
    }

    // Resets additional guess words.
    public void resetAdditionalGuessWords() {
        guessableWords.clear();
    }

    /**
     * gets a random word from the vocabulary file
     * @param filterWord if a filter is needed for the vocabulary file - used for evil mode
     * @return string containing the random word
     */

    public String getRandomWord(String filterWord) {

        String referenceWord;
        if (words.isEmpty()) return "apple"; // Fallback if something goes wrong
        //returns empty String if validReferenceWords is empty
        if(validReferenceWords.isEmpty()) {
            return "";
        }
        //get a random index from validReferenceWords
        int index = (int) (Math.random() * validReferenceWords.size());

        //get the word from the corresponding index
        referenceWord = validReferenceWords.toArray(new String[0])[index];

        //differentiate between regular wordle and evil wordle
        if(!filterWord.isEmpty()) {
            for(String c : filterWord.split("")) {
                //filters words based on each character in filter word
                validReferenceWords = validReferenceWords.stream().filter(word -> !word.contains(c.toLowerCase())).collect(Collectors.toSet());

                //handles case where no more options are possible and index stays same
                if(!validReferenceWords.isEmpty()) {
                    //get a new index since validReferenceWords isn't empty
                    index = (int) (Math.random() * validReferenceWords.size());
                    //get the new word from the index
                    referenceWord = validReferenceWords.toArray(new String[0])[index];
                }
            }
        }

        return referenceWord;
    }

    /**
     * used to reset validReferenceWords after game reset
     */
    public void vocabRestart() {
        validReferenceWords = words;
    }
    // Checks if the provided word exists in either the reference or guessable words.
    public boolean contains(String word) {
        return referenceWords.contains(word) || guessableWords.contains(word);
    }
}