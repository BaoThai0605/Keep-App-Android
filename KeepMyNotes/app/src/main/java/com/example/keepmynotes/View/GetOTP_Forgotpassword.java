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
import android.widget.Toast;

import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class GetOTP_Forgotpassword extends AppCompatActivity {
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_get_otp_forgotpassword);
        database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
        initView();
    }

    private void initView() {
        final EditText phone_fgpw = findViewById(R.id.phone_fgpw);
        final Button btngetOTP = findViewById(R.id.btngetOTP);

        btngetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phone_fgpw.getText().toString().trim();
                DatabaseReference userAccount = database.getReference(phone).child("UserAccount");
                userAccount.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user == null) {
                            Toast.makeText(GetOTP_Forgotpassword.this, "Account is not exist", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            PhoneAuthProvider
                                    .getInstance()
                                    .verifyPhoneNumber(
                                            "+84" + phone,
                                            60,
                                            TimeUnit.SECONDS,
                                            GetOTP_Forgotpassword.this,
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
                                                    Intent i = new Intent(GetOTP_Forgotpassword.this, Vertify_by_OTP.class);
                                                    i.putExtra("phone", phone);
                                                    i.putExtra("vertificationID", vertificationID);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }
                                    );
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Fetch User", error.getMessage());
                    }
                });

            }
        });
    }
}