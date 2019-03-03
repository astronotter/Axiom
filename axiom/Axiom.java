package axiom;

import java.util.*;
import javax.persistence.*;

@Entity
class Category {
   @Id
   @GeneratedValue
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
   @Id
   @GeneratedValue
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