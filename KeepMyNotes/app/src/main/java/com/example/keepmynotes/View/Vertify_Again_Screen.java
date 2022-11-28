package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class Vertify_Again_Screen extends AppCompatActivity {

    private Button btnReVtf,btnBackHome;
    private EditText OTP_code_revtf;
    private String phone, vertificationID;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vertify_again_screen);
        user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        initData();
        initView();
        initReSentOTP();
        initEvent();
    }

    private void initData() {
        phone = user.getEmail();


    }

    private void initEvent() {
        btnReVtf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = OTP_code_revtf.getText().toString().trim();
                if (otp.length() < 6) {
                    Toast.makeText(Vertify_Again_Screen.this, "Please Enter Valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (vertificationID != null) {
                    PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider
                            .getCredential(
                            vertificationID,
                                    otp);
                    mAuth.signInWithCredential(phoneAuthProvider).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mUser = mAuth.getCurrentUser();
                                if (mUser != null) {
                                    ChangeData_User_Firebase_RoomDatabase();
                                    Intent i = new Intent(Vertify_Again_Screen.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(Vertify_Again_Screen.this, "Null", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Vertify_Again_Screen.this, "OTP code is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    private void ChangeData_User_Firebase_RoomDatabase() {
        user.setVertify(true);
        DatabaseUser.getInstance(getApplicationContext()).userDAO().insertUser(user);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRF = database.getReference(user.getUid()).child("UserAccount");
        userRF.setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(Vertify_Again_Screen.this, "Account is verity", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void initView() {
        btnReVtf = findViewById(R.id.btnReVtf);
        btnBackHome = findViewById(R.id.btnBackHome);
        OTP_code_revtf = findViewById(R.id.OTP_code_revtf);
    }

    private void initReSentOTP() {
        PhoneAuthProvider
                .getInstance()
                .verifyPhoneNumber(
                        "+84" + phone,
                        60,
                        TimeUnit.SECONDS,
                        Vertify_Again_Screen.this,
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
}