package project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Controller {

    @FXML
    private VBox itemBox;

    @FXML
    private TextField itemTxt;

    @FXML
    void addToDoItem(ActionEvent event) {
    	
    	Label toDoStr = new Label(itemTxt.getText());
    	CheckBox toDoChk = new CheckBox();
    	
    	HBox toDoBox = new HBox(toDoStr, toDoChk);
    	
    	toDoChk.setOnAction(e -> {
    		
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		itemBox.getChildren().remove(toDoBox);
    	});
    	
    	itemBox.getChildren().add(toDoBox);
    	
    }

}
