package axiom;

import java.util.*;
import java.util.stream.*;
import javax.script.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;

public class QuizStage extends Stage {
    Iterator<Question> iterator;
    
    Label textLabel = new Label();
    Label answerLabel = new Label();
    Button answerButton = new Button("Answer");
    Button nextButton = new Button("Next");
    Button finishButton = new Button("Finish");
    VBox vbox = new VBox(textLabel, answerLabel,
        new HBox(answerButton, nextButton, finishButton));

	public QuizStage(Iterator<Question> iterator) {
	   super();
   
	   this.iterator = iterator;
	   this.nextButton.setOnAction(ev -> this.next());
	   this.finishButton.setOnAction(ev -> this.close());
	   this.vbox.setAlignment(Pos.CENTER);
	   this.vbox.setPadding(new Insets(5, 5, 5, 5));
	   this.setScene(new Scene(this.vbox));
	   this.setTitle("Quiz");
       
       next();
	}
    private void next() {
       // Setup the next question, and hide the answer.
       Question question = this.iterator.next();
       Axiom.getInstance().runQuestion(question);
       
       this.textLabel.setText(Axiom.getInstance().produceText(question.getText()));
       this.answerLabel.setText("");
	   this.answerButton.setOnAction(ev -> {
           String text = Axiom.getInstance().produceText(question.getAnswer());
           this.answerLabel.setText(text);
       });
       
       // If we run out of questions then grey out the next button.
       if (!this.iterator.hasNext())
           this.nextButton.disableProperty().set(true);
    }
    public static void prompt(Iterator<Question> iterator) {
        QuizStage stage = new QuizStage(iterator);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }
}