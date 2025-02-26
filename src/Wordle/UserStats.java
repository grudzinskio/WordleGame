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

	private Map<String, Integer> commonLetters = new HashMap(26);
	private Map date;
	private int guessCount;
	private int avgGuess;

	public UserStats(){
		initCommonLetters();

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
		return 0;
	}

	/**
	 * updates user's stats based on inputted guess
	 * @param guess word being guessed
	 */
	public void updateStats(String guess){
		String[] letters = guess.toLowerCase().split("");
		for(String i : letters) {
			int frequency = commonLetters.get(i);
			frequency++;
			commonLetters.put(i, frequency);
		}
	}

	/**
	 * @param event
	 */
	@Override
	public void update(Event event) {

	}
}