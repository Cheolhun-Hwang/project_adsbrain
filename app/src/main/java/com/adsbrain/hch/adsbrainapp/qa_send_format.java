package com.adsbrain.hch.adsbrainapp;

/**
 * Created by qewqs on 2017-04-08.
 */

public class qa_send_format {
    private String who;
    private String when;
    private String title;
    private String contents;

    qa_send_format(){
        this.who = "";
        this.when = "";
        this.title = "";
        this.contents = "";
    }

    qa_send_format(String who, String when, String title, String contents){
        this.who = who;
        this.when = when;
        this.title = title;
        this.contents = contents;
    }

    public void setWho(String who){
        this.who = who;
    }
    public void setWhen(String when){
        this.when = when;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setContents(String contents){
        this.contents = contents;
    }

    public String getWho(){
        return this.who;
    }
    public String getWhen(){
        return  this.when;
    }
    public String getTitle(){
        return  this.title;
    }
    public String getContents(){
        return  this.contents;
    }
}
