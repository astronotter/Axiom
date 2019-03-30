package axiom;

import java.util.*;
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
    AddEditStage addEditStage = new AddEditStage();
    QuizStage quizStage = new QuizStage();
    
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
        
        ToolBar toolBar = new ToolBar();
        TextField filterField = new TextField();
        filterField.setPromptText("Question Filters");
        toolBar.prefWidthProperty().bind(this.widthProperty());
        Button addButton = new Button("+");
        addButton.setOnAction(ev -> addEditStage.show());
        Button quizButton = new Button("Quiz");
        toolBar.getItems().addAll(filterField, addButton, quizButton);
        
        pane.getChildren().addAll(menuBar, toolBar);
        
        this.setTitle("Axiom");
        this.setScene(scene);
    }
}

public class Axiom extends Application {
    private FlatDB db;
    private AxiomStage stage;

    public Axiom() throws Exception {
        this.db = new FlatDB("axiom-db.xml");
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = new AxiomStage();
        this.stage.show();
    }
    
    public void save() throws Exception {
        this.db.save();
    }
    public void list() {
        Question questions[] = db.select(new Question[0])
                               .toArray(Question[]::new);
        for (Question question : questions) {
            System.out.println(String.format("%s:%s",
            question.getID(), question.getText()));
        }
    }
    public UUID create(String text, String answer) {
      return db.insert(new Question(text, answer)).getID();
    }
    public void categorize(UUID questionID, String categoryNames[]) {
        for (String categoryName : categoryNames) {
            UUID categoryID = db.select(new Category[0])
                 .filter(category -> category.getName() == categoryName)
                 .findFirst()
                 .orElse(db.insert(new Category(categoryName)))
                 .getID();
            db.insert(new Categorize(categoryID, questionID));
        }
    }
    public static void main(String []args) throws Exception {
        Axiom program = new Axiom();

        if (args.length == 0) {
            program.launch();
            return;
        }
        switch (args[0]) {
            case "list":
                program.list();
                break;
            case "create": {
                if (args.length < 3) {
                   System.err.println("Invalid number of arguments.");
                   return;
                }
                UUID questionID = program.create(args[1], args[2]);
                program.categorize(questionID, Arrays.copyOfRange(args, 3, args.length));
                break;
            }
            case "assign":
                if (args.length < 2) {
                   System.err.println("Invalid number of arguments.");
                   return;
                }
                UUID questionID = UUID.fromString(args[1]);
                program.categorize(questionID, Arrays.copyOfRange(args, 2, args.length));
                break;
            case "help":
                System.err.println(String.format(
                   "Usage: java -jar Axiom [COMMAND ...]"
                 + "%nCommands:"
                 + "%n  create QUESTION ANSWER [CATEGORY1 ...]"
                 + "%n  assign ITEM CATEGORY1 [CATEGORY2 ...]"
                 + "%n  list"
                 + "%n  help"));
                break;
            default:
                System.err.println("Invalid command.");
                return;
        }
        program.save();
    }
}