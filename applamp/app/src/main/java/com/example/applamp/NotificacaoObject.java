package com.example.applamp;

public class NotificacaoObject {
    String text;
    int id;
    boolean to_aceppt;
    boolean accepted;

    public NotificacaoObject(String text, boolean to_aceppt, boolean accepted, int id) {
        this.text = text;
        this.to_aceppt = to_aceppt;
        this.accepted = accepted;
        this.id = id;
    }
}
