package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by bort0 on 21/03/2017.
 */

public class QuizItem {

    public static final String CHILD = "quiz";
    private String quiz;
    private boolean answer;

    public QuizItem() {
    }

    public QuizItem(String quiz, boolean answer) {

        this.answer = answer;
        this.quiz = quiz;
    }

    public QuizItem(String quiz) {

        this.answer = false;
        this.quiz = quiz;
    }

    public boolean getAnswer() {
        return this.answer;
    }
    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
    public String getQuiz() {
        return this.quiz;
    }
    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }
}
