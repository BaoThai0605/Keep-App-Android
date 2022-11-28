package com.example.keepmynotes.View;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepmynotes.Model.Database.DatabaseLabels;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseRecycleBin;
import com.example.keepmynotes.Model.Database.DatabaseSetting;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.Labels;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.Note_Deleted;
import com.example.keepmynotes.Model.SettingNotes;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginForm extends AppCompatActivity {

    private EditText Emailtxt,Passwordtxt;
    private Button btnLogin,btnSignUp;
    private TextView forgotpassword;
    private FirebaseDatabase database;
    private String email;
    private  String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_form);
        database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        checkLogged();
        initView();
        initListener();

    }

    private void checkLogged() {
        User user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();
        if(user!=null)
        {
            startActivity(new Intent(LoginForm.this,MainActivity.class));
            finish();
        }
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 email= Emailtxt.getText().toString().trim();
                 password = Passwordtxt.getText().toString().trim();
                if(email.isEmpty())
                {
                    Toast.makeText(LoginForm.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.isEmpty())
                {
                    Toast.makeText(LoginForm.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference userAccount = database.getReference(email).child("UserAccount");
                userAccount.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user==null)
                        {
                            Toast.makeText(LoginForm.this, "Account is not exist", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            if(password.equals(user.getPassword().trim()))
                            {
                                Toast.makeText(LoginForm.this, "Welcom to note app", Toast.LENGTH_SHORT).show();
                                DatabaseUser.getInstance(getApplicationContext()).userDAO().insertUser(user);
                                fetchDataFromFireBase();
                                startActivity(new Intent(LoginForm.this,MainActivity.class));
                                finish();

                            }
                            else
                            {
                                Toast.makeText(LoginForm.this, "Password is not correct", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Fetch User",error.getMessage());
                    }
                });


            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginForm.this,RegisterForm.class));
                finish();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginForm.this,GetOTP_Forgotpassword.class));

            }
        });
    }

    private void fetchDataFromFireBase() {

        DatabaseReference reference = database.getReference(email).child("ListNote");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Note note = dataSnapshot.getValue(Note.class);
                    DatabaseNote.getInstanceDTB(getApplicationContext()).noteDAO().createNote(note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference rcl = database.getReference(email).child("RecycleBin");
        rcl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Note_Deleted note_delted = dataSnapshot.getValue(Note_Deleted.class);
                    DatabaseRecycleBin.getInstanceDTB(getApplicationContext()).recycleBinDAO().InsertBin(note_delted);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });


        DatabaseReference labels = database.getReference(email).child("ListLabel");
        labels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Labels labels1 =  dataSnapshot.getValue(Labels.class);
                    DatabaseLabels.getInstance(getApplicationContext()).labelsDAO().InsertLabel(labels1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference reference1 = database.getReference(email).child("SettingNote");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SettingNotes settingNotes = snapshot.getValue(SettingNotes.class);
                DatabaseSetting.getInstance(getApplicationContext()).settingDAO().InsertSetting(settingNotes);
                Log.e("Fetch Setting","Success");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Fetch Setting",error.getMessage());
            }
        });

    }


    private void initView() {
        Emailtxt = findViewById(R.id.Emailtxt);
        Passwordtxt = findViewById(R.id.Passwordtxt);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        forgotpassword = findViewById(R.id.forgotpassword);
    }


}