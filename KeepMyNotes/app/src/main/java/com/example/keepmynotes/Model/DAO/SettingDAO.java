package com.example.keepmynotes.Model.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.keepmynotes.Model.SettingNotes;

@Dao
public interface SettingDAO {

    @Query("SELECT  * FROM Setting LIMIT 1")
    SettingNotes getCurrentSetting();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertSetting(SettingNotes settingNotes);

    @Query("DELETE FROM Setting")
    void DeleteSetting();
}
