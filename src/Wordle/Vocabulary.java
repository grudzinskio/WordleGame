package Wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

    public Vocabulary() {
        words = new HashSet<>();
    }

    /**
     * Loads words from the specified text file into the dictionary.
     * 
     * @param filePath The path to the text file containing the dictionary words.
     */
    public void loadWords(String filePath) {
        this.filePath = filePath;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the specified word exists in the dictionary.
     * 
     * @param word The word to check.
     * @return True if the word exists in the dictionary, false otherwise.
     */
    public boolean contains(String word) {
        return words.contains(word.toLowerCase());
    }
}