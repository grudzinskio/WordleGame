// java
package Wordle.Controllers;

import Wordle.DatabaseManager;
import Wordle.Statistics.UserStats;
import Wordle.Statistics.UserStatisticsDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatDisplayController {

    public static StatDisplayController instance;

    @FXML
    public Label userNameDisplay;
    @FXML
    public ListView<String> topLettersList;
    @FXML
    public ListView<String> topWordsList;
    @FXML
    public Label averageGuessesText;

    // Use this field to hold the user whose stats are to be displayed.
    private String currentUser;

    @FXML
    private void initialize() {
        instance = this;
        // If not set externally, use the logged-in user.
        if (currentUser == null) {
            currentUser = UserStats.getInstance().getUsername();
        }
        updateUI();
    }

    // Called by AdminStatsController to set the user whose stats should be shown.
    public void setUser(String username) {
        this.currentUser = username;
        updateUI();
    }

    // Update the display using the currentUser
    private void updateUI() {
        userNameDisplay.setText(currentUser + "'s Statistics");
        refreshStats();

        // Retrieve and sort the user's letter frequencies in descending order.
        Map<String, Integer> letterFrequencies = UserStatisticsDAO.getUserLetterFrequencies(currentUser);
        List<String> sortedLetters = letterFrequencies.entrySet().stream()
                .sorted(Comparator.comparing((java.util.Map.Entry<String, Integer> e) -> e.getValue()).reversed())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        topLettersList.setItems(FXCollections.observableArrayList(sortedLetters));

        // Retrieve and sort the user's word frequencies in descending order.
        Map<String, Integer> wordFrequencies = UserStatisticsDAO.getUserWordFrequencies(currentUser);
        List<String> sortedWords = wordFrequencies.entrySet().stream()
                .sorted(Comparator.comparing((java.util.Map.Entry<String, Integer> e) -> e.getValue()).reversed())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
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
                averageGuessesText.setText(String.format("%.2f", avg));
            } else {
                averageGuessesText.setText("0.00");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            averageGuessesText.setText("0.00");
        }
    }
}