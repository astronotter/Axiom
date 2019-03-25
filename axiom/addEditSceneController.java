package sample;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class addEditSceneController
{
    @FXML
    private Button okButton, backButton;

    @FXML
    private TextField answerText, tagText, questionText;

    @FXML
    private void ok()
    {
        okButton.setOnAction((event) ->
        {
            //Applies changes made to either the question, answers or tags
        });

    }

    @FXML
    private void back()
    {
        backButton.setOnAction((event) ->
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
