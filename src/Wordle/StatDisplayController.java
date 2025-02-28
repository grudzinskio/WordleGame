/*
 * Course: SWE2710
 * Created: 2/26/24
 * Created by: Mathias G
 */
package Wordle;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class StatDisplayController {
    public TextField averageGuesses;
    public TextField gamesPlayed;

    @FXML
    private void initialize() {
        UserStats userStats = UserStats.getInstance();
        averageGuesses.setText(String.valueOf(userStats.getAverageGuesses()));
        gamesPlayed.setText(String.valueOf(userStats.getGamesCount()));
    }
}
