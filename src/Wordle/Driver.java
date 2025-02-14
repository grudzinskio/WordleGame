package Wordle;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:30:53 PM
 */
public class Driver extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	public Vocabulary Vocabulary;

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Wordle.fxml"));
		Pane root = loader.load();
		Label label = new Label("Hello Wordle");
		root.getChildren().add(label);
		stage.setTitle("Wordle");
		stage.setScene(new Scene(root));
		stage.show();
	}

}