package com.example.demo;

public class ChatMessage {
    private String sender;
    private String content;

    // Constructors, getters, and setters

    public ChatMessage() {
    }

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    // Getter and setter methods

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
