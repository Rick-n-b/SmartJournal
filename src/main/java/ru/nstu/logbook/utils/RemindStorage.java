package ru.nstu.logbook.utils;

import ru.nstu.logbook.notes.Reminder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class RemindStorage {

    public ArrayList<Reminder> reminds;
    private final static String OPTIONS_DEFAULT_PATH = "./src/main/resources/ru/nstu/logbook/Options.ini";
    private final static String DEFAULT_PATH = "./src/main/resources/ru/nstu/logbook/";
    private final static String NOTE_DIRECTORY = "reminds";
    private final static String SAVE_EXTENSION = ".bin";
    private String path;
    private static RemindStorage instance;

    public static RemindStorage getInstance(String path) {
        if (instance == null)
            instance = new RemindStorage(path);
        return instance;
    }

    public static RemindStorage getInstance() {
        if (instance == null)
            instance = new RemindStorage();
        return instance;
    }

    private RemindStorage(String path) {
        this.path = path;
        if (this.path == null) {
            this.path = System.getenv("HOME");
        }
        reminds = new ArrayList<>();
    }

    private RemindStorage() {
        this(DEFAULT_PATH);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void loadMonth(LocalDate date) {
        reminds.clear();
        var local = date.minusDays(30);
        for (int days = 1; days <= 31 * 2; days++) {

            ArrayList<Reminder> loaded = load(local);
            if (loaded != null) {
                reminds.addAll(loaded);
            }

            local = local.plusDays(1);
        }
    }

    public Reminder load(LocalDate date, String topic) {

        var dir = new File(path, NOTE_DIRECTORY);
        var file = new File(dir, date.toString() + SAVE_EXTENSION);
        if (file.exists()) {
            for(var rem : load(date)){
                if(rem.getTopic().equals(topic))
                    return rem;
            }
        }
        return null;
    }

    public ArrayList<Reminder> load(LocalDate date) {

        var dir = new File(path, NOTE_DIRECTORY);
        var file = new File(dir, date.toString() + SAVE_EXTENSION);
        if (file.exists()) {
            try (var in = new ObjectInputStream(new FileInputStream(file))) {
                return (ArrayList<Reminder>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void save(Reminder reminder) {
        var dir = new File(path, NOTE_DIRECTORY);
        var file = new File(dir, reminder.getExpirationDate().toString() + SAVE_EXTENSION);
        var reminders = new ArrayList<Reminder>();
        if (!dir.exists())
            dir.mkdir();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            reminders = load(reminder.getExpirationDate());
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            reminders.add(reminder);
            out.writeObject(reminders);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean delete(Reminder reminder) {
        var dir = new File(path, NOTE_DIRECTORY);
        var file = new File(dir, reminder.getExpirationDate().toString() + SAVE_EXTENSION);
        reminds.remove(reminder);
        ArrayList<Reminder> reminders = load(reminder.getExpirationDate());
        try {
            if(reminders != null){
                for(var rem : reminders) {
                    if(rem.getContent().equals(reminder.getContent()) &&
                            rem.getExpirationTime().toString().equals(reminder.getExpirationTime().toString()) &&
                            rem.getTopic().equals(reminder.getTopic()) &&
                            rem.getExpirationDate().toString().equals(reminder.getExpirationDate().toString())
                    ){
                        reminders.remove(rem);
                        reminds.remove(rem);
                        if (reminders.isEmpty())
                            return Files.deleteIfExists(Path.of(file.getPath()));
                        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                            reminders.remove(reminder);
                            out.writeObject(reminders);
                            out.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return true;
                    }
                }
            }
            return false;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteAll(){
        var dir = new File(path, NOTE_DIRECTORY);
        File[] files = dir.listFiles();
        if(dir.exists())
            for (var file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
    }

    public void loadAll(){
        var dir = new File(path, NOTE_DIRECTORY);
        File[] files = dir.listFiles();

        ArrayList<Reminder> allTheRems = new ArrayList<>();
        if(dir.exists()){
            reminds.clear();
            for (var file : files) {
                try (var in = new ObjectInputStream(new FileInputStream(file))) {
                    if(file.getName().matches("YYYY-mm-dd"))
                        allTheRems.addAll((ArrayList<Reminder>) in.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void saveAll(){
        for(var rem : reminds){
            save(rem);
        }
    }


    public synchronized void loadConf() {
        String path = OPTIONS_DEFAULT_PATH;
        Properties properties = new Properties();
        var options = new File(path);
        try {
            if (options.exists()) {
                properties.load(Files.newInputStream(options.toPath()));
                this.path = properties.getProperty("saveDirectoryPath");
            } else {
                options.createNewFile();
            }
        } catch (IOException e) {
            System.err.println(": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public synchronized void saveConf() {
        String path = OPTIONS_DEFAULT_PATH;
        var options = new File(path);
        try {
            if (options.exists()) {
                Properties properties = new Properties();
                properties.setProperty("saveDirectoryPath", this.path);
                properties.store(Files.newOutputStream(Path.of(path)), "LogBook options");
            } else {
                options.createNewFile();
                saveConf();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
