package com.cabanasrd.data.entities;

/**
 * Created by Arturo on 7/24/2015.
 */
public class Answer {

    private int Id = 0;
    private String Text = null;


    public Answer() {
    }

    public Answer(int id, String text) {
        Id = id;
        Text = text;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
