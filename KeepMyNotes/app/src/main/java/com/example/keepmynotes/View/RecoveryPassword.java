package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecoveryPassword extends AppCompatActivity {
    private EditText newpass_fgpw,cfnewpass_fgpw;
    private Button btnChangePass_fgpw;
    private String p2,p3,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_recovery_password);
        phone = getIntent().getStringExtra("phone");
        initView();
        initEnvent();
        
    }

    private void initEnvent() {

        btnChangePass_fgpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2 = newpass_fgpw.getText().toString().trim();
                p3 = cfnewpass_fgpw.getText().toString().trim();
                if(checkValidation())
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                    DatabaseReference reference = database.getReference(phone).child("UserAccount").child("password");
                    reference.setValue(p2, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getApplicationContext(), "Change password success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RecoveryPassword.this,LoginForm.class));
                            finish();

                        }
                    });




                }
            }
        });
    }

    private void initView() {
        newpass_fgpw = findViewById(R.id.newpass_fgpw);
        cfnewpass_fgpw = findViewById(R.id.cfnewpass_fgpw);
        btnChangePass_fgpw = findViewById(R.id.btnChangePass_fgpw);
    }

    private boolean checkValidation()
    {
        if(p2.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "New password not null", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p3.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Confirm password not null", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p2.length()<6)
        {
            Toast.makeText(getApplicationContext(), "New  Password at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p3.length()<6)
        {
            Toast.makeText(getApplicationContext(), "Confirm Password at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!p2.equals(p3))
        {
            Toast.makeText(getApplicationContext(), "New password and confirm password is not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}