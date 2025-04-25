package Wordle.Controllers;

import Wordle.DatabaseManager;
import Wordle.Statistics.UserStats;
import Wordle.Statistics.UserStatisticsDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class StatDisplayController {

    public static StatDisplayController instance;

    @FXML public Label userNameDisplay;
    @FXML public ListView<String> topLettersList;
    @FXML public ListView<String> topWordsList;
    @FXML public Label averageGuessesText;
    @FXML public Label gamesPlayedText;

    @FXML public ComboBox<String> letterSortBox;
    @FXML public ComboBox<String> wordSortBox;

    private String currentUser;

    @FXML
    private void initialize() {
        instance = this;

        if (currentUser == null) {
            currentUser = UserStats.getInstance().getUsername();
        }

        // Set up sort options
        letterSortBox.setItems(FXCollections.observableArrayList("Sort by Frequency", "Sort A-Z"));
        wordSortBox.setItems(FXCollections.observableArrayList("Sort by Frequency", "Sort A-Z"));

        letterSortBox.setValue("Sort by Frequency");
        wordSortBox.setValue("Sort by Frequency");

        letterSortBox.setOnAction(e -> updateUI());
        wordSortBox.setOnAction(e -> updateUI());

        updateUI();
    }

    public void setUser(String username) {
        this.currentUser = username;
        updateUI();
    }

    private void updateUI() {
        userNameDisplay.setText(currentUser + "'s Statistics");
        refreshStats();

        Map<String, Integer> letterFrequencies = UserStatisticsDAO.getUserLetterFrequencies(currentUser);
        Map<String, Integer> wordFrequencies = UserStatisticsDAO.getUserWordFrequencies(currentUser);

        List<String> sortedLetters = sortMap(letterFrequencies, letterSortBox.getValue());
        List<String> sortedWords = sortMap(wordFrequencies, wordSortBox.getValue());

        topLettersList.setItems(FXCollections.observableArrayList(sortedLetters));
        topWordsList.setItems(FXCollections.observableArrayList(sortedWords));
    }

    public void refreshStats() {
        String query = "SELECT average_guesses, games_played FROM user_statistics WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double avg = rs.getDouble("average_guesses");
                int games = rs.getInt("games_played");

                averageGuessesText.setText(String.format("%.2f", avg));
                gamesPlayedText.setText(String.valueOf(games));
            } else {
                averageGuessesText.setText("0.00");
                gamesPlayedText.setText("0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            averageGuessesText.setText("0.00");
            gamesPlayedText.setText("0");
        }
    }

    private List<String> sortMap(Map<String, Integer> map, String sortOption) {
        if (sortOption == null) sortOption = "Sort by Frequency";

        return map.entrySet().stream()
                .sorted(
                        sortOption.equals("Sort A-Z")
                                ? Map.Entry.comparingByKey()
                                : Map.Entry.<String, Integer>comparingByValue().reversed()
                )
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
    }
}
