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
    private ListView<Question> questionList;
    private TextField filterField;
    private Button quizButton;
    private ListIterator<Question> quiz;
    Question currentQuestion;
    
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
                setText((!empty && question != null)?
                    question.toString() : null);
            }
        });
        questionList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                if (ev.getClickCount() == 2) {
                    edit(questionList.getSelectionModel()
                                     .getSelectedItems()
                                     .get(0));
                }
            }
        });
        
        ToolBar toolBar = new ToolBar();
        filterField = new TextField();
        filterField.setPromptText("Filter");
        filterField.textProperty().addListener(ev -> refilter());
        toolBar.prefWidthProperty().bind(this.widthProperty());
        Button addButton = new Button("+");
        addButton.setOnAction(ev -> { edit(null); refilter(); });
        quizButton = new Button("Quiz");
        quizButton.setOnAction(ev -> { quiz(); refilter(); });
        toolBar.getItems().addAll(filterField, addButton, quizButton);
        
        pane.getChildren().addAll(menuBar, toolBar, questionList);
        
        this.setTitle("Axiom");
        this.setScene(scene);
        
        // Populate the list with no filter.
        refilter();
    }
    // Filter text has changed, we need to update the question list accordingly.
    void refilter() {
        String filter = filterField.getText();
        List<Question> questions = Axiom.getInstance().getDB()
          .select(new Question[0])
          .filter(question -> CategoryFilter.passes(filter, question))
          .collect(Collectors.toList());
        
        // If the list is empty, grey out the quiz button.
        quizButton.disableProperty().set(questions.isEmpty());
        
        // Set to an empty list and then back, this forces a refresh.
        questionList.setItems(FXCollections.observableArrayList());
        questionList.setItems(FXCollections.observableArrayList(questions));
    }
    void edit(Question question) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        
        TextArea textArea = new TextArea(question == null? "" : question.getText());
        TextArea answerArea = new TextArea(question == null? "" : question.getAnswer());
        TextField tagField = new TextField();
        
        Button okButton = new Button("OK");
        okButton.setOnAction(ev -> {
            Question q = question;
            if (q == null) {
                q = new Question();
                Axiom.getInstance().getDB().insert(q);
            }
            q.setText(textArea.getText());
            q.setAnswer(answerArea.getText());
            stage.close();
            refilter();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(ev -> stage.close());
        
        VBox vbox = new VBox(textArea, answerArea, tagField,
            new HBox(okButton, cancelButton));
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        
        stage.setScene(new Scene(vbox));
        stage.setTitle("Add/Edit Question");
        stage.showAndWait();
    }
    
    void quiz() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        
        quiz = questionList.getItems().listIterator();
        currentQuestion = quiz.next();
        
        Label textLabel = new Label(currentQuestion.getText());
        Label answerLabel = new Label();
        
        Button answerButton = new Button("Answer");
        answerButton.setOnAction(ev ->
            answerLabel.setText(currentQuestion.getAnswer()));
        Button nextButton = new Button("Next");
        nextButton.setOnAction(ev -> {
            // Setup the next question, and hide the answer.
            currentQuestion = quiz.next();
            textLabel.setText(currentQuestion.getText());
            answerLabel.setText("");
            
            // If we run out of questions then grey out the next button.
            if (!quiz.hasNext())
                nextButton.disableProperty().set(true);
        });
        Button finishButton = new Button("Finish");
        finishButton.setOnAction(ev -> stage.close());
        
        VBox vbox = new VBox(textLabel, answerLabel,
            new HBox(answerButton, nextButton, finishButton));
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        
        stage.setScene(new Scene(vbox));
        stage.setTitle("Quiz");
        stage.showAndWait();
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