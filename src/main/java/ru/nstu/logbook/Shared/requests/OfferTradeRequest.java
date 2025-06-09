package ru.nstu.logbook.Shared.requests;

import ru.nstu.logbook.Shared.trades.TradeInners;

import java.io.Serializable;

public record OfferTradeRequest(int targetId, TradeInners inners) implements Serializable {
}
