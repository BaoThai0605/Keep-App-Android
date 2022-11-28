package com.example.keepmynotes.Model.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.Note_Deleted;

import java.util.List;

@Dao
public interface RecycleBinDAO {
    @Query("SELECT * FROM note_deleteds ORDER BY id DESC")
    List<Note_Deleted> getAllRecycleBin();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertBin(Note_Deleted recycleBin);

    @Query("SELECT * FROM note_deleteds WHERE id = :id")
    Note_Deleted getNoteDeletebyId(int id);

    @Query("DELETE FROM note_deleteds")
    void DeleteAllRecycle();

    @Delete
    void deleteBin(Note_Deleted recycleBin);
}
