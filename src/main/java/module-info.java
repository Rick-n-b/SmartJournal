module ru.nstu.logbook.Client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports ru.nstu.logbook;
    opens ru.nstu.logbook to javafx.fxml;
    exports ru.nstu.logbook.controllers;
    opens ru.nstu.logbook.controllers to javafx.fxml;

    exports ru.nstu.logbook.utils;
    exports ru.nstu.logbook.net;
    exports ru.nstu.logbook.notes;

}