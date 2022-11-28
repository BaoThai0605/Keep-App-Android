package com.example.keepmynotes.Model.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.keepmynotes.Model.DAO.UserDAO;
import com.example.keepmynotes.Model.User;

@Database(entities  = User.class,version = 1,exportSchema = false)
public abstract class DatabaseUser extends RoomDatabase {

    public static DatabaseUser database;

    public static synchronized DatabaseUser getInstance(Context context)
    {
        if(database==null)
        {
            return database = Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseUser.class,
                    "User.db")
                    .allowMainThreadQueries() // Cho phep crud
                    .build();
        }
        return  database;
    }

    public abstract UserDAO userDAO();


}
