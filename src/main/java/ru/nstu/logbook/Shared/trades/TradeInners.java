package ru.nstu.logbook.Shared.trades;

import java.io.Serializable;

public record TradeInners(Transaction offer) implements Serializable {
}
