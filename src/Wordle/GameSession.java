package Wordle;

import javafx.event.Event;

import java.util.List;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:30:56 PM
 */
public class GameSession {

	private WordleGame game;
	private List<Observer> observers;

	public GameSession(){

	}

	/**
	 * 
	 * @param observer
	 */
	public void addObserver(Observer observer){

	}

	/**
	 * 
	 * @param event
	 */
	public void notifyObservers(Event event){

	}

	/**
	 * 
	 * @param observer
	 */
	public void removeObserver(Observer observer){

	}

}