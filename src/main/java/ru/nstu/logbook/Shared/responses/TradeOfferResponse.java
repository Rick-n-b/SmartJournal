package ru.nstu.logbook.Shared.responses;

import ru.nstu.logbook.Shared.trades.TradeInners;

import java.io.Serializable;

public record TradeOfferResponse(int id, int targetId, TradeInners inners) implements Serializable {
}
