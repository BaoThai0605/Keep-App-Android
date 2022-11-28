package com.example.keepmynotes.Controller.EventListener;

import com.example.keepmynotes.Model.Note_Deleted;

public interface NoteDelete_Event_Listener {
     void OnRestore(Note_Deleted note, int pos);
     void OnDelete(Note_Deleted note, int pos);
}
