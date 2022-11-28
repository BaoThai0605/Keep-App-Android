package com.example.keepmynotes.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.Window;

import com.example.keepmynotes.Controller.EventListener.NoteEventListener;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.R;
import com.example.keepmynotes.View.CustomAdapter.RecycleApdapterHome;

import java.util.ArrayList;
import java.util.List;

public class ViewByLabel_Screen extends AppCompatActivity implements NoteEventListener {

    private List<Note> mlist_org;
    private List<Note> mlist_copy;
    private RecyclerView recyclerView;
    private RecycleApdapterHome recycleApdapterHome;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_by_label_screen);
        
        initData();
        initView();
    }

    private void initData() {
        key = getIntent().getStringExtra("labelquery");
        mlist_org = DatabaseNote.getInstanceDTB(getApplicationContext()).noteDAO().getAll();
        mlist_copy = new ArrayList<>();

        for(Note s : mlist_org)
        {
            if(FilerLabel(s,key))
            {
                mlist_copy.add(s);
            }
        }


    }

    private boolean FilerLabel(Note s, String key) {
        String [] arr = s.getLabels().split(",");
        for (String txt : arr)
        {
            if(txt.trim().contains(key))
            {
                return true;
            }
        }
        return false;
    }



    private void initView() {
        recyclerView = findViewById(R.id.listviewbylabel);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycleApdapterHome = new RecycleApdapterHome(mlist_copy, this);
        recyclerView.setAdapter(recycleApdapterHome);
    }

    @Override
    public void OnClicktoVieworUpdate(Note note, int pos) {

    }

    @Override
    public void OnLongClicktoSetAlarm(Note note, int pos) {

    }
}