package axiom;

import java.util.*;
import java.util.stream.*;

// A helper class for parsing category filters provided by the user and
// determining if a given question meets the requirements.
// Filters are provided by the user in the form of a simple algebra, for example
//
//  -tag1 tag2+tag3
//
// Would filter out all questions which do include tag1 and do not include both
// tag2 and tag3. Note that the tags themselves can be heirarchical, so that
// tag1 may categorize tag2, etc. Parenthesis can be used.
//
class CategorizeFilter {
    /*private static Token tokenize(String filter) {
        
    }
    
    public CategorizeFilter(String filter) {
        int depth = 0;
        for (;;) {
            Token tok = tokenize(filter);
            if (tok == null)
                break;
            switch (tok.type) {
                case TOK_LPAREN:
                    depth++;
                case TOK_RPAREN:
                    depth--;
                case TOK_AND:
                
                case TOK_NOT:
                case TOK_SPACE:
                case TOK_CATEGORY:
                    
            }
        }
    }*/

    private String filter;
    
    public CategorizeFilter(String filter) {
        this.filter = filter;
    }
    private static boolean categorizes(Category category, Question question) {
        return Axiom.getInstance().getDB()
          .select(new Categorize[0])
          .anyMatch(categorizes ->
            (categorizes.getCategoryID().equals(category.getID())
             && categorizes.getElementID().equals(question.getID())));
    }
    public boolean passes(Question question) {
        List<String> categoryNames = Arrays.asList(filter.split(","));
        return Axiom.getInstance().getDB()
          .select(new Category[0])
          .filter(category -> categoryNames.contains(category.getName()))
          .allMatch(category -> categorizes(category, question));
    }
}