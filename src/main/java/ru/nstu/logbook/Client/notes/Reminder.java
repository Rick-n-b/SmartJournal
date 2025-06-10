package ru.nstu.logbook.Client.notes;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder implements Serializable {
    LocalDate expirationDate;
    LocalTime expirationTime;
    String topic = "";
    String content = "";



    public void setTopic(String topic) {
        this.topic = topic;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    public void setExpirationTime(LocalTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getContent() {
        return content;
    }
    public String getTopic() {
        return topic;
    }
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    public LocalTime getExpirationTime() {
        return expirationTime;
    }
}
