package axiom

import org.junit.Test;
import static org.junit.Assert.*;

public class QuestionTest
{
    Question question = new Question("Question", "Answer")
    String serializedQuestion = question.serialize();
    Question deserializedQuestion = question.deserialize(serializedQuestion);
    UUID deserializedID = deserializedQuestion.getID();

    @Test
    public void questionCreation()
    {
        assertNotNull(question); //Should return TRUE

    }
    @Test
    public void questionQuestion()
    {
        assertEquals("Question", question.getText()); //Should return TRUE
    }

    @Test
    public void questionAnswer()
    {
        assertEquals("Answer", question.getAnswer()); //Should return TRUE
    }

    @Test
    public void deserializedQuestion()
    {
        assertEquals("Question", deserializedQuestion.getText()); //Should return TRUE
    }

    @Test
    public void deserializedAnswer()
    {
        assertEquals("Answer", deserializedQuestion.getAnswer()); //Should return TRUE
    }

    @Test
    public void deserializedID()
    {
        assertTrue( deserializedID.equals(question.getID()) ); //Should return TRUE
    }
}