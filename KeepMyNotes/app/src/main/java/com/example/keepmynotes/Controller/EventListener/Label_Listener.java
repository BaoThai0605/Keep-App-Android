package com.example.keepmynotes.Controller.EventListener;

import com.example.keepmynotes.Model.Labels;

public interface Label_Listener {
    void OnLongClick(Labels labels, int pos);
    void OnClicktoDelete(Labels labels, int pos);
    void OnClickViewLabel(Labels labels, int pos);
}
