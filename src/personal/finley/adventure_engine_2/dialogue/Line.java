package personal.finley.adventure_engine_2.dialogue;

import java.util.List;

import personal.finley.adventure_engine_2.Condition;

public class Line {
    
    private boolean hasSpoken;

    private List<String> text;
    private Condition condition;

    private boolean once;
    // If non-null, redirect to this topic after line is spoken
    private String redirectTopicId;

    public Line(List<String> text, Condition condition, boolean once, String redirectTopicId) {
        this.text = text;
        this.condition = condition;
        this.once = once;
        this.redirectTopicId = redirectTopicId;
    }

}
