package com.adsbrain.hch.adsbrainapp;

import java.util.ArrayList;

/**
 * Created by qewqs on 2017-06-02.
 */

public class Game_Question_Info_Class {
    private String Movie_URL;
    private ArrayList<QUES> list_q;

    public Game_Question_Info_Class() {
        Movie_URL = "";
        this.list_q = new ArrayList<QUES>();
    }

    public Game_Question_Info_Class(String movie_URL, ArrayList<QUES> list_q) {
        Movie_URL = movie_URL;
        this.list_q = list_q;
    }

    public void setMovie_URL(String movie_URL) {
        Movie_URL = movie_URL;
    }

    public void setList_q(ArrayList<QUES> list_q) {
        this.list_q = list_q;
    }

    public String getMovie_URL() {
        return Movie_URL;
    }

    public ArrayList<QUES> getList_q() {
        return list_q;
    }
}