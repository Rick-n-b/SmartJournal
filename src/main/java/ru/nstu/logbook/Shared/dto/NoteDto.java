package ru.nstu.logbook.Shared.dto;

import java.io.Serializable;
import java.util.Date;

public record NoteDto(String title, Date creationDate, String content) implements Serializable {
}
