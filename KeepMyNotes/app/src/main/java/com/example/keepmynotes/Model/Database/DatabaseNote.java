package com.example.keepmynotes.Model.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.keepmynotes.Model.DAO.NoteDAO;
import com.example.keepmynotes.Model.Note;

@Database(entities = Note.class,version = 1,exportSchema = false)
public abstract class DatabaseNote extends RoomDatabase {

    public static DatabaseNote database;

    //Singleton Pattern
    public static synchronized  DatabaseNote getInstanceDTB(Context context)
    {
        if(database == null)
        {
            return database = Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseNote.class,
                    "Notes.db")
                    .allowMainThreadQueries() // Cho phep crud
                    .build();
        }

        return database;
    }

    public abstract NoteDAO noteDAO();
}
