package axiom;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class QuestionTest {
    Question question = new Question("Question", "Answer");
    String serializedQuestion = question.serialize();
    Question deserializedQuestion = question.deserialize(serializedQuestion);
    UUID deserializedID = deserializedQuestion.getID();

    @Test
    public void questionCreation() {
        assertNotNull(question);
    }
    @Test
    public void questionQuestion() {
        assertEquals("Question", question.getText());
    }
    @Test
    public void questionAnswer() {
        assertEquals("Answer", question.getAnswer());
    }
    @Test
    public void deserializedQuestion() {
        assertEquals("Question", deserializedQuestion.getText());
    }
    @Test
    public void deserializedAnswer() {
        assertEquals("Answer", deserializedQuestion.getAnswer());
    }
    @Test
    public void deserializedID() {
        assertTrue( deserializedID.equals(question.getID()));
    }
}