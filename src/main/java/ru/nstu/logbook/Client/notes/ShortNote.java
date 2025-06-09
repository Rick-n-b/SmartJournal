package ru.nstu.logbook.Client.notes;

import java.time.LocalDate;

public class ShortNote {

    public ShortNote(LocalDate date, String topic, int id){
        this.date = date;
        this.topic = topic;
        this.id = id;
    }
    public ShortNote(LocalDate date, String topic){
        this(date, topic, -1);
    }
    public ShortNote(LocalDate date){
        this(date, "Unnamed");
    }
    public ShortNote(){
        this(LocalDate.now());
    }

    public LocalDate date;
    protected String topic;
    protected int id;

    public int getId() {
        return id;
    }
    public LocalDate getDate() {
        return date;
    }
    public String getTopic() {
        return topic;
    }

    @Override
    public String toString(){
        return date.toString() + "\n" + topic;
    }

}
