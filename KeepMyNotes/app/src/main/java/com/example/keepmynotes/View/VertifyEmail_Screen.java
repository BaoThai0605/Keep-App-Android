package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepmynotes.Model.Database.DatabaseSetting;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.SettingNotes;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VertifyEmail_Screen extends AppCompatActivity {

    private String email, password, name, vertificationID;
    private TextView btnSkip, ResendOTP;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private EditText OTP_code;
    private Button btnVtf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vertify_email_screen);
        btnSkip = findViewById(R.id.btnSkip);
        btnVtf = findViewById(R.id.btnVtf);
        OTP_code = findViewById(R.id.OTP_code);
        ResendOTP = findViewById(R.id.ResendOTP);
        mAuth = FirebaseAuth.getInstance();

        initUser();
        initEvent();
    }

    private void initEvent() {

        btnVtf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTP = OTP_code.getText().toString().trim();
                if (OTP.length() < 6) {
                    Toast.makeText(VertifyEmail_Screen.this, "Please Enter Valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (vertificationID != null) {
                    PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider.getCredential(
                            vertificationID, OTP);
                    mAuth.signInWithCredential(phoneAuthProvider).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mUser = mAuth.getCurrentUser();
                                if (mUser != null) {
                                    createUserAndDefaultSetting(email, true);
                                    FetchDataFromFirebase(email);
                                    Intent i = new Intent(VertifyEmail_Screen.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(VertifyEmail_Screen.this, "Null", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(VertifyEmail_Screen.this, "OTP code is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAndDefaultSetting(email, false);
                FetchDataFromFirebase(email);
                Intent i = new Intent(VertifyEmail_Screen.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }

        });

        ResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider
                        .getInstance()
                        .verifyPhoneNumber(
                                "+84" + email,
                                60,
                                TimeUnit.SECONDS,
                                VertifyEmail_Screen.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String newvertificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        vertificationID = newvertificationID;
                                    }
                                });

            }
        });
    }

    private void createUserAndDefaultSetting(String uid, boolean b) {
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setUid(uid);
        user.setVertify(b);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRF = database.getReference(uid).child("UserAccount");
        userRF.setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(VertifyEmail_Screen.this, "User is Created in firebase", Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference settingRF = database.getReference(uid).child("SettingNote");
        settingRF.setValue(SettingNotes.getDefaultSetting(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(VertifyEmail_Screen.this, "Setting Config is created in firebase", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initUser() {
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");
        vertificationID = getIntent().getStringExtra("vertificationID");
    }

    private void FetchDataFromFirebase(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reference = database.getReference(uid).child("UserAccount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                DatabaseUser.getInstance(getApplicationContext()).userDAO().insertUser(user);
                Log.e("Fetch User", "Success");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Fetch User", error.getMessage());
            }
        });

        DatabaseReference reference1 = database.getReference(uid).child("SettingNote");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SettingNotes settingNotes = snapshot.getValue(SettingNotes.class);
                DatabaseSetting.getInstance(getApplicationContext()).settingDAO().InsertSetting(settingNotes);
                Log.e("Fetch Setting", "Success");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Fetch Setting", error.getMessage());
            }
        });
    }

}