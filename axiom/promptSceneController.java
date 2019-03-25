package sample;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class promptSceneController
{
    @FXML
    private Button submitButton, passButton, endButton;

    @FXML
    private TextField answerText;

    @FXML
    private Label questionLabel;

    @FXML
    private void submit()
    {
        submitButton.setOnAction((event) ->
        {
            //Takes what's in answer text field and checks to see if it matches answer to question in label
        });

    }

    @FXML
    private void pass()
    {
        passButton.setOnAction((event) ->
        {
            //Changes current question
        });

    }

    @FXML
    private void end()
    {
        endButton.setOnAction((event) ->
        {
            try {
                Parent switching = FXMLLoader.load(getClass().getResource("startScene.fxml"));
                Scene change = new Scene(switching);
                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                stage.setScene(change);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}