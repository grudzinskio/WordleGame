package Wordle.Controllers;

import Wordle.Statistics.UserStatisticsDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminStatsController {

    @FXML
    private ListView<String> topLettersList;
    @FXML
    private ListView<String> topWordsList;
    @FXML
    private ListView<String> usersList;

    @FXML
    private void initialize() {
        // Populate all letters sorted by frequency
        Map<String, Integer> letterFrequencies = UserStatisticsDAO.getLetterFrequencies();
        var topLetters = letterFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        topLettersList.getItems().addAll(topLetters);

        // Populate all words sorted by frequency
        Map<String, Integer> wordFrequencies = UserStatisticsDAO.getWordFrequencies();
        var topWords = wordFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        topWordsList.getItems().addAll(topWords);

        // Populate all users
        List<String> allUsers = UserStatisticsDAO.getAllUsers();
        usersList.getItems().addAll(allUsers);

        // Add listener to open the selected user's stats display.
        usersList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Wordle/Views/Stats_Display.fxml"));
                    Parent root = loader.load();
                    StatDisplayController statController = loader.getController();
                    statController.setUser(newVal);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(newVal + " Stats");
                    stage.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}