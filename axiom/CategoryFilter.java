package axiom;

import java.util.*;
import java.util.stream.*;

class CategoryFilter {
    public static boolean categorizes(Category category, Question question) {
        return Axiom.getInstance().getDB()
          .select(new Categorize[0])
          .anyMatch(categorizes ->
            (categorizes.getCategoryID().equals(category.getID())
             && categorizes.getElementID().equals(question.getID())));
    }
    public static boolean passes(String filter, Question question) {
        List<String> categoryNames = Arrays.asList(filter.split(","));
        return Axiom.getInstance().getDB()
          .select(new Category[0])
          .filter(category -> categoryNames.contains(category.getName()))
          .allMatch(category -> categorizes(category, question));
    }
}