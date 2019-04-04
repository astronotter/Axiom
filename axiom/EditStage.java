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

class EditStage extends Stage {
    Question question;
    
    TextArea textArea = new TextArea();
    TextArea answerArea = new TextArea();
    TextField tagField = new TextField();
    Button okButton = new Button("OK");
    Button cancelButton = new Button("Cancel");
    VBox vbox = new VBox(textArea, answerArea, tagField,
        new HBox(okButton, cancelButton));
    
    public EditStage(Question question) {
        super();
        
        this.question = question;
        if (this.question != null)
            populate();
        this.okButton.setOnAction(ev -> apply());
        this.cancelButton.setOnAction(ev -> this.close());
        this.vbox.setAlignment(Pos.CENTER);
        this.vbox.setPadding(new Insets(5, 5, 5, 5));
        this.setScene(new Scene(vbox));
        this.setTitle("Add/Edit Question");
    }
    private void populate() {
        final FlatDB db = Axiom.getInstance().getDB();
        
        this.textArea.setText(question.getText());
        this.answerArea.setText(question.getAnswer());
        
        // Generate tag list from database.
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
    private void apply() {
        final FlatDB db = Axiom.getInstance().getDB();
        
        if (this.question == null)
            this.question = db.insert(new Question());
        this.question.setText(textArea.getText());
        this.question.setAnswer(answerArea.getText());
        
        // Strip all categories from question.
        db.select(new Categorize[0])
          .filter(cat -> cat.getElementID().equals(this.question.getID()))
          .forEach(cat -> db.remove(cat));
        
        // Parse and recategorize question.
        String tokens[] = tagField.getText().split(",");
        for (String token : tokens) {
            final String categoryName = token.trim();
            if (categoryName.equals(""))
                continue;
            
            // Find the category by name, or create it if it does not exist
            Category category =
                db.select(new Category[0])
                  .filter(cat -> cat.getName().equals(categoryName))
                  .findAny()
                  .orElseGet(() -> db.insert(new Category(categoryName)));
            
            db.insert(new Categorize(category.getID(), this.question.getID()));
        }
        this.close();
    }
    public static void prompt(Question question) {
        EditStage stage = new EditStage(question);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }
}