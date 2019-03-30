package axiom;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.stage.*;

import java.io.IOException;

public class PromptSceneController {
    private Button submitButton, passButton, endButton;
    private TextField answerText;
    private Label questionLabel;

    @FXML
    private void submit() {
        //Takes what's in answer text field and checks to see if it matches answer to question in label
    }
    @FXML
    private void pass() {
        //Changes current question
    }
    @FXML
    private void end(Event event) {
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