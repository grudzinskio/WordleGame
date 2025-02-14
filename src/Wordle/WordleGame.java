package Wordle;

import java.util.List;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class WordleGame {

	private List<Guess> guesses;
	private int maxGuesses;
	private String referenceWord;
	public GameSession m_GameSession;
	public Vocabulary m_Vocabulary;

	public WordleGame(){

	}

	public boolean checkWin(){
		return false;
	}

	/**
	 * 
	 * @param word
	 */
	public boolean isValidWord(String word){
		return false;
	}

	/**
	 * 
	 * @param guess
	 */
	public Guess makeGuess(String guess){
		return null;
	}

}