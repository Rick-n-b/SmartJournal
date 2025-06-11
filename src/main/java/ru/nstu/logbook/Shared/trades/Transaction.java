package ru.nstu.logbook.Shared.trades;

import java.io.Serializable;

public record Transaction(String type, Serializable data) implements Serializable {
    public static final Integer UNKNOWN_COUNT = -1;
}
