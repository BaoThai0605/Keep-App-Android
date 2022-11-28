package com.example.keepmynotes.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePass_Fragment extends Fragment {

    private EditText oldpass,newpass,cfnewpass;
    private Button btnChangePass;
    private String p1,p2,p3,p4;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = DatabaseUser.getInstance(getActivity()).userDAO().getCurrentUser();
        if(user!=null)
        {
            p4 = user.getPassword();
        }
        else
        {
            p4 = "";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.changepass_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oldpass = view.findViewById(R.id.oldpass);
        newpass = view.findViewById(R.id.newpass);
        cfnewpass = view.findViewById(R.id.cfnewpass);
        btnChangePass = view.findViewById(R.id.btnChangePass);

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p1 = oldpass.getText().toString().trim();
                p2 = newpass.getText().toString().trim();
                p3 = cfnewpass.getText().toString().trim();
                if(checkValidation())
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                    DatabaseReference reference = database.getReference(user.getUid()).child("UserAccount");
                    user.setPassword(p2);
                    reference.setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(getActivity(), "Change Password success", Toast.LENGTH_SHORT).show();
                            oldpass.setText("");
                            newpass.setText("");
                            cfnewpass.setText("");
                        }
                    });
                }
            }
        });
    }

    private boolean checkValidation()
    {
        if(p1.isEmpty())
        {
            Toast.makeText(getActivity(), "Old password not null", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p2.isEmpty())
        {
            Toast.makeText(getActivity(), "New password not null", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p3.isEmpty())
        {
            Toast.makeText(getActivity(), "Confirm password not null", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p1.length()<6)
        {
            Toast.makeText(getActivity(), "Old Password at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p2.length()<6)
        {
            Toast.makeText(getActivity(), "New  Password at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p3.length()<6)
        {
            Toast.makeText(getActivity(), "Confirm Password at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!p1.equals(p4))
        {
            Toast.makeText(getActivity(), "Old password is not correct", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!p2.equals(p3))
        {
            Toast.makeText(getActivity(), "New password and confirm password is not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p1.equals(p2))
        {
            Toast.makeText(getActivity(), "Oldpassword and NewPassword must different", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
