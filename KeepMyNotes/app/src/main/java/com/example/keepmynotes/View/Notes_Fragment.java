package com.example.keepmynotes.View;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.keepmynotes.Controller.AlarmForNote.AlarmReceiver;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.View.CustomAdapter.RecycleApdapterHome;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Controller.EventListener.NoteEventListener;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class Notes_Fragment extends Fragment implements NoteEventListener {
    private List<Note> mlist;
    private List<Note> origanal;

    private EditText Searchtxt;
    private RecycleApdapterHome mAdapter;
    private ImageView imageView, mulcol, onecol;
    private Button btnVertifyAgain;
    private RecyclerView recyclerView;
    private LinearLayout ShowNullItem;
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_VIEW_OR_UPDATE = 2;
    private static final int REQUEST_VIEW_REFRESHNOTES = 3;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private int noteClicked = -1;
    private SharedPreferences sharedPreferences;
    private int MODE_VIEW;
    private User userCurrent;


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        userCurrent = DatabaseUser.getInstance(getActivity().getApplicationContext()).userDAO().getCurrentUser();
        mlist = new ArrayList<>();
        origanal = new ArrayList<>();



        //CheckList is Empty
        createNotificationChannel();

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AlarmNote";
            String description = "Channel for notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("AlarmNote", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllNotes(REQUEST_VIEW_REFRESHNOTES, false);
        onecol = view.findViewById(R.id.onecol);
        mulcol = view.findViewById(R.id.mulcol);
        ShowNullItem = view.findViewById(R.id.ShowNullItem);
        btnVertifyAgain = view.findViewById(R.id.btnVertifyAgain);
        Searchtxt = view.findViewById(R.id.Searchtxt);

        sharedPreferences = this.getActivity().getSharedPreferences("ModeViewData", MODE_PRIVATE);
        MODE_VIEW = sharedPreferences.getInt("ModeView", 1);
        //Listview and Adapter
        //Recycle View
        recyclerView = view.findViewById(R.id.listNotes);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(MODE_VIEW, StaggeredGridLayoutManager.VERTICAL));
        //Create Icon
        CreateModeView(MODE_VIEW);
        mAdapter = new RecycleApdapterHome(mlist, this);
        recyclerView.setAdapter(mAdapter);
        ShowNullItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });
        //Button Create
        imageView = view.findViewById(R.id.btnCreateNote);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userCurrent.getVertify() && mlist.size()==5)
                {
                    Toast.makeText(getActivity(), "Please Vertify Account to do more", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(getActivity(),
                        CreateNote_View.class), REQUEST_ADD);
            }
        });

        onecol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE_VIEW = 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("ModeView", MODE_VIEW);
                editor.commit();
                onecol.setVisibility(View.GONE);
                mulcol.setVisibility(View.VISIBLE);

                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(MODE_VIEW, StaggeredGridLayoutManager.VERTICAL));
            }
        });

        mulcol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE_VIEW = 2;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("ModeView", MODE_VIEW);
                editor.commit();
                onecol.setVisibility(View.VISIBLE);
                mulcol.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(MODE_VIEW, StaggeredGridLayoutManager.VERTICAL));
                Log.e("Mode view", "1");
            }
        });

        if(userCurrent!=null && !userCurrent.getVertify())
        {
            btnVertifyAgain.setVisibility(View.VISIBLE);
            btnVertifyAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(),Vertify_Again_Screen.class));
                }
            });
        }

        Searchtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String key  = Searchtxt.getText().toString().trim();
                mlist.clear();
                for(Note s : origanal)
                {
                    if(s.getTitle().contains(key) || s.getSubtitle().contains(key) || s.getContent().contains(key) || FilerLabel(s,key))
                    {
                        mlist.add(s);
                    }
                    mAdapter.notifyDataSetChanged();
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
        });


    }

    private void CheckListisEmty() {
        if (mlist.size() == 0) {
            if (ShowNullItem.getVisibility() == View.GONE) {
                ShowNullItem.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (ShowNullItem.getVisibility() == View.VISIBLE) {
                ShowNullItem.setVisibility(View.GONE);
            }
        }
    }

    private void CreateModeView(int mode_view) {
        if (MODE_VIEW == 1) {
            mulcol.setVisibility(View.VISIBLE);
        } else {
            onecol.setVisibility(View.VISIBLE);
        }
    }


    //Get all notes from database
    private void getAllNotes(final int code, final boolean isNoteDeleted) {
        class GetAllTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {

                return DatabaseNote
                        .getInstanceDTB(getActivity()
                                .getApplicationContext())
                        .noteDAO().getAll();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.e("List Notes", notes.toString());
                //Load all note when start activity
                switch (code) {
                    case REQUEST_ADD:
                        origanal.add(0, notes.get(0));
                        mlist.add(0, notes.get(0));
                        PushDataToFireBase(notes.get(0));
                        mAdapter.notifyItemInserted(0);
                        recyclerView.smoothScrollToPosition(0);
                        CheckListisEmty();
                        break;
                    case REQUEST_VIEW_REFRESHNOTES:
                        origanal.addAll(notes);
                        mlist.addAll(origanal);
                        mAdapter.notifyDataSetChanged();
                        CheckListisEmty();
                        break;
                    case REQUEST_VIEW_OR_UPDATE:
                        mlist.remove(noteClicked);
                        origanal.remove(noteClicked);
                        if (isNoteDeleted) {
                            mAdapter.notifyItemRemoved(noteClicked);
                        } else {
                            mlist.add(noteClicked, notes.get(noteClicked));
                            origanal.add(noteClicked, notes.get(noteClicked));
                            UpdateItemtoFireBase(notes.get(noteClicked));
                            mAdapter.notifyItemChanged(noteClicked);
                        }
                        CheckListisEmty();
                }
            }

            private void UpdateItemtoFireBase(Note note) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference reference = database.getReference(userCurrent.getUid()).child("ListNote");
                reference.child(String.valueOf(note.getId())).updateChildren(note.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getActivity(), "Update success", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        new GetAllTask().execute();
    }

    private void PushDataToFireBase(Note note) {

        String path = userCurrent.getUid();
        Log.e("Uid", userCurrent.getUid());
        DatabaseReference reference = database.getReference(path);
        reference.child("ListNote").child(String.valueOf(note.getId())).setValue(note, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.e("SUCCESS", "Push data");
                Toast.makeText(getActivity(), "Pushed Data to FireBase", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD) && (resultCode == Activity.RESULT_OK)) {
            getAllNotes(REQUEST_ADD, false);
        } else if ((requestCode == REQUEST_VIEW_OR_UPDATE) && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                getAllNotes(REQUEST_VIEW_OR_UPDATE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }

    @Override
    public void OnClicktoVieworUpdate(@NonNull Note note, int pos) {

        noteClicked = pos;

        if (note.getPassword() == null || note.getPassword().trim().isEmpty()) {
            Intent intent = new Intent(getActivity(), CreateNote_View.class);
            intent.putExtra("ViewOrUpdate", true);
            intent.putExtra("Note", note);
            startActivityForResult(intent, REQUEST_VIEW_OR_UPDATE);
        } else {
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            final View view = getLayoutInflater().inflate(R.layout.dialog_password_layout, null);
            final EditText inputpassword = (EditText) view.findViewById(R.id.inputpassword);
            final Button cancel_pass_btn = view.findViewById(R.id.cancel_pass_btn);
            final Button ok_pass_btn = view.findViewById(R.id.ok_pass_btn);
            final TextView errorpassword = view.findViewById(R.id.errorpassword);


            alert.setView(view);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(true);

            cancel_pass_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            ok_pass_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String PASSWORD_FROM_DIALOG;
                    PASSWORD_FROM_DIALOG = inputpassword.getText().toString();
                    if (note.getPassword().equals(PASSWORD_FROM_DIALOG)) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(getActivity(), CreateNote_View.class);
                        intent.putExtra("ViewOrUpdate", true);
                        intent.putExtra("Note", note);
                        startActivityForResult(intent, REQUEST_VIEW_OR_UPDATE);
                    } else {
                        errorpassword.setVisibility(View.VISIBLE);
                    }
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void OnLongClicktoSetAlarm(Note note, int pos) {
        final Calendar calendar = Calendar.getInstance();
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.option_alarm_layout, null);
        final Button btnAddalarm_dialog = view.findViewById(R.id.btnAddalarm_dialog);
        final Button btnAddalarm1m_dialog = view.findViewById(R.id.btnAddalarm1m_dialog);
        final Button removeAlarm = view.findViewById(R.id.removeAlarm);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        btnAddalarm_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showPickerDiaglog();
            }

            private void showPickerDiaglog() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                View view = getLayoutInflater().inflate(R.layout.activity_alarm_picker, null);
                final TextView DatePicker_txt, TimePicker_txt;
                final Button DatePicker, TimePicker, btn_setalarm, btn_cancel_setalarm;

                DatePicker = view.findViewById(R.id.DatePicker);
                TimePicker = view.findViewById(R.id.TimePicker);
                btn_setalarm = view.findViewById(R.id.btn_setalarm);
                btn_cancel_setalarm = view.findViewById(R.id.btn_cancel_setalarm);
                DatePicker_txt = view.findViewById(R.id.DatePicker_txt);
                TimePicker_txt = view.findViewById(R.id.TimePicker_txt);


                alert.setView(view);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(true);
                btn_cancel_setalarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                DatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker datePicker, int y, int m, int d) {
                                String date = d + "/" + (m + 1) + "/" + y;
                                DatePicker_txt.setText(date);
                                calendar.set(Calendar.DAY_OF_MONTH, d);
                                calendar.set(Calendar.MONTH, m);
                                calendar.set(Calendar.YEAR, y);
                            }
                        }, year, month, day);
                        dialog.show();
                    }
                });
                TimePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int min = calendar.get(Calendar.MINUTE);

                        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker timePicker, int h, int m) {
                                String time = h + ":" + m;
                                TimePicker_txt.setText(time);
                                calendar.set(Calendar.HOUR_OF_DAY, h);
                                calendar.set(Calendar.MINUTE, m);
                            }
                        }, hour, min, true);
                        dialog.show();
                    }
                });
                btn_setalarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (DatePicker_txt.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Choose Date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TimePicker_txt.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Choose Time", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                        intent.putExtra("ID_notification", note.getId());
                        if (!note.getContent().isEmpty()) {
                            intent.putExtra("Content", note.getContent());
                        } else {
                            intent.putExtra("Content", "");

                        }

                        if (!note.getTitle().isEmpty()) {
                            intent.putExtra("Title", note.getTitle());
                        } else {
                            intent.putExtra("Title", "");
                        }
                        alertDialog.dismiss();

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Toast.makeText(getActivity(), DatePicker_txt.getText().toString() + "-" + TimePicker_txt.getText().toString(), Toast.LENGTH_SHORT).show();
                        note.setAlarmTime(TimePicker_txt.getText().toString());
                        note.setDatealarm(DatePicker_txt.getText().toString());
                        DatabaseNote.getInstanceDTB(getActivity()).noteDAO().createNote(note);
                        mAdapter.notifyItemChanged(pos);
                    }
                });

                alertDialog.show();
            }
        });
        btnAddalarm1m_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
                int min = calendar.get(Calendar.MINUTE);
                String date = day + "/" + (month + 1) + "/" + year;
                String time = hour + ":" + min;
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("ID_notification", note.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(getActivity(), date + " - " + time, Toast.LENGTH_SHORT).show();
                note.setAlarmTime(date);
                note.setDatealarm(time);
                DatabaseNote.getInstanceDTB(getActivity()).noteDAO().createNote(note);
                mAdapter.notifyItemChanged(pos);

            }
        });
        if (!note.getDatealarm().isEmpty()) {
            removeAlarm.setVisibility(View.VISIBLE);
            removeAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    note.setAlarmTime("");
                    note.setDatealarm("");
                    DatabaseNote.getInstanceDTB(getActivity()).noteDAO().createNote(note);
                    mAdapter.notifyItemChanged(pos);

                }
            });
        }

        alertDialog.show();

    }


}
