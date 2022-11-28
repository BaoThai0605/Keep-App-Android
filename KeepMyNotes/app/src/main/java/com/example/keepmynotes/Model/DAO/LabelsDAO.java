package com.example.keepmynotes.Model.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.keepmynotes.Model.Labels;

import java.util.List;

@Dao
public interface LabelsDAO {

    @Query("SELECT * FROM tblabels ORDER BY id_labels DESC")
    List<Labels> getAllLabels();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertLabel(Labels labels);

    @Delete
    void DeleteLabel(Labels labels);

    @Query("DELETE FROM tblabels")
    void DeleteAllLabel();

}
