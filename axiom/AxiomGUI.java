package axiom;

import java.util.*;
import java.util.stream.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.application.Application;

class AxiomStage extends Stage {
    FlowPane pane = new FlowPane();
    Scene scene = new Scene(pane, 500, 400);
    MenuBar menuBar = new MenuBar();
    Menu editMenu = new Menu("Edit");
    Menu fileMenu = new Menu("File");
    Menu helpMenu = new Menu("Help");
    MenuItem openMenuItem = new MenuItem("Open");
    MenuItem saveMenuItem = new MenuItem("Save");
    MenuItem aboutMenuItem = new MenuItem("About");
    MenuItem deleteMenuItem = new MenuItem("Delete");
    ListView<Question> questionList = new ListView<Question>();
    TextField filterField = new TextField();
    Button addButton = new Button("+");
    ToolBar toolBar = new ToolBar();
    Button quizButton = new Button("Quiz");
    
    public AxiomStage() {
        this.fileMenu.getItems().add(openMenuItem);
        this.fileMenu.getItems().add(saveMenuItem);
        this.deleteMenuItem.setOnAction(ev -> delete());
        this.editMenu.getItems().add(deleteMenuItem);
        this.helpMenu.getItems().add(aboutMenuItem);
        this.menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        this.menuBar.prefWidthProperty().bind(this.widthProperty());
        this.questionList.prefWidthProperty().bind(this.widthProperty());
        this.questionList.prefHeightProperty().bind(this.heightProperty());
        this.questionList.setCellFactory(view -> new ListCell<Question>() {  // [so 36657299]
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);
                setText((!empty && question != null)?
                    question.toString() : null);
            }
        });
        this.questionList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                if (ev.getClickCount() == 2) {
                    Question selected = questionList.getSelectionModel()
                        .getSelectedItems().get(0);
                    EditStage.prompt(selected);
                    refilter();
                }
            }
        });
        this.filterField.setPromptText("Filter");
        this.filterField.textProperty().addListener(ev -> refilter());
        this.toolBar.prefWidthProperty().bind(this.widthProperty());
        this.addButton.setOnAction(ev -> {
            EditStage.prompt(null);
            refilter();
        });
        this.quizButton.setOnAction(ev -> {
            QuizStage.prompt(questionList.getItems().iterator());
            refilter();
        });
        this.toolBar.getItems().addAll(filterField, addButton, quizButton);
        this.pane.getChildren().addAll(menuBar, toolBar, questionList);
        this.setTitle("Axiom");
        this.setScene(scene);
        
        // Populate the list.
        refilter();
    }
    void delete() {
        final Question selected = questionList.getSelectionModel().getSelectedItem();
        Axiom.getInstance().getDB().remove(selected);
        
        // Refresh the list.
        refilter();
    }
    // Filter text has changed, we need to update the question list accordingly.
    void refilter() {
        final String filter = this.filterField.getText();
        final List<Question> questions = Axiom.getInstance().listQuestions(filter);
        
        // If the list is empty, grey out the quiz button.
        this.quizButton.disableProperty().set(questions.isEmpty());
        
        // Set to an empty list and then back, this forces a refresh.
        this.questionList.setItems(FXCollections.observableArrayList());
        this.questionList.setItems(FXCollections.observableArrayList(questions));
    }
}

public class AxiomGUI extends Application {
    private AxiomStage stage;
    
    public AxiomGUI() {
    }
    public AxiomStage getStage() {
        return this.stage;
    }
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = new AxiomStage();
        this.stage.show();
    }
}