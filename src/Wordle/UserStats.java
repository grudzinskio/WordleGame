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

/**
 * UserStats class
 * Tracks individual stats for players
 */
public class UserStats implements Observer {
	private static final UserStats instance = new UserStats();

	private Map<String, Integer> commonLetters = new HashMap(26);
	private Map date;
	private int guessCount;
	private double avgGuess;
	private int games;

	public UserStats(){
		initCommonLetters();

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

	public double getAverageGuesses(){
		if (games == 0) return 0.0;
		return ((double) guessCount) / games;
	}
	
	public void updateGamesCount() {
		games++;
	}
	/**
	 * updates user's stats based on inputted guess
	 * @param guess word being guessed
	 */
	public void updateStats(String guess){
		guessCount++;

		String[] letters = guess.toLowerCase().split("");
		for(String i : letters) {
			int frequency = commonLetters.getOrDefault(i, 0);
			commonLetters.put(i, frequency + 1);
		}
	}

	/**
	 * @param event
	 */
	@Override
	public void update(Event event) {

	}

	public double getGamesCount() {
		return games;
	}

	public int getGuessCount() {
		return guessCount;
	}
}