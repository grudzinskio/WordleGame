package Wordle.Controllers;

import Wordle.Vocabulary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;


public class AdminController {
    // vocabulary instance
    private final Vocabulary vocabulary = Vocabulary.getVocabulary();
    public TextField fileName;
    private final Queue<String> filePaths = new LinkedList<>();

    @FXML
    public void initialize() {
        fileName.setText(vocabulary.getFilePath());
        filePaths.add(vocabulary.getFilePath());
    }

    /**
     * changes the vocab file in the game
     * @param actionEvent when button clicked
     */
    public void changeFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if(file != null) {
            try {
                if(validateFile(file)) {
                    vocabulary.loadWords(file.getPath());
                    fileName.setText(vocabulary.getFilePath());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Determine if an inputted file is the correct format or not
     * @param file file to be checked
     * @return boolean value on file status
     */
    private boolean validateFile(File file) {
        if(!String.valueOf(file).endsWith(".txt")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong file type, must be .txt");
            alert.show();
            return false;
        }
        return true;
    }

    /**
     * Button action to undo a vocab file change
     * @param actionEvent when button clicked
     */
    public void undoFileChange(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to Undo");
        //make sure an undo action is possible
        if(filePaths.isEmpty()) {
            alert.show();
        } else {
            String path = vocabulary.getFilePath();
            vocabulary.loadWords(filePaths.poll());
            fileName.setText(vocabulary.getFilePath());
            filePaths.add(path);
        }
    }
}
