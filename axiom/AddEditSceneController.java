package axiom;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;

import java.io.IOException;

public class AddEditSceneController {
    private Button okButton, backButton;
    private TextField answerText, tagText, questionText;

    @FXML
    private void ok() {
        //Applies changes made to either the question, answers or tags
    }
    @FXML
    private void back(Event event) {
        try {
            Parent switching = FXMLLoader.load(getClass().getResource("startScene.fxml"));
            Scene change = new Scene(switching);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(change);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}