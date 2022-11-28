package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.keepmynotes.Controller.Worker.DeleteWorker;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseRecycleBin;
import com.example.keepmynotes.Model.Database.DatabaseSetting;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.Note_Deleted;
import com.example.keepmynotes.Model.SettingNotes;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CreateNote_View extends AppCompatActivity {

    private EditText title_txt, subtitletxt, contenttxt;
    private TextView datetimetxt;
    private String currentcolor;
    private CoordinatorLayout layout;
    private ImageView imgtxt;
    private VideoView videostxt;
    private String selectedImagePath, DATE_ALARM, TIME_ALARM, Video_Path, Sound_Path, label;
    private String PASSWORD_FOR_NOTE;
    private SettingNotes settingNotes;
    private LinearLayout layoutdisplaylabels;
    private  ImageView btnShare;

    private static final int REQUEST_ADD_IMG = 27;
    private static final int REQUEST_ADD_VIDEO = 651;
    private static final int REQUEST_STORAGE_PERMISSION = 8;
    private static final int REQUEST_ADDLABELS = 212;

    private Note note_item_to_view_or_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_note_view);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        settingNotes = DatabaseSetting.getInstance(getApplicationContext()).settingDAO().getCurrentSetting();
        initView();
        CreateNotification();
    }

    private void CreateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "AlarmChanel";
            String decription = "Channel for alarm manager";
            int ROLE = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("title", name, ROLE);

            channel.setDescription(decription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initView() {
        //########################################################################
        currentcolor = "#FFFFFFFF";
        selectedImagePath = "";
        PASSWORD_FOR_NOTE = "";
        DATE_ALARM = "";
        TIME_ALARM = "";
        Video_Path = "";
        Sound_Path = "";
        label = "";


        title_txt = findViewById(R.id.title_txt);
        subtitletxt = findViewById(R.id.subtitletxt);
        contenttxt = findViewById(R.id.contenttxt);
        datetimetxt = findViewById(R.id.datetimetxt);
        layout = findViewById(R.id.layoutCreateNote);
        imgtxt = findViewById(R.id.imgtxt);
        layoutdisplaylabels = findViewById(R.id.layoutdisplaylabels);
        videostxt = findViewById(R.id.videostxt);
        ImageView savebtn = findViewById(R.id.btnSave);
        ImageView backbtn = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);


        //########################################################################
        datetimetxt.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date()));
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNote();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareData();
            }
        });


        //########################################################################
        DisplayLabel(label);
        checkNoteExist();
        intTextSizeAndFont();
        initOptionNote();
        initSetUpColor();


    }

    private void shareData() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, note_item_to_view_or_update.toSendNote());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


    private void intTextSizeAndFont() {
        title_txt.setTextSize(settingNotes.getTextsize());
        subtitletxt.setTextSize(settingNotes.getTextsize());
        contenttxt.setTextSize(settingNotes.getTextsize());

        title_txt.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.BOLD_ITALIC));
        subtitletxt.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
        contenttxt.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
        datetimetxt.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
    }

    private void checkNoteExist() {
        //Check Boolean name "ViewOrUpdate" is exist. if not exist default false
        if (getIntent().getBooleanExtra("ViewOrUpdate", false)) {
            note_item_to_view_or_update = (Note) getIntent().getSerializableExtra("Note");
            setViewtoUpdate();
        }
        else
        {
            btnShare.setVisibility(View.GONE);
        }

    }

    private void setViewtoUpdate() {
        btnShare.setVisibility(View.VISIBLE);
        PASSWORD_FOR_NOTE = note_item_to_view_or_update.getPassword();

        if(note_item_to_view_or_update.getLabels()!=null)
        {
            label = note_item_to_view_or_update.getLabels();
            DisplayLabel(label);
        }
        title_txt.setText(note_item_to_view_or_update.getTitle());
        subtitletxt.setText(note_item_to_view_or_update.getSubtitle());
        contenttxt.setText(note_item_to_view_or_update.getContent());
        currentcolor = note_item_to_view_or_update.getColor();
        if (note_item_to_view_or_update.getAlarmTime() != null && note_item_to_view_or_update.getDatealarm() != null) {
            DATE_ALARM = note_item_to_view_or_update.getDatealarm();
            TIME_ALARM = note_item_to_view_or_update.getAlarmTime();


        }

        layout.setBackgroundColor(Color.parseColor(note_item_to_view_or_update.getColor()));

        if (note_item_to_view_or_update.getImage() != null && !note_item_to_view_or_update.getImage().trim().isEmpty()) {
            imgtxt.setImageBitmap(BitmapFactory.decodeFile(note_item_to_view_or_update.getImage()));
            imgtxt.setVisibility(View.VISIBLE);
            selectedImagePath = note_item_to_view_or_update.getImage();
            findViewById(R.id.btnRemoveImg).setVisibility(View.VISIBLE);
        }
        if (note_item_to_view_or_update.getVideo() != null && !note_item_to_view_or_update.getVideo().trim().isEmpty()) {
            DisplayVideo(note_item_to_view_or_update.getVideo());
        }
        findViewById(R.id.btnRemoveImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgtxt.setImageBitmap(null);
                imgtxt.setVisibility(View.GONE);
                findViewById(R.id.btnRemoveImg).setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });

        findViewById(R.id.btnRemoveVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videostxt.setVisibility(View.GONE);
                findViewById(R.id.btnRemoveVideo).setVisibility(View.GONE);
                Video_Path = "";
            }
        });
    }

    private void initSetUpColor() {
        layout.setBackgroundColor(Color.parseColor(currentcolor));
    }

    private void initOptionNote() {
        LinearLayout layoutOption = findViewById(R.id.layoutOptionnote);
        BottomSheetBehavior<LinearLayout> bottomOption = BottomSheetBehavior.from(layoutOption);
        layoutOption.findViewById(R.id.optioncolor)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bottomOption.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            bottomOption.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                });

        final ImageView imgcolordefalt = layoutOption.findViewById(R.id.imgcolordefalt);
        final ImageView imgcolorRed = layoutOption.findViewById(R.id.imgcolorRed);
        final ImageView imgcolorGreen = layoutOption.findViewById(R.id.imgcolorGreen);
        final ImageView imgcolorBlue = layoutOption.findViewById(R.id.imgcolorBlue);
        final ImageView imgcolorYellow = layoutOption.findViewById(R.id.imgcolorYellow);

        layoutOption.findViewById(R.id.colordefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentcolor = "#FFFFFFFF";
                imgcolordefalt.setImageResource(R.drawable.ic_done);
                imgcolorRed.setImageResource(0);
                imgcolorGreen.setImageResource(0);
                imgcolorBlue.setImageResource(0);
                imgcolorYellow.setImageResource(0);
                initSetUpColor();
            }
        });

        layoutOption.findViewById(R.id.colorYellow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentcolor = "#FFFF00";
                imgcolordefalt.setImageResource(0);
                imgcolorRed.setImageResource(0);
                imgcolorGreen.setImageResource(0);
                imgcolorBlue.setImageResource(0);
                imgcolorYellow.setImageResource(R.drawable.ic_done);
                initSetUpColor();
            }
        });

        layoutOption.findViewById(R.id.colorBlue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentcolor = "#00FFFF";
                imgcolordefalt.setImageResource(0);
                imgcolorRed.setImageResource(0);
                imgcolorGreen.setImageResource(0);
                imgcolorBlue.setImageResource(R.drawable.ic_done);
                imgcolorYellow.setImageResource(0);
                initSetUpColor();
            }
        });

        layoutOption.findViewById(R.id.colorGreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentcolor = "#7CFC00";
                imgcolordefalt.setImageResource(0);
                imgcolorRed.setImageResource(0);
                imgcolorGreen.setImageResource(R.drawable.ic_done);
                imgcolorBlue.setImageResource(0);
                imgcolorYellow.setImageResource(0);
                initSetUpColor();
            }
        });

        layoutOption.findViewById(R.id.colorRed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentcolor = "#FF0000";
                imgcolordefalt.setImageResource(0);
                imgcolorRed.setImageResource(R.drawable.ic_done);
                imgcolorGreen.setImageResource(0);
                imgcolorBlue.setImageResource(0);
                imgcolorYellow.setImageResource(0);
                initSetUpColor();
            }
        });

        if (note_item_to_view_or_update != null && note_item_to_view_or_update.getColor() != null && !note_item_to_view_or_update.getColor().trim().isEmpty()) {
            switch (note_item_to_view_or_update.getColor()) {
                case "#FFFFFFFF":
                    layoutOption.findViewById(R.id.colordefault).performClick();
                    break;
                case "#FF0000":
                    layoutOption.findViewById(R.id.colorRed).performClick();
                    break;
                case "#7CFC00":
                    layoutOption.findViewById(R.id.colorGreen).performClick();
                    break;
                case "#00FFFF":
                    layoutOption.findViewById(R.id.colorBlue).performClick();
                    break;
                case "#FFFF00":
                    layoutOption.findViewById(R.id.colorYellow).performClick();
                    break;
            }
        }

        layoutOption.findViewById(R.id.layoutAddimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat
                        .checkSelfPermission(
                                getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNote_View.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                } else {
                    addImage();
                }
            }
        });

        layoutOption.findViewById(R.id.layoutaddLabels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNote_View.this, PickLabel_Screen.class);
                intent.putExtra("Labelss", label);
                startActivityForResult(intent, REQUEST_ADDLABELS);

            }
        });

        layoutOption.findViewById(R.id.layoutaddVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat
                        .checkSelfPermission(
                                getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNote_View.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                } else {
                    addVideo();
                }
            }
        });
        if (PASSWORD_FOR_NOTE.isEmpty()) {
            layoutOption.findViewById(R.id.layoutsetpassword).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(CreateNote_View.this);
                    View view = getLayoutInflater().inflate(R.layout.dialog_set_password_layout, null);
                    final EditText input_set_password = (EditText) view.findViewById(R.id.input_set_password);
                    final EditText input_set_repassword = (EditText) view.findViewById(R.id.input_set_repassword);
                    final Button cancel_pass_btn = view.findViewById(R.id.cancel_setpass_btn);
                    final Button ok_pass_btn = view.findViewById(R.id.ok_setpass_btn);
                    final TextView errorsetpass = view.findViewById(R.id.errorsetpass);

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
                            String password, repassword;
                            password = input_set_password.getText().toString();
                            repassword = input_set_repassword.getText().toString();
                            if (password.equals(repassword)) {
                                PASSWORD_FOR_NOTE = password;
                                alertDialog.dismiss();
                                Toast.makeText(CreateNote_View.this, "Please Save this note", Toast.LENGTH_SHORT).show();
                            } else {
                                errorsetpass.setText("Password and Confrim Password is not match");
                                errorsetpass.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    alertDialog.show();

                }
            });
        } else {
            layoutOption.findViewById(R.id.layoutsetpassword).setVisibility(View.GONE);
            layoutOption.findViewById(R.id.changepassword_note).setVisibility(View.VISIBLE);
            layoutOption.findViewById(R.id.removepassword_note).setVisibility(View.VISIBLE);
            layoutOption.findViewById(R.id.changepassword_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(CreateNote_View.this);
                    View view = getLayoutInflater().inflate(R.layout.dialog_changepassword_layout, null);
                    final EditText oldpass_txt = (EditText) view.findViewById(R.id.oldpass_txt);
                    final EditText newpass_txt = (EditText) view.findViewById(R.id.newpass_txt);
                    final EditText newrepass_txt = (EditText) view.findViewById(R.id.newrepass_txt);
                    final Button cancel_changepass_btn = view.findViewById(R.id.cancel_changepass_btn);
                    final Button ok_changepass_btn = view.findViewById(R.id.ok_changepass_btn);
                    final TextView errorchange = view.findViewById(R.id.errorchange);

                    alert.setView(view);
                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(true);

                    cancel_changepass_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    ok_changepass_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String oldpass, password, repassword;
                            oldpass = oldpass_txt.getText().toString();
                            password = newpass_txt.getText().toString();
                            repassword = newrepass_txt.getText().toString();
                            if (oldpass.equals(PASSWORD_FOR_NOTE)) {
                                if (password.equals(repassword)) {
                                    PASSWORD_FOR_NOTE = password;
                                    alertDialog.dismiss();
                                    Toast.makeText(CreateNote_View.this, "Please Save this note", Toast.LENGTH_SHORT).show();
                                } else {
                                    errorchange.setText("Password and Confrim Password is not match");
                                    errorchange.setVisibility(View.VISIBLE);
                                }
                            } else {
                                errorchange.setText("Old pass is not correct");
                                errorchange.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    alertDialog.show();
                }
            });
            layoutOption.findViewById(R.id.removepassword_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(CreateNote_View.this);
                    View view = getLayoutInflater().inflate(R.layout.dialog_remove_password_layout, null);
                    final Button cancel_remove_pass_btn = view.findViewById(R.id.cancel_remove_pass_btn);
                    final Button ok_remove_pass_btn = view.findViewById(R.id.ok_remove_pass_btn);

                    alert.setView(view);
                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(true);

                    cancel_remove_pass_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    ok_remove_pass_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            PASSWORD_FOR_NOTE = "";
                            Toast.makeText(CreateNote_View.this, "Please Save this note", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.show();
                }
            });
        }

        if (note_item_to_view_or_update != null) {
            layoutOption.findViewById(R.id.deletenote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomOption.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            class MoveNote_to_RecycleBin extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    Note_Deleted temp = HashNote(note_item_to_view_or_update);
                                    DatabaseRecycleBin
                                            .getInstanceDTB(getApplicationContext())
                                            .recycleBinDAO()
                                            .InsertBin(temp);

                                    DatabaseNote.
                                            getInstanceDTB(getApplicationContext()).
                                            noteDAO().
                                            deleNote(note_item_to_view_or_update);
                                    AddtoRecycleBinInFireBase(temp);
                                    DeleteNotetoFireBase(note_item_to_view_or_update);
                                    return null;
                                }

                                private Note_Deleted HashNote(Note note_hash) {
                                    Note_Deleted note_deleted = new Note_Deleted();

                                    note_deleted.setId(note_hash.getId());

                                    note_deleted.setTitle(note_hash.getTitle());
                                    note_deleted.setDatetime(note_hash.getDatetime());
                                    note_deleted.setSubtitle(note_hash.getSubtitle());
                                    note_deleted.setContent(note_hash.getContent());

                                    note_deleted.setImage(note_hash.getImage());
                                    note_deleted.setVideo(note_hash.getVideo());
                                    note_deleted.setSound(note_hash.getSound());


                                    note_deleted.setPassword(note_hash.getPassword());

                                    note_deleted.setColor(note_hash.getColor());

                                    note_deleted.setAlarmTime(note_hash.getAlarmTime());
                                    note_deleted.setDatealarm(note_hash.getDatealarm());
                                    note_deleted.setLabels(note_hash.getLabels());
                                    User user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();
                                    //Use WorkerManagerForWork
                                    Data data = new Data.Builder()
                                            .putInt("ID_Note", note_deleted.getId())
                                            .putString("Uid", user.getUid())
                                            .build();
                                    WorkRequest request;
                                    switch (settingNotes.getTimeUnit()) {
                                        case "SECONDS":
                                            request = new OneTimeWorkRequest.Builder(DeleteWorker.class)
                                                    .addTag(String.valueOf(note_deleted.getId()))
                                                    .setInitialDelay(settingNotes.getTimedelete(), TimeUnit.SECONDS)
                                                    .setInputData(data)
                                                    .build();
                                            WorkManager.getInstance(getApplicationContext()).enqueue(request);
                                            break;
                                        case "HOUR":
                                            request = new OneTimeWorkRequest.Builder(DeleteWorker.class)
                                                    .addTag(String.valueOf(note_deleted.getId()))
                                                    .setInitialDelay(settingNotes.getTimedelete(), TimeUnit.HOURS)
                                                    .setInputData(data)
                                                    .build();
                                            WorkManager.getInstance(getApplicationContext()).enqueue(request);
                                            break;
                                        case "DAYS":
                                            request = new OneTimeWorkRequest.Builder(DeleteWorker.class)
                                                    .addTag(String.valueOf(note_deleted.getId()))
                                                    .setInitialDelay(settingNotes.getTimedelete(), TimeUnit.DAYS)
                                                    .setInputData(data)
                                                    .build();
                                            WorkManager.getInstance(getApplicationContext()).enqueue(request);
                                            break;

                                    }
                                    return note_deleted;
                                }

                                @Override
                                protected void onPostExecute(Void unused) {
                                    super.onPostExecute(unused);
                                    Intent intent = new Intent();
                                    intent.putExtra("isNoteDeleted", true);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }

                                private void AddtoRecycleBinInFireBase(Note_Deleted temp) {
                                    User user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                                    DatabaseReference reference = database.getReference(user.getUid()).child("RecycleBin");
                                    reference.child(String.valueOf(temp.getId())).setValue(temp, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(getApplicationContext(), "Note is added in RecycleBin on Firebase", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                private void DeleteNotetoFireBase(Note note) {
                                    User user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                                    DatabaseReference reference = database.getReference(user.getUid()).child("ListNote");
                                    reference.child(String.valueOf(note.getId())).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(getApplicationContext(), "Note is Deleted on Firebase", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            new MoveNote_to_RecycleBin().execute();
                        }

                    }
            );
        } else {
            layoutOption.findViewById(R.id.deletenote).setVisibility(View.GONE);
        }
    }

    private void addVideo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_ADD_VIDEO);
    }


    private void addImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_ADD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_IMG && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imgisSelected = data.getData();
                if (imgisSelected != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imgisSelected);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgtxt.setImageBitmap(bitmap);
                        imgtxt.setVisibility(View.VISIBLE);
                        selectedImagePath = getPath(imgisSelected);
                        findViewById(R.id.btnRemoveImg).setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if (requestCode == REQUEST_ADD_VIDEO && resultCode == RESULT_OK) {
            if (data != null) {
                Uri Videoselected = data.getData();
                Video_Path = getPath(Videoselected);
                DisplayVideo(Video_Path);
            }
        }
        if(requestCode == REQUEST_ADDLABELS && resultCode == RESULT_OK)
        {
            if (data != null) {
               label =  data.getStringExtra("ResultLabel");
               DisplayLabel(label);
            }
        }
    }


    private void DisplayLabel(String label) {
        String[] words = label.split(",");
        layoutdisplaylabels.removeAllViews();
        for(String s : words)
        {
            TextView textView = new TextView(this);
            textView.setText(s);
            textView.setTextSize(15);
            textView.setBackgroundColor(Color.parseColor("#FF000000"));
            textView.setTextColor(Color.parseColor("#FFFFFFFF"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5,5,5,5);
            textView.setLayoutParams(params);
            textView.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
            layoutdisplaylabels.addView(textView);
        }
    }

    private void DisplayVideo(String path) {
        videostxt.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        videostxt.setVisibility(View.VISIBLE);
        videostxt.setMediaController(mediaController);
        mediaController.setAnchorView(videostxt);
        findViewById(R.id.btnRemoveVideo).setVisibility(View.VISIBLE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addImage();
            } else {
                Toast.makeText(this, "Can't not access Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void CreateNote() {
        if (title_txt.getText().toString().trim().isEmpty() && contenttxt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please type something in your new note", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(title_txt.getText().toString().trim());
        note.setSubtitle(subtitletxt.getText().toString().trim());
        note.setSubtitle(subtitletxt.getText().toString().trim());
        note.setContent(contenttxt.getText().toString().trim());
        note.setDatetime(datetimetxt.getText().toString().trim());
        note.setAlarmTime(TIME_ALARM.trim());
        note.setDatealarm(DATE_ALARM.trim());
        note.setColor(currentcolor.trim());
        note.setVideo(Video_Path.trim());
        note.setSound(Sound_Path.trim());
        note.setLabels(label.trim());

        note.setImage(selectedImagePath);
        note.setPassword(PASSWORD_FOR_NOTE);
        if (!selectedImagePath.isEmpty()) {
            note.setImage(selectedImagePath);
        }
        if (note_item_to_view_or_update != null) {
            note.setId(note_item_to_view_or_update.getId());
        }

        //Save note
        @SuppressLint("StaticFieldLeak")
        class CreateTASK extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseNote.getInstanceDTB(getApplicationContext()).noteDAO().createNote(note);
                Log.e("CreateNote", "success");

                //UpdatetoFireBase

                return null;
            }

            @Override
            protected void onPostExecute(Void avoid) {
                super.onPostExecute(avoid);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new CreateTASK().execute();
    }

    private String getPath(Uri uri) {
        String path;
        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null, null);
        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            path = cursor.getString(index);
            cursor.close();
        }
        return path;
    }


}