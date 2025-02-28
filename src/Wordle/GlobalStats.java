package Wordle;

import javafx.event.Event;

import java.util.List;


/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:30:58 PM
 */
public class GlobalStats implements Observer {

	private int aggregateGuessCount;



	public GlobalStats(){

	}


	public List<String> getRecommendations(){
		return null;
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