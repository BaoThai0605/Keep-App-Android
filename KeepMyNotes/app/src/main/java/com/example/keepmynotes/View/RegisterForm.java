
package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.keepmynotes.Model.SettingNotes;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

public class RegisterForm extends AppCompatActivity {
    private EditText phone_rgt, pass_rgt, cpass_rgt, name_rgt;
    private Button btnRgt, backtoLogin;
    private String email, password, name;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_form);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        initView();
        initListener();
    }

    private void initListener() {
        btnRgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    email = phone_rgt.getText().toString().trim();
                    password = pass_rgt.getText().toString().trim();
                    name = name_rgt.getText().toString().trim();
                    DatabaseReference userAccount = database.getReference(email).child("UserAccount");
                    userAccount.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user.getUid() != email) {
                                PhoneAuthProvider
                                        .getInstance()
                                        .verifyPhoneNumber(
                                                "+84" + email,
                                                60,
                                                TimeUnit.SECONDS,
                                                RegisterForm.this,
                                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                    @Override
                                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                                    }

                                                    @Override
                                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onCodeSent(@NonNull String vertificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                        super.onCodeSent(vertificationID, forceResendingToken);
                                                        finish();
                                                        Intent i = new Intent(RegisterForm.this, VertifyEmail_Screen.class);
                                                        i.putExtra("email", email);
                                                        i.putExtra("password", password);
                                                        i.putExtra("name", name);
                                                        i.putExtra("vertificationID", vertificationID);
                                                        startActivity(i);

                                                    }
                                                }
                                        );

                            }
                            else {
                                Toast.makeText(RegisterForm.this, "Account is already exist, please login", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        backtoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterForm.this,LoginForm.class));
                finish();
            }
        });

    }


    private boolean checkValidation() {
        if (name_rgt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone_rgt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Enter the Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass_rgt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass_rgt.getText().toString().trim().length() < 6) {
            Toast.makeText(this, "Password is at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cpass_rgt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Enter the Confirm Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cpass_rgt.getText().toString().trim().length() < 6) {
            Toast.makeText(this, "Password is at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pass_rgt.getText().toString().trim().equals(cpass_rgt.getText().toString().trim())) {
            Toast.makeText(this, "Password and Confirm password is not match", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void initView() {
        phone_rgt = findViewById(R.id.phone_rgt);
        pass_rgt = findViewById(R.id.pass_rgt);
        cpass_rgt = findViewById(R.id.cpass_rgt);
        btnRgt = findViewById(R.id.btnRgt);
        backtoLogin = findViewById(R.id.backtoLogin);
        name_rgt = findViewById(R.id.name_rgt);
    }
}