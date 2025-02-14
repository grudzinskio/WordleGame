package Wordle;

import javafx.event.Event;

import java.util.Map;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:07 PM
 */
public class UserStats implements Observer {

	private Map commonLetters;
	private Map date;
	private int guessCount;

	public UserStats(){

	}

	public double getAverageGuesses(){
		return 0;
	}


	/**
	 * 
	 * @param guess
	 */
	public void updateStats(Guess guess){

	}

	/**
	 * @param event
	 */
	@Override
	public void update(Event event) {

	}
}