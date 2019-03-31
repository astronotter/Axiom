package axiom;

import java.util.*;
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
    private ListView<Question> questionList;
    private TextField filterField;
    
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
        
        questionList = new ListView<Question>();
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
                if (ev.getClickCount() == 2) {
                    edit(questionList.getSelectionModel()
                                     .getSelectedItems()
                                     .get(0));
                    refilter();
                }
            }
        });
        
        ToolBar toolBar = new ToolBar();
        filterField = new TextField();
        filterField.setPromptText("Question Filters");
        filterField.setOnKeyTyped(ev -> refilter());
        toolBar.prefWidthProperty().bind(this.widthProperty());
        Button addButton = new Button("+");
        addButton.setOnAction(ev -> { add(); refilter(); });
        Button quizButton = new Button("Quiz");
        quizButton.setOnAction(ev -> { quiz(); refilter(); });
        toolBar.getItems().addAll(filterField, addButton, quizButton);
        
        pane.getChildren().addAll(menuBar, toolBar, questionList);
        
        this.setTitle("Axiom");
        this.setScene(scene);
        
        // Populate the list with no filter
        refilter();
    }
    // Filter text has changed, we need to update the question list accordingly
    void refilter() {
        ObservableList<Question> questions = FXCollections.observableArrayList(
            Axiom.getInstance()
                 .getDB()
                 .select(new Question[0])
                 .toArray(Question[]::new));
        questionList.setItems(questions);
    }
    void edit(Question question) {
        TextArea textArea = new TextArea(question.getText());
        TextArea answerArea = new TextArea(question.getAnswer());
        TextField tagField = new TextField();
        
        VBox vbox = new VBox(textArea, answerArea, tagField);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(vbox));
        stage.setTitle("Add/Edit Question");
        stage.showAndWait();
        
        question.setText(textArea.getText());
        question.setAnswer(answerArea.getText());
    }
    void add() {
        Question question = new Question("", "");
        edit(question);
        Axiom.getInstance().getDB().insert(question);
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