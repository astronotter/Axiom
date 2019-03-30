package axiom;

import java.util.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.application.Application;

class QuizStage extends Stage {
    public QuizStage() {
        FlowPane pane = new FlowPane();
        Scene scene = new Scene(pane, 500, 400);
        
        TextArea answerField = new TextArea();        
        pane.getChildren().addAll(answerField);
        
        this.setScene(scene);
        this.setTitle("Quiz");
    }
}

class AddEditStage extends Stage {    
    public AddEditStage() {
        FlowPane pane = new FlowPane();
        Scene scene = new Scene(pane, 500, 400);
        
        TextArea answerField = new TextArea();        
        pane.getChildren().addAll(answerField);
        
        this.setScene(scene);
        this.setTitle("Add/Edit Question");
    }
}

class AxiomStage extends Stage {
    AddEditStage addEditStage;
    QuizStage quizStage;
    
    public AxiomStage() {
        this.addEditStage = new AddEditStage();
        this.quizStage = new QuizStage();
        
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
        
        ListView<String> questionList = new ListView<String>();
        questionList.prefWidthProperty().bind(this.widthProperty());
        
        ToolBar toolBar = new ToolBar();
        TextField filterField = new TextField();
        filterField.setPromptText("Question Filters");
        filterField.setOnKeyTyped(
            ev -> refilter(questionList.getItems(),
                           filterField.getCharacters().toString()));
        toolBar.prefWidthProperty().bind(this.widthProperty());
        Button addButton = new Button("+");
        addButton.setOnAction(ev -> addEditStage.show());
        Button quizButton = new Button("Quiz");
        toolBar.getItems().addAll(filterField, addButton, quizButton);
        
        pane.getChildren().addAll(menuBar, toolBar, questionList);
        
        this.setTitle("Axiom");
        this.setScene(scene);
        
        // Populate the list with no filter
        refilter(questionList.getItems(), filterField.getCharacters().toString());
    }
    // Filter text has changed, we need to update the question list accordingly
    void refilter(ObservableList<String> list, String filter) {
        Question questions[] = Axiom.getInstance()
                                    .getDB()
                                    .select(new Question[0])
                                    .toArray(Question[]::new);
        for (Question question : questions)
            list.add(question.getText());
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