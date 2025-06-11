package ru.nstu.logbook.Client.utils;

import ru.nstu.logbook.Client.notes.Note;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NoteStorage {
    public Map<LocalDate, Note> notes;
    private final static String OPTIONS_DEFAULT_PATH = "./src/main/resources/ru/nstu/logbook/Options.ini";
    private final static String DEFAULT_PATH = "./src/main/resources/ru/nstu/logbook/";
    private final static String NOTE_DIRECTORY = "notes";
    private final static String SAVE_EXTENSION = ".bin";
    private String path;
    private static NoteStorage instance;

    public static NoteStorage getInstance(String path) {
        if (instance == null)
            instance = new NoteStorage(path);
        return instance;
    }

    public static NoteStorage getInstance() {
        if (instance == null)
            instance = new NoteStorage();
        return instance;
    }

    private NoteStorage(String path) {
        this.path = path;
        if (this.path == null) {
            this.path = System.getenv("HOME");
        }
        notes = new HashMap<>();
    }

    private NoteStorage() {
        this(DEFAULT_PATH);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void loadMonth(LocalDate date) {
        notes.clear();
        var local = LocalDate.of(date.getYear(), date.getMonth(), 1);
        for (int days = 1; days <= local.lengthOfMonth(); days++) {

            Note loaded = load(local);
            if (loaded != null) {
                notes.put(local, loaded);
            }
            if (local.lengthOfMonth() == local.getDayOfMonth()) {
                return;
            }
            local = local.plusDays(1);
        }
    }

    public void loadPeriod(LocalDate since, LocalDate to) {
        if(since.isAfter(to))
            return;

        notes.clear();
        var local = since;

        do{
            NoteStorage.getInstance().load(local);
            Note loaded = load(local);
            if (loaded != null) {
                notes.put(local, loaded);
            }
            local = local.plusDays(1);
        }while(!local.isAfter(to));
    }

    public Note load(LocalDate date) {

        var notesDir = new File(path, NOTE_DIRECTORY);
        var noteFile = new File(notesDir, date.toString() + SAVE_EXTENSION);
        if (noteFile.exists()) {
            try (var in = new ObjectInputStream(new FileInputStream(noteFile))) {
                return (Note) in.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void save(Note note) {
        var notesDir = new File(path, NOTE_DIRECTORY);
        var noteFile = new File(notesDir, note.getDate().toString() + SAVE_EXTENSION);

        if (!notesDir.exists())
            notesDir.mkdir();

        if (!noteFile.exists()) {
            try {
                noteFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(noteFile))) {
            out.writeObject(note);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Note note) {
        var notesDir = new File(path, NOTE_DIRECTORY);
        var noteFile = new File(notesDir, note.getDate().toString() + SAVE_EXTENSION);
        try {
            return Files.deleteIfExists(Path.of(noteFile.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
