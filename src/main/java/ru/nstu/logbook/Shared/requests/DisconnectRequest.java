package ru.nstu.logbook.Shared.requests;

import java.io.Serializable;

public record DisconnectRequest(int clientId) implements Serializable {
}
