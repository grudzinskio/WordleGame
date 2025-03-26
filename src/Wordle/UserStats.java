/*
 * Course: SWE2710
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:07 PM
 */

package Wordle;

import javafx.event.Event;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * UserStats class
 * Tracks individual stats for players
 */
public class UserStats implements Observer {
    private static UserStats instance;

    private Map<String, Integer> commonLetters = new HashMap(26);
    private Map<String, Integer> commonWords = new HashMap();
    private String username;
    int guessCount;
    private int games;

    public UserStats(String username) {
        initCommonLetters();
        this.username = username;
    }

    public static void setInstance(String username) {
        instance = new UserStats(username);
    }

    public static UserStats getInstance() {
        return instance;
    }

    private void initCommonLetters() {
        commonLetters.put("a", 0);
        commonLetters.put("b", 0);
        commonLetters.put("c", 0);
        commonLetters.put("d", 0);
        commonLetters.put("e", 0);
        commonLetters.put("f", 0);
        commonLetters.put("g", 0);
        commonLetters.put("h", 0);
        commonLetters.put("i", 0);
        commonLetters.put("j", 0);
        commonLetters.put("k", 0);
        commonLetters.put("l", 0);
        commonLetters.put("m", 0);
        commonLetters.put("n", 0);
        commonLetters.put("o", 0);
        commonLetters.put("p", 0);
        commonLetters.put("q", 0);
        commonLetters.put("r", 0);
        commonLetters.put("s", 0);
        commonLetters.put("t", 0);
        commonLetters.put("u", 0);
        commonLetters.put("v", 0);
        commonLetters.put("w", 0);
        commonLetters.put("x", 0);
        commonLetters.put("y", 0);
        commonLetters.put("z", 0);
    }

    public double getAverageGuesses() {
        if (games == 0) return 0.0;
        return ((double) guessCount) / games;
    }

    public void updateGamesCount() {
        games++;
    }

    /**
     * updates user's stats based on inputted guess
     *
     * @param guess word being guessed
     */
    public void updateStats(String guess) {
        String[] letters = guess.toLowerCase().split("");

        guessCount++;
        for (String letter : letters) {
            int frequency = commonLetters.getOrDefault(letter, 0);
            commonLetters.put(letter, frequency);
        }

        int wordFrequency = commonWords.getOrDefault(guess, 0);
        commonWords.put(guess, wordFrequency + 1);
    }

    /**
     * @param event
     */
    @Override
    public void update(Event event) {

    }

    public int getGamesCount() {
        return games;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getLetterFrequencies() {
        return commonLetters;
    }

    public Map<String, Integer> getWordFrequencies() {
        return commonWords;
    }


    public void incrementGameCount(int gamesPlayed) {
        this.games += gamesPlayed;
    }

    public void updateLetterFrequency(String letterFrequencies) {
        JSONObject json = new JSONObject(letterFrequencies);
        for (String key : json.keySet()) {
            int frequency = json.getInt(key);
            commonLetters.put(key, commonLetters.getOrDefault(key, 0) + frequency);
        }
    }

    public void updateWordFrequency(String wordFrequencies) {
        JSONObject json = new JSONObject(wordFrequencies);
        for (String key : json.keySet()) {
            int frequency = json.getInt(key);
            commonWords.put(key, commonWords.getOrDefault(key, 0) + frequency);
        }
    }

    public int getGuessCount() {
        return guessCount;
    }
}