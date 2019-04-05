package axiom;

import java.util.*;
import java.util.stream.*;
import java.util.regex.*;
import javax.script.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.application.Application;

public class Axiom {
    static Axiom instance;
    
    ScriptEngineManager scriptFactory = new ScriptEngineManager();
    ScriptEngine js = scriptFactory.getEngineByName("JavaScript");
    FlatDB db;

    private Axiom(String dbFileName) throws Exception {
        this.db = new FlatDB(dbFileName);
    }
    public static Axiom getInstance() {
        return instance;
    }
    public FlatDB getDB() {
        return this.db;
    }
    
    public void save() throws Exception {
        this.db.save();
    }
    
    public List<Question> listQuestions(String filter) {
        final List<String> categoryNames = Arrays.asList(filter.split(","));
        return this.db
            .select(new Question[0])
            .filter(question -> this.db
                .select(new Category[0])
                .filter(category -> categoryNames.contains(category.getName()))
                .allMatch(category -> this.db
                    .select(new Categorize[0])
                    .anyMatch(categorizes ->
                     (categorizes.getCategoryID().equals(category.getID())
                     && categorizes.getElementID().equals(question.getID())))))
            .collect(Collectors.toList());
    }
    public Question createQuestion(String text, String answer) {
        return this.db.insert(new Question(text, answer));
    }
    public Question editQuestion(Question question, String text, String answer) {
        question.setText(text);
        question.setAnswer(answer);
        return question;
    }
    public void categorizeQuestion(Question question, String categories) {
        // Strip all categories from question.
        this.db
            .select(new Categorize[0])
            .filter(cat -> cat.getElementID().equals(question.getID()))
            .forEach(cat -> this.db.remove(cat));
        
        // Parse and recategorize question.
        for (String token : categories.split(",")) {
            final String categoryName = token.trim();
            if (categoryName.equals(""))
                continue;
            
            // Find the category by name, or create it if it does not exist
            Category category = this.db
                .select(new Category[0])
                .filter(cat -> cat.getName().equals(categoryName))
                .findAny()
                .orElseGet(() -> this.db.insert(new Category(categoryName)));
            
            this.db.insert(new Categorize(category.getID(), question.getID()));
        }
    }
    public List<Category> getCategories(Question question) {
        List<Category> categories = new ArrayList<Category>();
        this.db
            .select(new Categorize[0])
            .filter(cat -> cat.getElementID().equals(question.getID()))
            .forEach(cat -> this.db
                .select(new Category[0])
                .filter(cat2 -> cat2.getID().equals(cat.getCategoryID()))
                .forEach(cat2 -> categories.add(cat2)));
        return categories;
    }
    public Question getQuestion(UUID id) {
        return this.db
            .select(new Question[0])
            .filter(q -> q.getID().equals(id))
            .findAny()
            .orElse(null);
    }
    // Given text with embedded scripts, executes and inserts the results of the
    // scripts into the text, to produce a final output string.
    public String produceText(String text) {
        final Pattern ptn = Pattern.compile("\\[([^\\]]+)\\]");   // Adapted from SO #14584018
        final Matcher m = ptn.matcher(text);
        
        String output = "";
        int i = 0;
        while (m.find()) {
            output += text.substring(i, m.start());
            
            try {
                Object result = js.eval(m.group(1));
                if (result != null)
                    output += result;
            }
            catch (ScriptException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Failed to run script: %s", ex));
                alert.showAndWait();
            }
            i = m.end();
        }
        return output + text.substring(i);
    }
    public void processCLI(String args[]) {
        switch (args[0]) {
            case "list": {
                String filter = (args.length > 2)? "" : args[1];
                for (Question question : listQuestions(filter)) {
                    System.out.println(String.format("%s:%s",
                        question.getID(), question.getText()));
                }
                break;
            }
            case "create": {
                if (args.length < 4) {
                   System.err.println("Invalid number of arguments.");
                   return;
                }
                Question question = createQuestion(args[1], args[2]);
                categorizeQuestion(question, args[3]);
                break;
            }
            case "edit": {
                if (args.length < 4) {
                   System.err.println("Invalid number of arguments.");
                   return;
                }
                Question question = getQuestion(UUID.fromString(args[1]));
                editQuestion(question, args[2], args[3]);
                break;
            }
            case "assign":
                if (args.length != 2) {
                   System.err.println("Invalid number of arguments.");
                   return;
                }
                Question question = getQuestion(UUID.fromString(args[1]));
                categorizeQuestion(question, args[2]);
                break;
            case "help":
                System.err.println(String.format(
                   "Usage: java -jar Axiom [COMMAND ...]"
                 + "%nCommands:"
                 + "%n  create QUESTION ANSWER CATEGORIES"
                 + "%n  edit ID QUESTION ANSWER CATEGORIES"
                 + "%n  assign ITEM CATEGORIES"
                 + "%n  list"
                 + "%n  help"));
                break;
            default:
                System.err.println("Invalid command.");
                return;
        }
    }
    public static void main(String []args) throws Exception {        
        instance = new Axiom("axiom.db");
        
        // If the program is called without arguments then the GUI is launched.
        if (args.length == 0)
            Application.launch(AxiomGUI.class);
        else
            instance.processCLI(args);
        instance.save();
    }
}