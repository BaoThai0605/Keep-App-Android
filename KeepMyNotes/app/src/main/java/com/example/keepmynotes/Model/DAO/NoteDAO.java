package com.example.keepmynotes.Model.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.keepmynotes.Model.Note;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAll();

    @Query("SELECT * FROM notes WHERE id = :id")
    Note getbyId(int id);

    @Query("DELETE FROM notes")
    void DeleteAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createNote(Note note);

    @Delete
    void deleNote(Note note);


}
