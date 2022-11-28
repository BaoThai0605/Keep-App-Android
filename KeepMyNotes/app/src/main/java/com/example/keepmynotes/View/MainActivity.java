package com.example.keepmynotes.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.keepmynotes.Model.Database.DatabaseLabels;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseRecycleBin;
import com.example.keepmynotes.Model.Database.DatabaseSetting;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerlayout;
    private TextView Email, vtf;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initView();
        initUser();
    }

    private void initUser() {

        User user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();
        if (user != null) {
            Email.setText(user.getName());
            if (user.getVertify()) {
                vtf.setText("Account is vertified");
            } else {
                vtf.setText("Account is not vertified");

            }
        }
    }

    private void initView() {
        navigationView = findViewById(R.id.NavView);
        View v = navigationView.getHeaderView(0);
        Email = v.findViewById(R.id.Email);
        vtf = v.findViewById(R.id.vtf);


        mDrawerlayout = findViewById(R.id.drawerlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Add toobar

        //Set đóng mở Nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerlayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);
        //add event
        mDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.NavView);
        navigationView.setNavigationItemSelectedListener(this);

        ChangeFragments(new Notes_Fragment());
        navigationView.getMenu().findItem(R.id.nav_notes).setChecked(true);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_notes:
                ChangeFragments(new Notes_Fragment());
                mDrawerlayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_bin:
                ChangeFragments(new RecycleBin_Fragment());
                mDrawerlayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_label:
                ChangeFragments(new Label_Fragment());
                mDrawerlayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_setting:
                ChangeFragments(new Setting_Fragment());
                mDrawerlayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_changepassword:
                ChangeFragments(new ChangePass_Fragment());
                mDrawerlayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_logout:
                LogOut();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerlayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void LogOut()
    {
        DatabaseSetting.getInstance(getApplicationContext()).settingDAO().DeleteSetting();
        DatabaseNote.getInstanceDTB(getApplicationContext()).noteDAO().DeleteAll();
        DatabaseUser.getInstance(getApplicationContext()).userDAO().DeleteUser();
        DatabaseLabels.getInstance(getApplicationContext()).labelsDAO().DeleteAllLabel();
        DatabaseRecycleBin.getInstanceDTB(getApplicationContext()).recycleBinDAO().DeleteAllRecycle();
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginForm.class));
    }

    private void ChangeFragments(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragments, fragment);
        transaction.commit();
    }
}