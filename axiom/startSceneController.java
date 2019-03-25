package sample;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class startSceneController
{
    @FXML
    private Button addButton, startButton, filterButton;

    @FXML
    private TextField filterText;

    @FXML
    private void filter()
    {
        filterButton.setOnAction((event) ->
        {
            //Some filtering function here
        });

    }

    /** Dedicated to the button actions that changes the scene **/
    @FXML
    private void switchScene()
    {
        addButton.setOnAction((event) ->
        {
            try {
                Parent switching = FXMLLoader.load(getClass().getResource("addEditScene.fxml"));
                Scene change = new Scene(switching);
                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                stage.setScene(change);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        startButton.setOnAction((event) ->
        {
            try {
                Parent switching = FXMLLoader.load(getClass().getResource("promptScene.fxml"));
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
