package axiom;

import java.util.*;


public class Axiom {
   private FlatDB db;
   
   public Axiom() throws Exception {
      this.db = new FlatDB("axiom-db.xml");  
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
      
      String command = (args.length > 0)? args[0] : "help";
      switch (command) {
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