package com.adsbrain.hch.adsbrainapp;

/**
 * Created by qewqs on 2017-06-02.
 */

public class QUES{
    private String context;
    private int answer;
    private String Q_one;
    private String Q_two;
    private String Q_three;
    private String Q_four;
    private int score;

    public QUES( ) {
        this.context = "";
        this.answer = 0;
        Q_one = "";
        Q_two = "";
        Q_three = "";
        Q_four = "";
        this.score = 0;
    }

    public QUES(String context, int answer, String q_one, String q_two, String q_three, String q_four, int score) {
        this.context = context;
        this.answer = answer;
        Q_one = q_one;
        Q_two = q_two;
        Q_three = q_three;
        Q_four = q_four;
        this.score = score;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setQ_one(String q_one) {
        Q_one = q_one;
    }

    public void setQ_two(String q_two) {
        Q_two = q_two;
    }

    public void setQ_three(String q_three) {
        Q_three = q_three;
    }

    public void setQ_four(String q_four) {
        Q_four = q_four;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getContext() {
        return context;
    }

    public int getAnswer() {
        return answer;
    }

    public String getQ_one() {
        return Q_one;
    }

    public String getQ_two() {
        return Q_two;
    }

    public String getQ_three() {
        return Q_three;
    }

    public String getQ_four() {
        return Q_four;
    }

    public int getScore() {
        return score;
    }
}
