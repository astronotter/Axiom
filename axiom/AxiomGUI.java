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
        Menu editMenu = new Menu("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(ev -> delete());
        editMenu.getItems().add(deleteMenuItem);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(new MenuItem("About"));
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
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
    void delete() {
        Question selected = questionList.getSelectionModel().getSelectedItem();
        Axiom.getInstance().getDB().remove(selected);
        
        // Refresh the list.
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
        final FlatDB db = Axiom.getInstance().getDB();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        
        TextArea textArea = new TextArea(question == null? "" : question.getText());
        TextArea answerArea = new TextArea(question == null? "" : question.getAnswer());
        TextField tagField = new TextField();
        if (question != null) {
            db.select(new Categorize[0])
              .filter(cat -> cat.getElementID().equals(question.getID()))
              .forEach(cat -> {
                  db.select(new Category[0])
                    .filter(cat2 -> cat2.getID().equals(cat.getCategoryID()))
                    .forEach(cat2 -> tagField.setText(
                        (tagField.getText().length() != 0) ?
                            tagField.getText() + "," + cat2.getName()
                                : cat2.getName()));
              });
        }
        
        Button okButton = new Button("OK");
        okButton.setOnAction(ev -> {
            final Question q = (question != null)? question : db.insert(new Question());
            q.setText(textArea.getText());
            q.setAnswer(answerArea.getText());
            
            String tags[] = tagField.getText().split(",");
            List<UUID> tagIDs = new ArrayList<UUID>();
            for (String tag : tags) {
                if (tag.equals(""))
                    continue;
                Category category =
                    db.select(new Category[0])
                      .filter(cat -> cat.getName().equals(tag))
                      .findAny()
                      .orElseGet(() -> db.insert(new Category(tag)));
                db.select(new Categorize[0])
                  .filter(cat -> cat.getCategoryID().equals(category.getID()))
                  .findAny()
                  .orElseGet(() -> db.insert(new Categorize(category.getID(), q.getID())));
                
                // Grab the id for the category while we are at it. We need this
                // for tracking and removing any straggler categories attached
                // to the question.
                tagIDs.add(category.getID());
            }
            db.select(new Categorize[0])
              .filter(cat -> cat.getElementID().equals(q.getID())
                  && !tagIDs.contains(cat.getCategoryID()))
              .forEach(cat -> db.remove(cat));
            
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