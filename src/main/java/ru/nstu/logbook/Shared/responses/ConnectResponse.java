package ru.nstu.logbook.Shared.responses;

import ru.nstu.logbook.Shared.dto.ClientDescriptor;
import ru.nstu.logbook.Shared.network.NetClient;

import java.io.Serializable;
import java.util.List;

public record ConnectResponse(ClientDescriptor descriptor, List<NetClient> clients) implements Serializable {
}
