package com.example.keepmynotes.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.keepmynotes.Controller.EventListener.Label_Listener;
import com.example.keepmynotes.Model.Database.DatabaseLabels;
import com.example.keepmynotes.Model.Database.DatabaseNote;
import com.example.keepmynotes.Model.Database.DatabaseUser;
import com.example.keepmynotes.Model.Labels;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.User;
import com.example.keepmynotes.R;
import com.example.keepmynotes.View.CustomAdapter.RecycleApdapterLabels;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Label_Fragment extends Fragment implements Label_Listener {

    private List<Labels> mlist;
    private RecycleApdapterLabels mAdapter;
    private RecyclerView recyclerView;
    private TextView nulllabel;


    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_VIEW_OR_UPDATE = 2;
    private static final int REQUEST_VIEW_REFRESHNOTES = 3;
    private static final int REQUEST_VIEW_DELETED = 4;
    private int noteClicked = -1;
    private User usercurrent;
    private FirebaseDatabase database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlist = new ArrayList<>();
        usercurrent = DatabaseUser.getInstance(getActivity()).userDAO().getCurrentUser();
        database = FirebaseDatabase.getInstance("https://noteappbachay-default-rtdb.asia-southeast1.firebasedatabase.app");

        getAllLabels(REQUEST_VIEW_REFRESHNOTES);
        Log.e("List Size", String.valueOf(mlist.size()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.label_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.Rcv_label);
        nulllabel = view.findViewById(R.id.nulllabel);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new RecycleApdapterLabels(mlist, this);
        recyclerView.setAdapter(mAdapter);

        view.findViewById(R.id.btnAddLabel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final View view = getLayoutInflater().inflate(R.layout.diagloc_add_label, null);
                final EditText nameCreatelabel = view.findViewById(R.id.nameCreatelabel);
                final Button cancel_create_label = view.findViewById(R.id.cancel_create_label);
                final Button ok_create_label = view.findViewById(R.id.ok_create_label);

                alert.setView(view);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(true);

                cancel_create_label.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                ok_create_label.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!nameCreatelabel.getText().toString().trim().isEmpty()) {
                            alertDialog.dismiss();
                            String name = nameCreatelabel.getText().toString().trim();
                            if (CheckValidLabel(name)) {
                                Labels newLabel = new Labels();
                                newLabel.setNamelabels(name);
                                newLabel.setCheckLabel(false);
                                DatabaseLabels.getInstance(getActivity()).labelsDAO().InsertLabel(newLabel);
                                getAllLabels(REQUEST_ADD);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please set the new Name for label", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();

            }
        });
    }

    private boolean CheckValidLabel(String name) {
        List<Labels> list = DatabaseLabels.getInstance(getActivity()).labelsDAO().getAllLabels();
        for (Labels s : list) {
            if (s.getNamelabels().equals(name)) {
                Toast.makeText(getActivity(), "Label name is already exist", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void getAllLabels(int request) {
        class getAllLabelsTask extends AsyncTask<Void, Void, List<Labels>> {
            @Override
            protected List<Labels> doInBackground(Void... voids) {
                return DatabaseLabels
                        .getInstance(getActivity())
                        .labelsDAO()
                        .getAllLabels();
            }

            @Override
            protected void onPostExecute(List<Labels> labels) {
                super.onPostExecute(labels);
                switch (request) {
                    case REQUEST_ADD:
                        mlist.add(0, labels.get(0));
                        PushDataToFireBase(labels.get(0));
                        mAdapter.notifyItemInserted(0);
                        recyclerView.smoothScrollToPosition(0);
                        CheckListisEmty();
                        break;
                    case REQUEST_VIEW_REFRESHNOTES:
                        mlist.addAll(labels);
                        mAdapter.notifyDataSetChanged();
                        CheckListisEmty();
                        break;
                    case REQUEST_VIEW_DELETED:
                        mlist.remove(noteClicked);
                        mAdapter.notifyItemRemoved(noteClicked);

                        CheckListisEmty();
                        break;
                    case REQUEST_VIEW_OR_UPDATE:
                        mlist.remove(noteClicked);
                        mlist.add(noteClicked, labels.get(noteClicked));
                        UpdateLabelFirebase(labels.get(noteClicked));
                        mAdapter.notifyItemChanged(noteClicked);
                        break;
                }
            }
        }
        new getAllLabelsTask().execute();

    }

    private void UpdateLabelFirebase(Labels labels) {
        DatabaseReference reference = database.getReference(usercurrent.getUid()).child("ListLabel");
        reference.child(String.valueOf(labels.getId_labels())).updateChildren(labels.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getActivity(), "Update success", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void OnLongClick(Labels labels, int pos) {
        noteClicked = pos;
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.diagloc_edit_label, null);
        final EditText nameEditlabel = (EditText) view.findViewById(R.id.nameEditlabel);
        nameEditlabel.setText(labels.getNamelabels());
        final Button cancel_Edit_label = view.findViewById(R.id.cancel_Edit_label);
        final Button ok_Edit_label = view.findViewById(R.id.ok_Edit_label);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        cancel_Edit_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ok_Edit_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameEditlabel.getText().toString().trim().isEmpty()) {
                    alertDialog.dismiss();
                    String name = nameEditlabel.getText().toString().trim();
                    if (CheckValidLabel(name)) {
                        labels.setNamelabels(name);
                        DatabaseLabels.getInstance(getActivity()).labelsDAO().InsertLabel(labels);
                        getAllLabels(REQUEST_VIEW_OR_UPDATE);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please set the new Name for label", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

            }
        });
        alertDialog.show();

    }

    @Override
    public void OnClicktoDelete(Labels labels, int pos) {

        noteClicked = pos;
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
                DatabaseLabels.getInstance(getActivity()).labelsDAO().DeleteLabel(labels);
                RemoveLabelinFirebase(labels);
                getAllLabels(REQUEST_VIEW_DELETED);
            }
        });
        alertDialog.show();

    }

    @Override
    public void OnClickViewLabel(Labels labels, int pos) {
        noteClicked = pos;
        Intent i = new Intent(getActivity(),ViewByLabel_Screen.class);
        i.putExtra("labelquery", labels.getNamelabels());
        startActivity(i);
    }

    private void RemoveLabelinFirebase(Labels labels) {
        String key = labels.getNamelabels().trim();
        List<Note> list_to_update = DatabaseNote.getInstanceDTB(getActivity()).noteDAO().getAll();
        for (int i = 0; i < list_to_update.size(); i++) {//New Query label;
            String newLabel = "";
            Note note_temp = list_to_update.get(i); // Note temp
            String[] arr = note_temp.getLabels().trim().split(","); //Change String to array
            if(note_temp.getLabels().contains(key) && !note_temp.getLabels().isEmpty())
            {
                for(String s : arr)
                {
                    if(!s.trim().equals(key))
                    {
                        newLabel = newLabel+s+",";
                    }
                }
                if(!newLabel.isEmpty())
                {
                    newLabel= newLabel.substring(0, newLabel.lastIndexOf(","));
                }
                note_temp.setLabels(newLabel);
                DatabaseNote.getInstanceDTB(getActivity()).noteDAO().createNote(note_temp);
                DatabaseReference UpdateNoteAfterDeletelabel = database.getReference(usercurrent.getUid()).child("ListNote").child(String.valueOf(note_temp.getId())).child("labels");
                UpdateNoteAfterDeletelabel.setValue(newLabel, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Log.e("DeleteLabel","Success");
                    }
                });
            }


        }
        DatabaseReference reference = database.getReference(usercurrent.getUid()).child("ListLabel");
        reference.child(String.valueOf(labels.getId_labels())).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getActivity(), "Labels is Deleted on Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CheckListisEmty() {
        if (mlist.size() == 0) {
            if (nulllabel.getVisibility() == View.GONE) {
                nulllabel.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (nulllabel.getVisibility() == View.VISIBLE) {
                nulllabel.setVisibility(View.GONE);
            }
        }
    }

    private void PushDataToFireBase(Labels label) {

        String path = usercurrent.getUid();
        Log.e("Uid", usercurrent.getUid());
        DatabaseReference reference = database.getReference(path);
        reference.child("ListLabel")
                .child(String.valueOf(label.getId_labels()))
                .setValue(label, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Log.e("SUCCESS", "Push data");
                        Toast.makeText(getActivity(), "Pushed Data Labels to FireBase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
