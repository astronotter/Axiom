package axiom;

import java.util.*;


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
    public Question() {
        this("", "");
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
    public void setText(String text) {
        this.text = text;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public static Question deserialize(String text) {
        String parts[] = text.split(",");
        // TODO validate input
        return new Question(UUID.fromString(parts[0]), parts[1], parts[2]);
    }
    public String serialize() {
        return String.format("%s,%s,%s", this.id, this.text, this.answer);
    }
    public String toString() {
        return this.text;
    }
}