package project;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Controller {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private HBox dogsBox;

	@FXML
	private Button guessBtn;

	@FXML
	private TextField guessTxt;

	@FXML
	private HBox guessedBox;

	@FXML
	private HBox lettersBox;

	@FXML
	private Label endLbl;
	
	private char[] word;
	private ArrayList<Label> letterLbls;
	private ArrayList<Character> guessedLetters;
	private int remainingGuesses = 5;

	
	@FXML
	void initialize() {
		assert dogsBox != null : "fx:id=\"dogsBox\" was not injected: check your FXML file 'main.fxml'.";
		assert guessBtn != null : "fx:id=\"guessBtn\" was not injected: check your FXML file 'main.fxml'.";
		assert guessTxt != null : "fx:id=\"guessTxt\" was not injected: check your FXML file 'main.fxml'.";
		assert guessedBox != null : "fx:id=\"guessedBox\" was not injected: check your FXML file 'main.fxml'.";
		assert lettersBox != null : "fx:id=\"lettersBox\" was not injected: check your FXML file 'main.fxml'.";

		word = "gazebo".toCharArray();

		for (int i = 0; i < remainingGuesses; i++) {
			ImageView dogImg = new ImageView(new Image("file:Dog" + (i + 1) + ".png"));
			dogImg.setPreserveRatio(true);
			dogImg.setFitHeight(115);
			dogsBox.getChildren().add(dogImg);
		}
		
		letterLbls = new ArrayList<>();
		for (char letter: word) {
			Label wordLbl = new Label("_");
			wordLbl.setPadding(new Insets(10));
			letterLbls.add(wordLbl);
			lettersBox.getChildren().add(wordLbl);
		}
		
		guessedLetters = new ArrayList<>();

	}

	@FXML
	void guess(ActionEvent event) {
		char guessedLetter = guessTxt.getText().charAt(0);

		boolean foundLetter = false;

		for (int i = 0; i < word.length; i++) {
			if (guessedLetter == word[i]) {
				foundLetter = true;
				letterLbls.get(i).setText(guessedLetter + "");
			}
		}
		
		boolean won = true;
		for (Label letterLbl: letterLbls){
			if (letterLbl.getText().charAt(0) == '_') {
				won = false;
			}
		}
		if (won) {
			endLbl.setText("You Win!");
		}
		
		if (!foundLetter) {
			if (!guessedLetters.contains(guessedLetter)) {
				remainingGuesses--;
				guessedLetters.add(guessedLetter);
				guessedBox.getChildren().add(new Label(guessedLetter + ""));
				dogsBox.getChildren().remove(dogsBox.getChildren().size() - 1);
				if (remainingGuesses == 0) {
					endLbl.setText("You Lose!");
				}
			}
		}
	}



}
