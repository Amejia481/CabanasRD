package com.cabanasrd.data.entities;

import java.util.ArrayList;

/**
 * Created by Arturo on 7/24/2015.
 */
public class Question {
    private int Id =0;
    private String Text = null;
    private ArrayList<Answer> Answers = null;
    private int userAnswerID = 0;


    public int getUserAnswerID() {
        return userAnswerID;
    }

    public void setUserAnswerID(int userAnswerID) {
        this.userAnswerID = userAnswerID;
    }

    public Question(int id, String text, ArrayList<Answer> answers) {
        Id = id;
        Text = text;
        Answers = answers;
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

    public ArrayList<Answer> getAnswers() {
        return Answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        Answers = answers;
    }
}
