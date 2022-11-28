package com.example.keepmynotes.Controller.EventListener;

import com.example.keepmynotes.Model.Note;

public interface NoteEventListener {
     void OnClicktoVieworUpdate(Note note, int pos);
     void OnLongClicktoSetAlarm(Note note, int pos);
}
