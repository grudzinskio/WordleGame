package Wordle.Controllers;

import Wordle.Vocabulary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class AdminController {
    // Vocabulary instance
    private final Vocabulary vocabulary = Vocabulary.getVocabulary();

    // Display current file paths for each
    @FXML
    public TextField refrenceWordCurrentFile;
    @FXML
    public TextField guessWordCurrentFile;

    @FXML
    public ListView refrenceWordList;
    @FXML
    public ListView guessWordList;

    // Separate queues for file history for each type
    private final Queue<String> refFilePaths = new LinkedList<>();
    private final Queue<String> guessFilePaths = new LinkedList<>();

    @FXML
    public void initialize() {
        String initialPath = vocabulary.getFilePath();
        refrenceWordCurrentFile.setText(initialPath);
        guessWordCurrentFile.setText(initialPath);
        refFilePaths.add(initialPath);
        guessFilePaths.add(initialPath);
    }

    /**
     * Changes the vocab file for reference words.
     * @param actionEvent when button clicked.
     */
    public void changeFileReferenceWord(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                if (validateFile(file)) {
                    // Save current reference file path
                    refFilePaths.add(vocabulary.getFilePath());
                    vocabulary.loadWords(file.getPath());
                    refrenceWordCurrentFile.setText(vocabulary.getFilePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Changes the vocab file for guess words.
     * @param actionEvent when button clicked.
     */
    public void changeFileGuessWord(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                if (validateFile(file)) {
                    // Save current guess file path
                    guessFilePaths.add(vocabulary.getFilePath());
                    vocabulary.loadWords(file.getPath());
                    guessWordCurrentFile.setText(vocabulary.getFilePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determines if an inputted file is the correct format.
     * @param file file to be checked.
     * @return boolean value on file status.
     */
    private boolean validateFile(File file) {
        if (!String.valueOf(file).endsWith(".txt")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong file type, must be .txt");
            alert.show();
            return false;
        }
        return true;
    }

    /**
     * Button action to undo the reference file change.
     * @param actionEvent when button clicked.
     */
    public void undoRefFileChange(ActionEvent actionEvent) {
        if (refFilePaths.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to Undo");
            alert.show();
        } else {
            String currentPath = vocabulary.getFilePath();
            vocabulary.loadWords(refFilePaths.poll());
            refrenceWordCurrentFile.setText(vocabulary.getFilePath());
            refFilePaths.add(currentPath);
        }
    }

    /**
     * Button action to undo the guess file change.
     * @param actionEvent when button clicked.
     */
    public void undoGuessFileChange(ActionEvent actionEvent) {
        if (guessFilePaths.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to Undo");
            alert.show();
        } else {
            String currentPath = vocabulary.getFilePath();
            vocabulary.loadWords(guessFilePaths.poll());
            guessWordCurrentFile.setText(vocabulary.getFilePath());
            guessFilePaths.add(currentPath);
        }
    }
}