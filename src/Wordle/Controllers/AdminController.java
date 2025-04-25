package Wordle.Controllers;

import Wordle.Vocabulary;
import Wordle.WordleGame;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AdminController {
    private final Vocabulary vocabulary = Vocabulary.getVocabulary();

    @FXML public TextField refrenceWordCurrentFile;
    @FXML public ListView<String> guessWordList;
    @FXML private Label statusLabel;

    private final Queue<String> refFilePaths = new LinkedList<>();
    private final Queue<String> guessFilePaths = new LinkedList<>();
    private ObservableList<String> guessWordObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            // set up reference file display
            String initialPath = vocabulary.getReferenceFilePath();
            refrenceWordCurrentFile.setText(new File(initialPath).getName());
            refFilePaths.add(initialPath);

            // populate guess list
            guessWordObservableList.addAll(vocabulary.getGuessableWords());
            guessWordList.setItems(guessWordObservableList);

            // click to remove
            guessWordList.setOnMouseClicked(e -> {
                String w = guessWordList.getSelectionModel().getSelectedItem();
                if (w != null) {
                    vocabulary.removeGuessWord(w);
                    guessWordObservableList.remove(w);
                }
            });
        } catch (Exception e) {
            showErrorAlert("Error during initialization", e);
        }
    }

    public void changeFileGuessWord(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File repoDir = new File(System.getProperty("user.dir"), "repository");
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            repoDir = new File(System.getProperty("user.dir"));
        }
        fileChooser.setInitialDirectory(repoDir);
        File file = fileChooser.showOpenDialog(null);
        if (file != null && validateFile(file)) {
            try {
                guessFilePaths.add(vocabulary.getReferenceFilePath());
                List<String> lines = Files.readAllLines(file.toPath());
                vocabulary.addGuessWords(lines);
                guessWordObservableList.addAll(lines);
            } catch (IOException e) {
                showErrorAlert("Error reading guess words file", e);
            } catch (Exception e) {
                showErrorAlert("Unexpected error while processing the guess words file", e);
            }
        }
    }

    public void changeFileReferenceWord(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File repoDir = new File(System.getProperty("user.dir"), "repository");
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            repoDir = new File(System.getProperty("user.dir"));
        }
        fileChooser.setInitialDirectory(repoDir);
        File file = fileChooser.showOpenDialog(null);
        if (file != null && validateFile(file)) {
            try {
                // backup old path
                refFilePaths.add(vocabulary.getReferenceFilePath());

                // load new words & update display
                vocabulary.loadWords(file.getPath());
                refrenceWordCurrentFile.setText(new File(vocabulary.getReferenceFilePath()).getName());

                // restart game if available
                WordleGame game = WordleGame.getCurrentGame();
                if (game != null) {
                    game.handleRestart();
                }

                showStatusAnimation("Reference file changed!\nNew game started.");
            } catch (Exception e) {
                showErrorAlert("Error updating reference file", e);
            }
        }
    }

    public void undoRefFileChange(ActionEvent actionEvent) {
        if (refFilePaths.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Unable to Undo").show();
        } else {
            try {
                String old = refFilePaths.poll();
                vocabulary.loadWords(old);
                refrenceWordCurrentFile.setText(new File(vocabulary.getReferenceFilePath()).getName());
            } catch (Exception e) {
                showErrorAlert("Error undoing reference file change", e);
            }
        }
    }

    private boolean validateFile(File file) {
        if (!file.getName().endsWith(".txt")) {
            new Alert(Alert.AlertType.ERROR, "Wrong file type, must be .txt").show();
            return false;
        }
        return true;
    }

    /** Fade in → pause → fade out animation for status label */
    private void showStatusAnimation(String message) {
        statusLabel.setText(message);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), statusLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), statusLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> fadeOut.play());

        fadeIn.play();
    }

    private void showErrorAlert(String message, Exception e) {
        new Alert(Alert.AlertType.ERROR, message + ": " + e.getMessage()).show();
        e.printStackTrace();
    }

    @FXML
    public void resetGuessWords(ActionEvent actionEvent) {
        // Reset the vocabulary's guess words to default state
        vocabulary.resetGuessWords();
        // Clear the current list
        guessWordObservableList.clear();
        // Repopulate with the default guess words from vocabulary
        guessWordObservableList.addAll(vocabulary.getGuessableWords());
        // Optionally update the view (if not automatically bound)
        guessWordList.setItems(guessWordObservableList);
    }
}