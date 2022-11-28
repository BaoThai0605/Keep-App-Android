package com.example.keepmynotes.Model;

import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "note_deleteds")
public class Note_Deleted extends Note implements Serializable {
    public Note_Deleted() {
        super();
    }
}
