package com.cabanasrd.data.entities;

/**
 * Created by Arturo on 7/29/2015.
 */
public class UserAnswer {

    private int idQuestion = 0;
    private int idAnswer = 0;
    private String idDevice = null;

    public UserAnswer(int idQuestion, int idAnswer, String idDevice) {
        this.idQuestion = idQuestion;
        this.idAnswer = idAnswer;
        this.idDevice = idDevice;
    }

    public UserAnswer() {
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getIdAnswer() {
        return idAnswer;
    }

    public void setIdAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }
}
