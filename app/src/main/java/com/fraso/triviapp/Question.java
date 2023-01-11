package com.fraso.triviapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question implements Serializable {

    private String id;
    private String question;

    private String correctAnswer;
    private List<String> incorrectAnswers = new ArrayList<>();
    private List<String> answers = new ArrayList<String>();

    public Question() {}
    public Question(String id, String question,String correctAnswer, List<String> incorrectAnswers ) {
        this.id = id;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getId() { return this.id; }

    public String getQuestion() {
        return this.question;
    }

    public List<String> getAnswers() {
        this.answers.addAll(this.incorrectAnswers);
        if (!this.answers.contains(this.correctAnswer))
            this.answers.add(this.correctAnswer);

        Collections.shuffle(this.answers);
        return this.answers;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }
}
