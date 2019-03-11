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
   
   public List<Object> select(Class type) {
      List<Object> result = new ArrayList<Object>();
      for (Object entity : entities) {
         if (entity.getClass() == type)
            result.add(entity);
      }
      return result;
   }
   public <T extends IFlatTable> void insert(T entity) {
      entities.add(entity);
   }
}


class Category implements IFlatTable {
   private long id;
   private String name;
   
   public Category(long id, String name) {
      this.name = name;
   }
   public String getName() {
      return name;
   }
   public static Category deserialize(String text) {
      String parts[] = text.split(",");
      // TODO validate input
      return new Category(Integer.parseInt(parts[0]), parts[1]);
   }
   public String serialize() {
      return String.format("%d,%s", this.id, this.name);
   }
}

class Question implements IFlatTable {
   private long id;
   private String text;
   private String answer;
   
   public Question(long id, String text, String answer) {
      this.id = id;
      this.text = text;
      this.answer = answer;
   }
   
   public String getText() {
      return text;
   }
   public String getAnswer() {
      return answer;
   }
   public static Question deserialize(String text) {
      String parts[] = text.split(",");
      // TODO validate input
      return new Question(Integer.parseInt(parts[0]), parts[1], parts[2]);
   }
   public String serialize() {
      return String.format("%d,%s,%s", this.id, this.text, this.answer);
   }
}

public class Axiom {
	public static void main(String []args) throws Exception {
      FlatDB db = new FlatDB("axiom-db.xml");
      
      if (args.length == 0) {
         for (Object q : db.select(Question.class))
            System.out.println(String.format("%s", ((Question)q).getText()));
         return;
      }
      switch (args[0]) {
         case "create": {
            if (args.length != 3)
               return;
            db.insert(new Question(0, args[1], args[2]));
            break;
         }
         default: {
            break;
         }
      }
      db.save();
	}
}