package Wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class WordleGame {

	public Label guess_display;
	public TextField averageGuesses;
	public TextField gamesPlayed;
	@FXML
	private AnchorPane rootPane;

@FXML
private GridPane gridPane;

	private List<Guess> guesses;
	private int maxGuesses;
	private String referenceWord;
	private UserStats userStats;
	public GameSession m_GameSession;
	public Vocabulary m_Vocabulary;
	private Label[][] labels;
	private int lRow = 0;
	private int lCol = 0;


	//My implementation of handling input is keep a buffer of characters,
	// so we could manipulate this array for different keyboard input.
	private List<Character> characters = new ArrayList<>();

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
		return true;
	}

	/**
	 * 
	 * @param guess
	 */
	public Guess makeGuess(String guess){
		if(!checkWin()) {
			int guesses = Integer.parseInt(guess_display.getText());
			guess_display.setText(String.valueOf(--guesses));

			userStats.updateStats(guess);

		}
		return null;
	}

	@FXML
	private void initialize() {
		populateLabels();
		userStats = UserStats.getInstance();
		userStats.updateGamesCount();
	}

	/*
		populate labels array, so we can use the instances to manipulate stuff.
	 */
	private void populateLabels() {
		labels = new Label[6][5];
		for (Node node : gridPane.getChildren()) {
			if (node instanceof Label) {
				Integer row = GridPane.getRowIndex(node);
				Integer col = GridPane.getColumnIndex(node);
				if (row == null) row = 0;
				if (col == null) col = 0;
				labels[row][col] = (Label) node;
			}
		}
	}

	/*
		Handles different keys on virtual keyboard of our GUI.
	 */
	@FXML
	private void handleKeyboardButton(ActionEvent event) {
		Button b = (Button) event.getSource();
		String buttonText = b.getText();
		if (buttonText.equals("ENTER")) {
			handleEnterButton();
		} else if (buttonText.equals("BACK")) {
			handleBackButton();
		} else {
			handleLetterKey(buttonText);
		}
	}

		/*
		Handle letters specifically
	 */
	private void handleLetterKey(String text) {
		if (lCol < 5) {
			labels[lRow][lCol].setText(text);
			characters.add(text.charAt(0)); // Store character
			lCol++;
		}
	}


	private void handleBackButton() {
		if (lCol > 0) {
			lCol--;
			labels[lRow][lCol].setText("");
			characters.remove(characters.size() - 1); // Remove last character
		}
	}

	private void handleEnterButton() {
		if (lCol == 5) {
			String word = getWordFromLabel().toUpperCase(); // Ensure uppercase comparison
		 if (isValidWord(word.toLowerCase())) {
				//giveFeedBackOnWord(word);
				lRow++;
				lCol = 0;
				characters.clear(); // Clear for next word
			 	makeGuess(word);
			} else {
				System.out.println("Word not in list.");
			}
		}
	}
	private String getWordFromLabel() {
		StringBuilder word = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			word.append(labels[lRow][i].getText());
		}
		return word.toString();
	}

	/**
	 * Created by Mathias G
	 * This launches the secondary window pop-up to display a user's stats
	 * @param actionEvent actionEvent is when the View Stats button is clicked
	 * @throws IOException Exception thrown if fxml issues occur and file can't be loaded
	 */
	public void viewStats(ActionEvent actionEvent) throws IOException {
		Parent stats = FXMLLoader.load(getClass().getResource("Stats_Display.fxml"));
		Scene scene = new Scene(stats);
		Stage stage = new Stage();


		stage.setScene(scene);
		stage.setTitle("User Stats");
		stage.show();
	}
}