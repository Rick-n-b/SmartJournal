package ru.nstu.logbook.Shared.messages;

import ru.nstu.logbook.Shared.dto.ClientDescriptor;

import java.io.Serializable;

public record ClientConnectMessage(ClientDescriptor descriptor) implements Serializable {
}
