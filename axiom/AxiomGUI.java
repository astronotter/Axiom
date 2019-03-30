package axiom;

import java.util.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.application.Application;

class QuizStage extends Stage {
    public QuizStage(List<Question> questions) {
        FlowPane pane = new FlowPane();
        Scene scene = new Scene(pane, 500, 400);
        
        TextArea answerField = new TextArea();        
        pane.getChildren().addAll(answerField);
        
        this.setScene(scene);
        this.setTitle("Quiz");
    }
}

class AddEditStage extends Stage {    
    public AddEditStage(Question question) {
        FlowPane pane = new FlowPane();
        Scene scene = new Scene(pane, 500, 400);
        
        TextArea answerField = new TextArea((question == null)? "" : question.toString());
        pane.getChildren().addAll(answerField);
        
        this.setScene(scene);
        this.setTitle("Add/Edit Question");
    }
}

class AxiomStage extends Stage {
    public AxiomStage() {
        FlowPane pane = new FlowPane();
        Scene scene = new Scene(pane, 500, 400);
        
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(new MenuItem("Open"));
        fileMenu.getItems().add(new MenuItem("Save"));
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(new MenuItem("About"));
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        menuBar.prefWidthProperty().bind(this.widthProperty());
        
        ListView<Question> questionList = new ListView<Question>();
        questionList.prefWidthProperty().bind(this.widthProperty());
        questionList.prefHeightProperty().bind(this.heightProperty());
        questionList.setCellFactory(view -> new ListCell<Question>() {  // [so 36657299]
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);
                setText((!empty && question != null)? question.toString() : null);
            }
        });
        questionList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                if (ev.getClickCount() == 2)
                    edit(questionList.getSelectionModel().getSelectedItems().get(0));
            }
        });
        
        ToolBar toolBar = new ToolBar();
        TextField filterField = new TextField();
        filterField.setPromptText("Question Filters");
        filterField.setOnKeyTyped(
            ev -> refilter(questionList.getItems(),
                           filterField.getCharacters().toString()));
        toolBar.prefWidthProperty().bind(this.widthProperty());
        Button addButton = new Button("+");
        addButton.setOnAction(ev -> add());
        Button quizButton = new Button("Quiz");
        quizButton.setOnAction(ev -> quiz());
        toolBar.getItems().addAll(filterField, addButton, quizButton);
        
        pane.getChildren().addAll(menuBar, toolBar, questionList);
        
        this.setTitle("Axiom");
        this.setScene(scene);
        
        // Populate the list with no filter
        refilter(questionList.getItems(), filterField.getCharacters().toString());
    }
    // Filter text has changed, we need to update the question list accordingly
    void refilter(ObservableList<Question> list, String filter) {
        list.setAll(Axiom.getInstance()
                         .getDB()
                         .select(new Question[0])
                         .toArray(Question[]::new));
    }
    void edit(Question question) {
        AddEditStage addEditStage = new AddEditStage(question);
        addEditStage.showAndWait();
    }
    void add() {
        AddEditStage addEditStage = new AddEditStage(null);
        addEditStage.showAndWait();
        // refilter();
    }
    void quiz() {
        QuizStage quizStage = new QuizStage(null);
        quizStage.showAndWait();
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