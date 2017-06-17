package com.adsbrain.hch.adsbrainapp;

import java.util.Date;

/**
 * Created by qewqs on 2017-06-06.
 */

public class store_data {
    String who;
    String nickname;
    String sexual;
    int age;
    String date;
    String video_url;
    int score;
    boolean q1;
    boolean q2;
    boolean q3;
    boolean q4;
    boolean q5;

    public store_data() {
        this.who = "";
        this.nickname = "";
        this.sexual = "";
        this.age = 0;
        this.date = "";
        this.video_url = "";
        this.score = 0;
        this.q1 = false;
        this.q2 = false;
        this.q3 = false;
        this.q4 = false;
        this.q5 = false;
    }

    public store_data(String who, String nickname, String sexaul, int age, String date, String video_url, int score, boolean q1, boolean q2, boolean q3, boolean q4, boolean q5) {
        this.who = who;
        this.nickname = nickname;
        this.sexual = sexaul;
        this.age = age;
        this.date = date;
        this.video_url = video_url;
        this.score = score;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q5 = q5;
    }

    public String getSexual() {
        return sexual;
    }

    public void setSexual(String sexual) {
        this.sexual = sexual;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setQ1(boolean q1) {
        this.q1 = q1;
    }

    public void setQ2(boolean q2) {
        this.q2 = q2;
    }

    public void setQ3(boolean q3) {
        this.q3 = q3;
    }

    public void setQ4(boolean q4) {
        this.q4 = q4;
    }

    public void setQ5(boolean q5) {
        this.q5 = q5;
    }

    public String getWho() {
        return who;
    }

    public String getNickname() {
        return nickname;
    }

    public int getAge() {
        return age;
    }

    public String getDate() {
        return date;
    }

    public String getVideo_url() {
        return video_url;
    }

    public int getScore() {
        return score;
    }

    public boolean isQ1() {
        return q1;
    }

    public boolean isQ2() {
        return q2;
    }

    public boolean isQ3() {
        return q3;
    }

    public boolean isQ4() {
        return q4;
    }

    public boolean isQ5() {
        return q5;
    }
}
