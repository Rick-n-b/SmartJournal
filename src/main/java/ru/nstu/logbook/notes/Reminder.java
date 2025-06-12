package ru.nstu.logbook.notes;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder implements Serializable {
    static final long serialVersionUID = 8129437039424566964L;

    LocalDate expirationDate;
    LocalTime expirationTime;
    String topic;
    String content;

    public Reminder(LocalDate date, LocalTime time, String topic, String content){
        this.expirationDate = date;
        this.expirationTime = time;
        this.topic = topic;
        this.content = content;
    }

    public Reminder(){
        this(LocalDate.now(), LocalTime.now(), "", "");
    }

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

    @Override
    public String toString(){
        return topic + " " + expirationTime + " " + expirationDate + "\n" + content;
    }
}
