package Wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Vocabulary class is responsible for loading and managing the dictionary words.
 * It provides methods to load words from a text file and check if a word exists in the dictionary.
 * 
 * @version 1.0
 * @created 14-Feb-2025 1:31:08 PM
 */
public class Vocabulary {
    private static final Vocabulary vocabulary = new Vocabulary();
    private String filePath;
    private Set<String> words;
    private Set<String> validReferenceWords;

    public Vocabulary() {
        words = new HashSet<>();
        validReferenceWords = words;
    }
    public static Vocabulary getVocabulary() {
        return vocabulary;
    }

    public String getFilePath() {
        return filePath;
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

}