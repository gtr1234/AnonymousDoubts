package com.example.karthik.anonymousdoubts.Chat;



import org.joda.time.LocalTime;


public class ChatMessage {
    private String messageText;
    private String messageUser;
    private boolean anon;
    private String messageTime;


    public ChatMessage(String messageText, String messageUser,boolean anon) {
        LocalTime mTime = LocalTime.now();
        String time = new StringBuilder("").append(mTime.getHourOfDay()).append(":").append(mTime.getMinuteOfHour()).toString();

        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = time;
        this.anon = anon;
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }


    public boolean isAnon() {
        return anon;
    }

    public void setAnon(boolean anon) {
        this.anon = anon;
    }
}
