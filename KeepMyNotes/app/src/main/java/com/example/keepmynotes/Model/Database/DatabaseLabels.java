package com.example.keepmynotes.Model.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.keepmynotes.Model.DAO.LabelsDAO;
import com.example.keepmynotes.Model.Labels;

@Database(entities = Labels.class,version = 2,exportSchema = false)
public abstract class DatabaseLabels extends RoomDatabase {
    public static DatabaseLabels database;

    public static synchronized DatabaseLabels getInstance(Context context)
    {
        if(database == null)
        {
            return database = Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseLabels.class,
                    "Labels.db")
                    .allowMainThreadQueries() // Cho phep crud
                    .build();

        }
        return  database;
    }

    public abstract LabelsDAO labelsDAO();
}
