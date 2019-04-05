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

class EditScriptStage extends Stage {
}

public class EditStage extends Stage {
    Question question;
    
    TextArea textArea = new TextArea();
    TextArea answerArea = new TextArea();
    TextArea scriptArea = new TextArea();
    TextField tagField = new TextField();
    Button okButton = new Button("OK");
    Button cancelButton = new Button("Cancel");
    VBox vbox = new VBox(textArea, answerArea, scriptArea, tagField,
        new HBox(okButton, cancelButton));
    
    public EditStage(Question question) {
        super();
        
        this.question = question;
        if (this.question != null) {
            this.textArea.setText(question.getText());
            this.answerArea.setText(question.getAnswer());
            this.scriptArea.setText(question.getScript());

            String tags = Axiom.getInstance()
                .getCategories(question)
                .stream()
                .map(cat -> cat.getName())
                .collect(Collectors.joining(","));
            this.tagField.setText(tags);
        }
        this.okButton.setOnAction(ev -> apply());
        this.cancelButton.setOnAction(ev -> this.close());
        this.vbox.setAlignment(Pos.CENTER);
        this.vbox.setPadding(new Insets(5, 5, 5, 5));
        this.setScene(new Scene(vbox));
        this.setTitle("Add/Edit Question");
    }
    private void apply() {
        final String text = textArea.getText();
        final String answer = answerArea.getText();
        final String script = scriptArea.getText();
        final String tags = tagField.getText();
        
        this.question = (this.question == null)
            ? Axiom.getInstance().createQuestion(text, answer, script)
            : Axiom.getInstance().editQuestion(this.question, text, answer, script);
        Axiom.getInstance().categorizeQuestion(this.question, tags);
        this.close();
    }
    public static void prompt(Question question) {
        EditStage stage = new EditStage(question);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }
}