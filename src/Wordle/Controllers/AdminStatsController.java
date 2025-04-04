package Wordle.Controllers;
import Wordle.Statistics.UserStatisticsDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminStatsController {

    @FXML
    private ListView<String> topLettersList;
    @FXML
    private ListView<String> leastLettersList;
    @FXML
    private ListView<String> topWordsList;
    @FXML
    private ListView<String> leastWordsList;
    @FXML
    private ListView<String> usersList;

    @FXML
    private void initialize() {
        // Populate all letters sorted by frequency
        Map<String, Integer> letterFrequencies = UserStatisticsDAO.getLetterFrequencies();
        List<String> topLetters = letterFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        topLettersList.getItems().addAll(topLetters);

        // Populate all letters sorted by least frequency
        List<String> leastLetters = letterFrequencies.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        leastLettersList.getItems().addAll(leastLetters);

        // Populate all words sorted by frequency
        Map<String, Integer> wordFrequencies = UserStatisticsDAO.getWordFrequencies();
        List<String> topWords = wordFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        topWordsList.getItems().addAll(topWords);

        // Populate all words sorted by least frequency
        List<String> leastWords = wordFrequencies.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        leastWordsList.getItems().addAll(leastWords);

        // Populate all users
        List<String> allUsers = UserStatisticsDAO.getAllUsers();
        usersList.getItems().addAll(allUsers);
    }
}