package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 18/04/2017.
 */

public class FAQItem {

    String question, answer;

    public FAQItem() {
    }

    public FAQItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
