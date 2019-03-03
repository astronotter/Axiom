package axiom;

import java.util.*;
import javax.persistence.*;

@Entity
class Category {
   // The id is unique to both Category and Question tables, so that a category
   // can unambiguously categorize both questions and other categories.
   @Id
   @TableGenerator(name="gid", table="gid", initialValue=1)
   @GeneratedValue(strategy=GenerationType.TABLE, generator="gid")
   private long id;
   @Basic
   private String name;
   
   public Category(String name) {
      this.name = name;
   }
   public String getName() {
      return name;
   }
}

@Entity
class Question {
   // The id is unique to both Category and Question tables, so that a category
   // can unambiguously categorize both questions and other categories.
   @Id
   @TableGenerator(name="gid", table="gid", initialValue=1)
   @GeneratedValue(strategy=GenerationType.TABLE, generator="gid")
   private long id;
   @Basic
   private String text;
   @Basic
   private String answer;
   
   public Question(String text, String answer) {
      this.text = text;
      this.answer = answer;
   }
   
   public String getText() {
      return text;
   }
   public String getAnswer() {
      return answer;
   }
}

public class Axiom {
   @SuppressWarnings("unchecked")
	public static void main(String []args) {
      EntityManagerFactory factory =
         Persistence.createEntityManagerFactory(
            "axiom", System.getProperties());
      EntityManager em = factory.createEntityManager();
      
      if (args.length == 0)
         return;
      switch (args[0]) {
         case "create": {
            if (args.length != 3)
               return;
            EntityTransaction tr = em.getTransaction();
            tr.begin();
            em.persist(new Question(args[1], args[2]));
            tr.commit();
            break;
         }
         default: {
            EntityTransaction tr = em.getTransaction();
            tr.begin();
            List<Question> results = em
               .createQuery("SELECT q FROM Question q")
               .getResultList();
            System.out.println(String.format("questions (%d):", results.size()));
            for (Question q : results) {
               System.out.println(String.format("%s:%s", q.getText(), q.getAnswer()));
            }
            tr.commit();
            break;
         }
      }
      em.close();
      factory.close();
	}
}