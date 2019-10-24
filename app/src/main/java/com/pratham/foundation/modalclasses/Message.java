package com.pratham.foundation.modalclasses;

public class Message {


    protected String message;
    protected String senderName;
    protected String senderAudio;

    public Message(String message, String senderName, String senderAudio) {
        this.message = message;
        this.senderName = senderName;
        this.senderAudio = senderAudio;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderAudio() {
        return senderAudio;
    }

    public void setSenderAudio(String senderAudio) {
        this.senderAudio = senderAudio;
    }
}
