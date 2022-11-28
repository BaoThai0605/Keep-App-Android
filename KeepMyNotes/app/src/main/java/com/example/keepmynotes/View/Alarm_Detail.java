package com.example.keepmynotes.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keepmynotes.Controller.AlarmForNote.AlarmReceiver;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.R;

public class Alarm_Detail extends AppCompatActivity {
    private TextView Title_Detail, Datetime_Detail, Content_Detail, SubTitle_Detail;
    private ImageView Img_Detail;
    private Button btn_Stop;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        initNote();
    }

    private void initNote() {
        int ID = getIntent().getIntExtra("ID", 1);
        Log.e("Note ID", String.valueOf(ID));
        note = DatabaseNote.getInstanceDTB(getApplicationContext()).noteDAO().getbyId(ID);
        if (note != null) {
            initView();
        } else return;
    }

    private void initView() {
        Title_Detail = findViewById(R.id.Title_Detail);
        Datetime_Detail = findViewById(R.id.Datetime_Detail);
        Content_Detail = findViewById(R.id.Content_Detail);
        SubTitle_Detail = findViewById(R.id.SubTitle_Detail);
        Img_Detail = findViewById(R.id.Img_Detail);
        btn_Stop = findViewById(R.id.btn_Stop);


        if (note.getTitle() != null) {
            Title_Detail.setText(note.getTitle());
        }
        if (note.getContent() != null) {
            Content_Detail.setText(note.getContent());
        }
        if(note.getSubtitle()!=null)
        {
            SubTitle_Detail.setText(note.getSubtitle());
        }

        Datetime_Detail.setText(note.getDatetime());

        if(!note.getImage().isEmpty())
        {
            Img_Detail.setImageBitmap( BitmapFactory.decodeFile(note.getImage()));
            Img_Detail.setVisibility(View.VISIBLE);
        }

        btn_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
                BacktoMainView();

            }
        });

    }

    private void BacktoMainView() {
        Intent intent = new Intent(Alarm_Detail.this, Notes_Fragment.class);
        startActivity(intent);
        finish();
    }

    private void stopAlarm() {
        Intent intent = new Intent(Alarm_Detail.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm_Detail.this, note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) Alarm_Detail.this.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        note.setAlarmTime("");
        note.setDatealarm("");
        DatabaseNote.getInstanceDTB(Alarm_Detail.this).noteDAO().createNote(note);
    }
}