package ru.nstu.logbook.notes;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder implements Serializable {
    static final long serialVersionUID = 8129437039424566964L;

    int id;
    LocalDate expirationDate;
    LocalTime expirationTime;
    String topic;
    String content;

    public Reminder(int id, LocalDate date, LocalTime time, String topic, String content){
        this.id = id;
        this.expirationDate = date;
        this.expirationTime = time;
        this.topic = topic;
        this.content = content;
    }

    public Reminder(int id){
        this(id, LocalDate.now(), LocalTime.now(), "", "");
    }

    public Reminder(){
        this(-1, LocalDate.now(), LocalTime.now(), "", "");
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
    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return id + ") " + topic + " " + expirationTime + " " + expirationDate + "\n" + content;
    }
}
