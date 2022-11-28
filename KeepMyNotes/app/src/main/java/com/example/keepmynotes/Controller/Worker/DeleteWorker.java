package com.example.keepmynotes.Controller.Worker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseRecycleBin;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.Note_Deleted;
import com.example.keepmynotes.Model.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.annotation.Annotation;

public class DeleteWorker extends Worker {

    public DeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        DeleteItemAfterTime();
        return Result.success();
    }

    private void DeleteItemAfterTime() {
        int ID_note = getInputData().getInt("ID_Note", 0);
        String Uid = getInputData().getString("Uid");
        User user = DatabaseUser.getInstance(getApplicationContext()).userDAO().getCurrentUser();

        if (ID_note != 0) {
            if (user != null) {
                Note_Deleted note = DatabaseRecycleBin.getInstanceDTB(getApplicationContext()).recycleBinDAO().getNoteDeletebyId(ID_note);
                DatabaseRecycleBin.getInstanceDTB(getApplicationContext()).recycleBinDAO().deleteBin(note);
            }


            FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
            DatabaseReference reference = database.getReference(Uid).child("RecycleBin");

            reference.child(String.valueOf(ID_note)).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(getApplicationContext(), "Note is Deleted on Firebase", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("Success", "Note is Deleted");
        } else {
            Log.e("Null", "Cant not find note");
        }
    }
}
