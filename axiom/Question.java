package axiom;

import java.util.*;


class Question {
    private UUID id;
    private String text;
    private String answer;
    private String script;
    
    public Question(UUID id, String text, String answer, String script) {
        this.id = id;
        this.text = text;
        this.answer = answer;
        this.script = script;
    }
    public Question(String text, String answer, String script) {
        this(UUID.randomUUID(), text, answer, script);
    }
    public Question() {
        this("", "", "");
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
    public String getScript() {
        return this.script;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setScript(String script) {
        this.script = script;
    }
    public static Question deserialize(String text) {
        String parts[] = text.split(",");
        return new Question(UUID.fromString(parts[0]), parts[1], parts[2],
            (parts.length < 4)? "" : parts[2]);  // Quirk with split: empty final part is dropped
    }
    public String serialize() {
        return String.format("%s,%s,%s,%s", this.id, this.text, this.answer, this.script);
    }
    public String toString() {
        return this.text;
    }
}