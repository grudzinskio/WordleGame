package Wordle.Controllers;

import Wordle.Statistics.HighScore;
import Wordle.Statistics.HighScoreDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class HighScoreController {

    @FXML
    private ListView<String> highScoreList;

    @FXML
    private void initialize() {
        List<HighScore> scores = HighScoreDAO.getHighScores();
        // The list is sorted first by time then by guesses
        highScoreList.setItems(FXCollections.observableArrayList(
            scores.stream().map(HighScore::toString).toList()
        ));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) highScoreList.getScene().getWindow();
        stage.close();
    }
}