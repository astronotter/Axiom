package axiom;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.nio.charset.*;


class FlatDB {
   String filename;
   private List<Object> entities;
   
   public FlatDB(String filename) throws Exception {
      this.filename = filename;
      this.entities = new ArrayList<Object>();
   
      try {
         String lines[] = Files.lines(Paths.get(filename)).toArray(String[]::new);
         for (String line : lines) {
            String parts[] = line.split(":");
            Class tableClass = Class.forName(parts[0]);
            if (tableClass == null) {
               System.err.println(String.format(
                  "Warning: Cannot find class '%s'. Ignoring entry.",
                  parts[0]));
               continue;
            }
            Method deserializeMethod = tableClass.getDeclaredMethod("deserialize", String.class);
            if (deserializeMethod == null) {
               System.err.println(String.format(
                  "Warning: Cannot find deserialize method for class '%s'. Ignoring entry.",
                  parts[0]));
               continue;
            }
            this.entities.add(deserializeMethod.invoke(tableClass, parts[1]));
         }
      }
      catch (NoSuchFileException notfound) {
         // Silently ignore, the file not existing is treated the same as an
         // empty db
      }
   }
   
   public void save() throws Exception {
      save(this.filename);
   }
   public void save(String filename) throws Exception {
      List<String> lines = new ArrayList<String>();
      for (Object entity : entities) {
         Class tableClass = entity.getClass();
         Method serializeMethod = tableClass.getDeclaredMethod("serialize");
         if (serializeMethod == null) {
            System.err.println(String.format(
               "Warning: No serialize method for class '%s'. Ignoring instance.",
               tableClass.getName()));
            continue;
         }
         String text = (String)serializeMethod.invoke(entity);
         lines.add(String.format("%s:%s", tableClass.getName(), text));
      }
      Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8);
   }
   // Due to type erasure we require a dummy variable (a la toArray). Usage is
   // then select(new Type[0]). This is a tradeoff between DRY and the inherent
   // problems with generics in Java.
   public <T> Stream<T> select(T type[]) {
      Stream.Builder<T> result = Stream.builder();
      for (Object entity : entities) {
         if (entity.getClass() == type.getClass().getComponentType())
            result.add((T)entity);
      }
      return result.build();
   }
   public <T> T insert(T entity) {
      entities.add(entity);
      return entity;
   }
}


class Category {
   private UUID id;
   private String name;
   
   public Category(UUID id, String name) {
      this.id = id;
      this.name = name;
   }
   public Category(String name) {
      this(UUID.randomUUID(), name);
   }
   
   public UUID getID() {
      return this.id;
   }
   public String getName() {
      return this.name;
   }
   public static Category deserialize(String text) {
      String parts[] = text.split(",");
      // TODO validate input
      return new Category(UUID.fromString(parts[0]), parts[1]);
   }
   public String serialize() {
      return String.format("%s,%s", this.id, this.name);
   }
}

class Categorize {
   private UUID categoryID;
   private UUID elementID;
   
   public Categorize(UUID categoryID, UUID elementID) {
      this.categoryID = categoryID;
      this.elementID = elementID;
   }
   
   public UUID getCategoryID() {
      return this.categoryID;
   }
   public UUID getElementID() {
      return this.elementID;
   }
   
   public static Categorize deserialize(String text) {
      String parts[] = text.split(",");
      // TODO validate input
      return new Categorize(UUID.fromString(parts[0]),
                            UUID.fromString(parts[1]));
   }
   public String serialize() {
      return String.format("%s,%s", this.categoryID, this.elementID);
   }
}

class Question {
   private UUID id;
   private String text;
   private String answer;
   
   public Question(UUID id, String text, String answer) {
      this.id = id;
      this.text = text;
      this.answer = answer;
   }
   public Question(String text, String answer) {
      this(UUID.randomUUID(), text, answer);
   }
   
   public UUID getID() {
      return this.id;
   }
   public String getText() {
      return this.text;
   }
   public String getAnswer() {
      return this.answer;
   }
   public static Question deserialize(String text) {
      String parts[] = text.split(",");
      // TODO validate input
      return new Question(UUID.fromString(parts[0]), parts[1], parts[2]);
   }
   public String serialize() {
      return String.format("%s,%s,%s", this.id, this.text, this.answer);
   }
}

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