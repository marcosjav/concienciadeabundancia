package com.bnvlab.concienciadeabundancia;

/**
 * Created by bort0 on 21/03/2017.
 */

public class Quiz {
    private int id;
    private String quiz;
    private int answer;

    public Quiz(int id, String quiz, int answer) {
        this.id = id;
        this.answer = answer;
        this.quiz = quiz;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getAnswer() {
        return this.answer;
    }
    public void setAnswer(int answer) {
        this.answer = answer;
    }
    public String getQuiz() {
        return this.quiz;
    }
    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }
}
