package com.example.keepmynotes.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.keepmynotes.Controller.EventListener.LabelCheck;
import com.example.keepmynotes.Controller.EventListener.Label_Listener;
import com.example.keepmynotes.Model.Database.DatabaseLabels;
import com.example.keepmynotes.Model.Labels;
import com.example.keepmynotes.R;
import com.example.keepmynotes.View.CustomAdapter.RecycleApdapterLabels;
import com.example.keepmynotes.View.CustomAdapter.RecycleApdapterLabelsSeleted;

import java.util.ArrayList;
import java.util.List;

public class PickLabel_Screen extends AppCompatActivity implements LabelCheck {

    private List<Labels> mlist;
    private RecycleApdapterLabelsSeleted mAdapter;
    private RecyclerView rcl_label_select;
    private Button btnsavelabel;
    private String labels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pick_label_screen);

        intData();
        initView();
    }

    private void intData() {
        labels = getIntent().getStringExtra("Labelss");
        mlist = new ArrayList<>();
        mlist = DatabaseLabels.getInstance(getApplicationContext()).labelsDAO().getAllLabels();
        String[] words = labels.split(",");
        for(String s : words)
        {
           for(Labels labels : mlist)
           {
               if(labels.getNamelabels().equals(s.trim()))
               {
                   labels.setCheckLabel(true);
               }
           }
        }

    }

    private void initView() {
        rcl_label_select = findViewById(R.id.rcl_label_select);
        btnsavelabel = findViewById(R.id.btnsavelabel);
        rcl_label_select.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new RecycleApdapterLabelsSeleted(mlist, labels,this);
        rcl_label_select.setAdapter(mAdapter);

        btnsavelabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "";
                for(int i = 0; i < mlist.size();i++)
                {
                    if(mlist.get(i).isCheckLabel())
                    {
                        result = result  + mlist.get(i).getNamelabels()+", ";
                    }
                }
                if(!result.isEmpty())
                {
                    result= result.substring(0, result.lastIndexOf(","));
                }

                Intent intent = new Intent();
                intent.putExtra("ResultLabel", result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    @Override
    public void OnCheck(boolean b, int pos) {
        mlist.get(pos).setCheckLabel(b);
    }
}