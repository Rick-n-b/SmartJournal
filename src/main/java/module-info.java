module ru.nstu.logbook.Client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    exports ru.nstu.logbook.Client;
    opens ru.nstu.logbook.Client to javafx.fxml;
    exports ru.nstu.logbook.Client.controllers;
    opens ru.nstu.logbook.Client.controllers to javafx.fxml;

    exports ru.nstu.logbook.Client.utils;
    exports ru.nstu.logbook.Client.net.Trades;
    exports ru.nstu.logbook.Client.net;
    exports ru.nstu.logbook.Client.notes;
    exports ru.nstu.logbook.Client.reminds;
    exports ru.nstu.logbook.Shared.dto;
    exports ru.nstu.logbook.Shared.messages;
    exports ru.nstu.logbook.Shared.requests;
    exports ru.nstu.logbook.Shared.trades;
    exports ru.nstu.logbook.Shared.network;
    exports ru.nstu.logbook.Shared.events;
    exports ru.nstu.logbook.Shared.responses;
    exports ru.nstu.logbook.Server;
    exports ru.nstu.logbook.Server.utils;

}