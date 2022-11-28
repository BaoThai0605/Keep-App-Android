package com.example.keepmynotes.View;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.keepmynotes.Model.Database.DatabaseSetting;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.SettingNotes;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class Setting_Fragment extends Fragment {
    private EditText textsizetxt,timedeletetxt;
    private RadioButton R1,R2,R3,df_font,mns_font,cs_font;
    private SettingNotes settingNotes;
    private TextView tv_size,tv_font;
    private Button Upsize,Downsize,btnSaveSetting;
    private String currentFont;
    private int MIN_TEXT_SIZE = 15;
    private int MAX_TEXT_SIZE = 25;
    private int currentsize,currentTime;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingNotes = DatabaseSetting.getInstance(getActivity()).settingDAO().getCurrentSetting();
        currentsize = settingNotes.getTextsize();
        currentTime = settingNotes.getTimedelete();
        currentFont = settingNotes.getFontText();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textsizetxt = view.findViewById(R.id.textsizetxt);
        timedeletetxt = view.findViewById(R.id.timedeletetxt);
        R1 = view.findViewById(R.id.R1);
        R2 = view.findViewById(R.id.R2);
        R3 = view.findViewById(R.id.R3);
        df_font = view.findViewById(R.id.df_font);
        mns_font = view.findViewById(R.id.mns_font);
        cs_font = view.findViewById(R.id.cs_font);
        tv_size = view.findViewById(R.id.tv_size);
        tv_font = view.findViewById(R.id.tv_font);


        SetSizeChange();
        SetFontChange();

        Upsize = view.findViewById(R.id.Upsize);
        Downsize = view.findViewById(R.id.Downsize);
        btnSaveSetting = view.findViewById(R.id.btnSaveSetting);


        if(settingNotes!=null)
        {
            textsizetxt.setText(String.valueOf(currentsize));
            timedeletetxt.setText(String.valueOf(currentTime));
            switch (settingNotes.getTimeUnit())
            {
                case "SECONDS":
                    R1.setChecked(true);
                    R2.setChecked(false);
                    R3.setChecked(false);
                    break;
                case "HOUR":
                    R1.setChecked(false);
                    R2.setChecked(true);
                    R3.setChecked(false);
                    break;
                case "DAYS":
                    R1.setChecked(false);
                    R2.setChecked(false);
                    R3.setChecked(true);
                    break;
            }

            switch (currentFont)
            {
                case "sans-serif":
                    df_font.setChecked(true);
                    mns_font.setChecked(false);
                    cs_font.setChecked(false);
                    break;
                case "monospace":
                    df_font.setChecked(false);
                    mns_font.setChecked(true);
                    cs_font.setChecked(false);
                    break;
                case "casual":
                    df_font.setChecked(false);
                    mns_font.setChecked(false);
                    cs_font.setChecked(true);
                    break;
            }

            df_font.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentFont = "sans-serif";
                    SetFontChange();
                }
            });

            mns_font.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentFont = "monospace";
                    SetFontChange();
                }
            });

            cs_font.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentFont = "casual";
                    SetFontChange();
                }
            });


            Upsize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentsize = currentsize +1;
                    if(currentsize>MAX_TEXT_SIZE)
                    {
                        currentsize= MAX_TEXT_SIZE;
                    }
                    textsizetxt.setText(String.valueOf(currentsize));
                    SetSizeChange();
                }
            });
            Downsize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentsize = currentsize -1;
                    if(currentsize<MIN_TEXT_SIZE)
                    {
                        currentsize= MIN_TEXT_SIZE;
                    }
                    textsizetxt.setText(String.valueOf(currentsize));
                    SetSizeChange();
                }
            });


            timedeletetxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(timedeletetxt.getText().toString().isEmpty())
                    {
                        timedeletetxt.setText("20");
                    }
                }
            });


            btnSaveSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    settingNotes.setTextsize(currentsize);
                    settingNotes.setTimedelete(Integer.parseInt(timedeletetxt.getText().toString()));
                    if(R1.isChecked())
                    {
                        settingNotes.setTimeUnit("SECONDS");
                    }
                    else if(R2.isChecked())
                    {
                        settingNotes.setTimeUnit("HOUR");
                    }
                    else if(R3.isChecked())
                    {
                        settingNotes.setTimeUnit("DAYS");
                    }
                    settingNotes.setFontText(currentFont);
                    User user = DatabaseUser.getInstance(getActivity()).userDAO().getCurrentUser();
                    DatabaseSetting.getInstance(getActivity()).settingDAO().InsertSetting(settingNotes);


                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                    DatabaseReference reference = database.getReference(user.getUid()).child("SettingNote");
                    reference.setValue(settingNotes, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(), "Config Success", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });


        }


    }

    private void SetFontChange() {
        tv_font.setTypeface(Typeface.create(currentFont, Typeface.NORMAL));
    }

    private void SetSizeChange() {
        tv_size.setTextSize(currentsize);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment, container, false);
    }
}
