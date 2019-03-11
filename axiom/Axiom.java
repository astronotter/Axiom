package axiom;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;


interface IFlatTable {
   // Get the name of the corresponding table in the database. Defaults to the
   // name of the subclass itself (this is a new feature in java 8).
   default String getTableName() {
      return this.getClass().getName();
   }
}

class FlatDB {
   private List<Object> entities;
   
   public FlatDB(String filename) throws Exception {
      this.entities = new ArrayList<Object>();
   
      String lines[] = Files.lines(Paths.get(filename)).toArray(String[]::new);
      for (String line : lines) {
         String parts[] = line.split(":");
         Class tableClass = Class.forName(parts[0]);
         if (tableClass == null) {
            System.err.println(String.format("Warning: Cannot find class '%s'. Ignoring entry.", parts[0]));
            continue;
         }
         Method deserializeMethod = tableClass.getDeclaredMethod("deserialize", String.class);
         if (deserializeMethod == null) {
            System.err.println(String.format("Warning: Cannot find class '%s'. Ignoring entry.", parts[0]));
            continue;
         }
         this.entities.add(deserializeMethod.invoke(tableClass, parts[1]));
      }
   }
   
   public void save() {
   }
   
   // Wierd quirk in the syntax: Even though this generic says extends, it can
   // be an interface as well.
   public <T extends IFlatTable> List<T> select() {
      return null;
   }
   public <T extends IFlatTable> void insert(T object) {
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
}

public class Axiom {
	public static void main(String []args) throws Exception {
      FlatDB db = new FlatDB("axiom-db.xml");
      
      if (args.length == 0) {
         //for (Question q : db.<Question>select())
         //   System.out.println(String.format("%s", q.getText()));
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