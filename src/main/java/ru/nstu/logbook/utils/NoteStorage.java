package ru.nstu.logbook.utils;

import ru.nstu.logbook.controllers.PageController;
import ru.nstu.logbook.net.DBManager;
import ru.nstu.logbook.notes.Note;
import ru.nstu.logbook.notes.Reminder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NoteStorage {
    public final Map<LocalDate, Note> notes;
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

    public Map<LocalDate, Note> loadAll(){
        var dir = new File(path, NOTE_DIRECTORY);
        File[] files = dir.listFiles();

        Map<LocalDate, Note> allTheNotes = new HashMap<>();
        if(dir.exists()){
            for (var file : files) {
                try (var in = new ObjectInputStream(new FileInputStream(file))) {
                    if(file.getName().matches("YYYY-mm-dd")){
                        Note note = (Note) in.readObject();
                        allTheNotes.put(note.getDate(), note);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return allTheNotes;
    }

    public void save(Note note) {
        var notesDir = new File(path, NOTE_DIRECTORY);
        var noteFile = new File(notesDir, note.getDate().toString() + SAVE_EXTENSION);

        if (!notesDir.exists())
            notesDir.mkdir();

        if (!noteFile.exists()) {
            try {
                if(PageController.getUserId() != -1){
                    try {
                        DBManager.addNoteForUser(PageController.getUserId(), note);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
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

    public void saveAll(){
        synchronized (notes){
            for(var note : notes.values()){
                save(note);
            }
        }
    }

    public boolean delete(Note note) {
        var notesDir = new File(path, NOTE_DIRECTORY);
        var noteFile = new File(notesDir, note.getDate().toString() + SAVE_EXTENSION);

        if(PageController.getUserId() != -1){
            try {
                DBManager.deleteNoteForUser(PageController.getUserId(), note.getDate());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
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
