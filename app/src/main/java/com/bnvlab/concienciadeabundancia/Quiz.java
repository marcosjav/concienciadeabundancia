package com.bnvlab.concienciadeabundancia;

import android.widget.Switch;

/**
 * Created by bort0 on 21/03/2017.
 */

public class Quiz {

    private String quiz;
    private boolean answer;

    public Quiz(String quiz, boolean answer) {

        this.answer = answer;
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
