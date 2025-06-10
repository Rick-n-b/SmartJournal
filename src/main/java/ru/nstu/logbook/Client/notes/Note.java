package ru.nstu.logbook.Client.notes;

import java.io.Serializable;
import java.time.LocalDate;

public class Note implements Serializable {
     static final long serialVersionUID = 8129437039424566964L;

     LocalDate date = LocalDate.now();
     String topic = "";
     String content = "";

     public void setContent(String content) {
          this.content = content;
     }
     public void setDate(LocalDate date) {
          this.date = date;
     }
     public void setTopic(String topic) {
          this.topic = topic;
     }
     public LocalDate getDate() {
          return date;
     }
     public String getTopic() {
          return topic;
     }
     public String getContent() {
          return content;
     }
}
