// File: src/Wordle/Vocabulary.java
package Wordle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Vocabulary {
    private static final Vocabulary INSTANCE = new Vocabulary();

    private String referenceFilePath = "";
    private String guessFilePath = "";

    private final List<String> referenceWords = new ArrayList<>();
    private final List<String> guessableWords = new ArrayList<>();

    private final Random random = new Random();

    public Vocabulary() { }

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

    // Returns a random reference word from the loaded reference words.
    public String getRandomWord() {
        if (referenceWords.isEmpty()) {
            return "";
        }
        return referenceWords.get(random.nextInt(referenceWords.size()));
    }

    // Checks if the provided word exists in either the reference or guessable words.
    public boolean contains(String word) {
        return referenceWords.contains(word) || guessableWords.contains(word);
    }
}