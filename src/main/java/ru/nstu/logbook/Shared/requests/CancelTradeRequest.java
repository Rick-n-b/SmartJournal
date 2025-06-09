package ru.nstu.logbook.Shared.requests;

import java.io.Serializable;

public record CancelTradeRequest(int id) implements Serializable {
}
