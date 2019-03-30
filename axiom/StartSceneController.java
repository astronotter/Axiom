package axiom;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;

import java.io.IOException;

public class StartSceneController {
    private Button addButton, startButton, filterButton;
    private TextField filterText;

    @FXML
    private void filter() {
        // Some filtering function here
    }
    @FXML
    private void add(Event event) {
        try {
            Parent switching = FXMLLoader.load(getClass().getResource("addEditScene.fxml"));
            Scene change = new Scene(switching);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(change);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void start(Event event) {
        try {
            Parent switching = FXMLLoader.load(getClass().getResource("promptScene.fxml"));
            Scene change = new Scene(switching);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(change);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
