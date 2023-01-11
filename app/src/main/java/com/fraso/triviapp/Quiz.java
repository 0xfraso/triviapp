package com.fraso.triviapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable {
    private int score = 0;
    private Difficulty difficulty = Difficulty.ANY;
    private int numberOfQuestions = 5;
    private int duration = 60;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<Question> questions;

    public Quiz(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public Quiz(int numberOfQuestions, int duration, Difficulty difficulty,ArrayList<Question> questions, ArrayList<String> categories) {
        this.numberOfQuestions = numberOfQuestions;
        this.duration = duration;
        this.difficulty = difficulty;
        this.questions = questions;
        this.categories = categories;
    }

    public Quiz() {}

    public ArrayList<Question> getQuestions() { return this.questions; }
    public int getScore() { return this.score; }
    public int getNumberOfQuestions() { return this.numberOfQuestions; }
    public int getDuration() { return this.duration; }
    public ArrayList<String> getCategories() { return this.categories; }
    public Difficulty getDifficulty() { return this.difficulty; }
    public void addScorePoint() { this.score++; }
}

