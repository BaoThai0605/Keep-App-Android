package com.example.keepmynotes.View;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.WorkManager;

import com.example.keepmynotes.Model.Database.DatabaseLabels;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.View.CustomAdapter.RecycleApdapterRecycleBin;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseRecycleBin;
import com.example.keepmynotes.Controller.EventListener.NoteDelete_Event_Listener;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.Note_Deleted;
import com.example.keepmynotes.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecycleBin_Fragment extends Fragment implements NoteDelete_Event_Listener {
    private List<Note_Deleted> mlist;
    private RecyclerView RecycleBin_RecycleView;
    private RecycleApdapterRecycleBin mAdapter;
    private LinearLayout BinNull;
    private static final int REQUEST_VIEW_OR_UPDATE = 2;
    private static final int REQUEST_VIEW_REFRESHNOTES = 3;
    private int noteClicked = -1;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlist = new ArrayList<>();
        getAllNotesinBin(REQUEST_VIEW_REFRESHNOTES);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycle_bin_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecycleBin_RecycleView = view.findViewById(R.id.RecycleBin_RecycleView);
        BinNull = view.findViewById(R.id.BinNull);

        RecycleBin_RecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new RecycleApdapterRecycleBin(mlist,this);
        RecycleBin_RecycleView.setAdapter(mAdapter);
    }

    private void getAllNotesinBin(int code) {
        class GetAllTaskRecycle extends AsyncTask<Void, Void, List<Note_Deleted>> {

            @Override
            protected List<Note_Deleted> doInBackground(Void... voids) {
                return DatabaseRecycleBin
                        .getInstanceDTB(getActivity())
                        .recycleBinDAO()
                        .getAllRecycleBin();
            }

            @Override
            protected void onPostExecute(List<Note_Deleted> note_deleteds) {
                super.onPostExecute(note_deleteds);
                Log.e("List Notes", note_deleteds.toString());
                switch (code) {
                    case REQUEST_VIEW_REFRESHNOTES:
                        mlist.addAll(note_deleteds);
                        mAdapter.notifyDataSetChanged();
                        CheckListisEmty();
                        break;
                }
            }

        }

        new GetAllTaskRecycle().execute();
    }

    private void CheckListisEmty() {
        if(mlist.size()==0)
        {
            if(BinNull.getVisibility()==View.GONE)
            {
                BinNull.setVisibility(View.VISIBLE);
            }
            if(RecycleBin_RecycleView.getVisibility()==View.VISIBLE)
            {
                RecycleBin_RecycleView.setVisibility(View.GONE);
            }
        }
        else
        {
            if(RecycleBin_RecycleView.getVisibility()==View.GONE)
            {
                RecycleBin_RecycleView.setVisibility(View.VISIBLE);
            }
            if(BinNull.getVisibility()==View.VISIBLE)
            {
                BinNull.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void OnRestore(Note_Deleted note, int pos) {

        class RestoreTask extends AsyncTask<Void,Void,Void>
        {

            Note restore = (Note)note;
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseRecycleBin
                        .getInstanceDTB(getActivity())
                        .recycleBinDAO()
                        .deleteBin(note);

                DatabaseNote
                        .getInstanceDTB(getActivity())
                        .noteDAO()
                        .createNote(restore);

                WorkManager.getInstance(getActivity()).cancelAllWorkByTag(String.valueOf(note.getId()));
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                mlist.remove(pos);
                mAdapter.notifyItemRemoved(pos);
                Toast.makeText(getActivity(), "Note is restored", Toast.LENGTH_SHORT).show();

            }
        }
        new RestoreTask().execute();



    }

    @Override
    public void OnDelete(Note_Deleted note, int pos) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.dialog_delete_confirm, null);
        final Button btnCanel = view.findViewById(R.id.btnCanel);
        final Button btnOK = view.findViewById(R.id.btnOK);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                class DeleteTask extends AsyncTask<Void,Void,Void>
                {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        User user = DatabaseUser.getInstance(getActivity()).userDAO().getCurrentUser();
                        DatabaseRecycleBin
                                .getInstanceDTB(getActivity())
                                .recycleBinDAO()
                                .deleteBin(note);
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");
                        DatabaseReference reference = database.getReference(user.getUid()).child("RecycleBin");

                        reference.child(String.valueOf(note.getId())).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getActivity(), "Note is Deleted on Firebase", Toast.LENGTH_SHORT).show();
                            }
                        });

                        WorkManager.getInstance(getActivity()).cancelAllWorkByTag(String.valueOf(note.getId()));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void unused) {
                        super.onPostExecute(unused);
                        mlist.remove(pos);
                        mAdapter.notifyItemRemoved(pos);
                        Toast.makeText(getActivity(), "Note is Deleted", Toast.LENGTH_SHORT).show();

                    }
                }
                new DeleteTask().execute();

            }
        });
        alertDialog.show();





    }
}
