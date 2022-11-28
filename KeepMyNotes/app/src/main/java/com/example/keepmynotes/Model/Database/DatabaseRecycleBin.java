package com.example.keepmynotes.Model.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.keepmynotes.Model.DAO.RecycleBinDAO;
import com.example.keepmynotes.Model.Note_Deleted;

@Database(entities  = Note_Deleted.class,version = 1,exportSchema = false)
public abstract class DatabaseRecycleBin extends RoomDatabase {

    public static DatabaseRecycleBin database;

    //Singleton Pattern
    public static synchronized DatabaseRecycleBin getInstanceDTB(Context context)
    {
        if(database == null)
        {
            return database = Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseRecycleBin.class,
                    "RecycleBin.db")
                    .allowMainThreadQueries() // Cho phep crud
                    .build();
        }

        return database;
    }

    public abstract RecycleBinDAO recycleBinDAO();
}
