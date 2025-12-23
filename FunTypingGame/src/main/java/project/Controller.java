package project;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane mainPane;
    
    @FXML
    private ComboBox<String> fontCbo;

    @FXML
    private FlowPane scriptPane;

    @FXML
    private TextArea typeTxt;

    @FXML
    void initialize() {
    	fontCbo.getItems().addAll("Small", "Medium", "Big");
    }
    
    /**
     * Allow user to select a script file. Show the script in the script TextArea.
     * @param event
     */
    @FXML
    void uploadScript(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
		File projectDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setInitialDirectory(projectDirectory);
    	FileChooser fc = new FileChooser();
    	File f = fc.showOpenDialog(null);
    	
    	Scanner reader = null;
		try {
			reader = new Scanner(f);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    	
    	try {
			while (true) {
				String line = reader.nextLine();
				String[] letters = line.split("");
				for (String l: letters) {
					Label letLbl = new Label(l);
					scriptPane.getChildren().add(letLbl);
				}
			}
    	
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

    	typeTxt.setEditable(true);
    }
    
    @FXML
    void endGame(ActionEvent event) {
    	
    }

    @FXML
    void setFont(ActionEvent event) {
    	for (String cssClass: fontCbo.getItems()) {
    		cssClass = cssClass.toLowerCase();
    		if (cssClass.equals(fontCbo.getValue())) {
    			for (Node n: scriptPane.getChildren()) {
    				n.getStyleClass().add(cssClass);    				        			
            	}
    			typeTxt.getStyleClass().add(cssClass);
			}
			else {
				for (Node n: scriptPane.getChildren()) {
    				n.getStyleClass().remove(cssClass);    				        			
            	}
    			typeTxt.getStyleClass().remove(cssClass); 
			}	
    	}
    }

    @FXML
    void updateTyping(KeyEvent event) {
    	String typed = typeTxt.getText();
    	char[] letters = typed.toCharArray();
    	
    	for (int i = 0; i < letters.length; i++) {
    		String letter = letters[i] + "";
    		Label letterLbl = (Label)(scriptPane.getChildren().get(i));
    		String scriptLetter = letterLbl.getText();
    		
    		//Correctly typed
    		if (letter.equals(scriptLetter)) {
    			letterLbl.getStyleClass().add("correct");
    			letterLbl.getStyleClass().remove("incorrect");
    		}
    		else {
    			letterLbl.getStyleClass().add("incorrect");
    			letterLbl.getStyleClass().remove("correct");
    		}
    	}

    }

    

    

}
