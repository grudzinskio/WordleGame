package test;

import Wordle.WordleGame;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvilWordleGameTest {
    //testing set up is taken from the setup for WordleGameTest
    private WordleGame game;

    static {
        new JFXPanel();
    }

    @BeforeEach
    public void setGame() {
        game = new WordleGame();
        // Simulate a 6x5 grid of labels.
        game.labels = new Label[6][5];
        game.hintCells = new boolean[6][5];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                game.labels[row][col] = new Label();
                // Initialize all cells with empty text.
                game.labels[row][col].setText("");
                game.hintCells[row][col] = false;
            }
        }
        // Reset row and column indices.
        game.lRow = 0;
        game.lCol = 0;
        // Reset hint usage.
        game.hintsUsed = 0;
        game.maxHints = 2;
        // Create a dummy hint button.
        game.hintButton = new Button();
        // Set the secret/reference word to "ALLOW".
        game.referenceWord = "ALLOW";
        // Turn on Evil Wordle
        game.evilWordleEnabled = true;
    }

    /**
     * Ensures that the vocabulary gets reset after each testing of Evil Wordle
     */
    @AfterEach
    public void resetGame(){
        game.vocabulary.vocabRestart();
    }

    @Test
    public void ensureReferenceWordChanges(){
        //simulate a guess
        game.giveFeedbackOnWord("ALLOW");

        //ensure the reference word changes
        assertNotEquals("ALLOW", game.referenceWord);

        //simulate another guess using the new reference word
        game.giveFeedbackOnWord(game.referenceWord);

        //ensure the reference word changes
        assertNotEquals("MOTEL", game.referenceWord);
    }

    @Test
    public void ensureNoLettersMatch(){
        //save current reference word
        String word = game.referenceWord;

        //simulate a guess
        game.giveFeedbackOnWord("ALLOW");

        //save new reference word
        String newWord = game.referenceWord;

        //check to make sure each letter isn't in the new reference word
        for(String i : word.split("")) {
            assertFalse(newWord.contains(i));
        }
    }

    @Test
    public void ensureWordStaysAfterNoEligibleWords(){
        //simulate guesses using as many letters as possible
        game.giveFeedbackOnWord("motel");
        game.giveFeedbackOnWord("bring");
        game.giveFeedbackOnWord("whack");
        game.giveFeedbackOnWord("fussy");

        //ensure that target word stays the same after there are no more word options left

        assertEquals("PUPPY", game.referenceWord);
        game.giveFeedbackOnWord("fluff");

        assertEquals("PUPPY", game.referenceWord);
        game.giveFeedbackOnWord("puppy");

        assertEquals("PUPPY", game.referenceWord);

    }


}
