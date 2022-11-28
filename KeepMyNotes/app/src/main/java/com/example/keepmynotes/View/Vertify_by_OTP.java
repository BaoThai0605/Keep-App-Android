package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepmynotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Vertify_by_OTP extends AppCompatActivity {
    private String phone, vertificationID;
    private TextView ResendOTP_fgpw;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private EditText OTP_code_fgpw;
    private Button btnVtf_fgpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vertify_by_otp);
        btnVtf_fgpw = findViewById(R.id.btnVtf_fgpw);
        OTP_code_fgpw = findViewById(R.id.OTP_code_fgpw);
        ResendOTP_fgpw = findViewById(R.id.ResendOTP_fgpw);
        mAuth = FirebaseAuth.getInstance();

        initUser();
        initEvent();
    }

    private void initEvent() {
        ResendOTP_fgpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider
                        .getInstance()
                        .verifyPhoneNumber(
                                "+84" + phone,
                                60,
                                TimeUnit.SECONDS,
                                Vertify_by_OTP.this,
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


        btnVtf_fgpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTP = OTP_code_fgpw.getText().toString().trim();
                if (OTP.length() < 6) {
                    Toast.makeText(Vertify_by_OTP.this, "Please Enter Valid code", Toast.LENGTH_SHORT).show();
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
                                    Intent i = new Intent(Vertify_by_OTP.this, RecoveryPassword.class);
                                    i.putExtra("phone", phone);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Vertify_by_OTP.this, "Vertify success redirect to ChangePassword", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Vertify_by_OTP.this, "Null", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Vertify_by_OTP.this, "OTP code is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initUser() {
        phone = getIntent().getStringExtra("phone");
        vertificationID = getIntent().getStringExtra("vertificationID");
    }
}