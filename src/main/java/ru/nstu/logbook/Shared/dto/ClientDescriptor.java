package ru.nstu.logbook.Shared.dto;


import java.io.Serializable;

public record ClientDescriptor(int id, String name) implements Serializable {
    public static final ClientDescriptor UNREGISTERED = new ClientDescriptor(-1, "UNREGISTERED");
}