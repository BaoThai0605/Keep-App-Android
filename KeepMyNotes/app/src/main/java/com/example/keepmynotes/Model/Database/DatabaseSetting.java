package com.example.keepmynotes.Model.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.keepmynotes.Model.DAO.SettingDAO;
import com.example.keepmynotes.Model.SettingNotes;

@Database(entities = SettingNotes.class,version = 2,exportSchema = false)
public abstract class DatabaseSetting extends RoomDatabase {
    public static  DatabaseSetting databaseSetting;

    public static synchronized  DatabaseSetting getInstance(Context context)
    {
        if(databaseSetting == null)
        {
            return databaseSetting = Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseSetting.class,
                    "Setting.db")
                    .allowMainThreadQueries() // Cho phep crud
                    .build();
        }

        return databaseSetting;
    }

    public abstract SettingDAO settingDAO();
}
