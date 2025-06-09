package ru.nstu.logbook.Shared.dto;

import java.util.Date;

public record ReminderDto(String title, Date expansionDate, String content) {
}
