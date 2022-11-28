package com.example.keepmynotes.View.CustomAdapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepmynotes.Controller.EventListener.NoteDelete_Event_Listener;
import com.example.keepmynotes.Model.Note_Deleted;
import com.example.keepmynotes.R;

import java.util.List;

public class RecycleApdapterRecycleBin extends RecyclerView.Adapter<RecycleApdapterRecycleBin.Holder> {
    private List<Note_Deleted> list;
    private NoteDelete_Event_Listener listener;
    public RecycleApdapterRecycleBin(List<Note_Deleted> list,NoteDelete_Event_Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater converter = LayoutInflater.from(parent.getContext());
        View rootview = converter.inflate(R.layout.layout_item_recycle,parent,false);
        return new Holder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Note_Deleted note = list.get(position);
        holder.bindata(note,listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class Holder extends RecyclerView.ViewHolder{
         TextView TitleinBin,ContentinBin;
         ImageView ImginBin;
         Button btnRestore,btnDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);
            TitleinBin = itemView.findViewById(R.id.TitleinBin);
            ContentinBin = itemView.findViewById(R.id.ContentinBin);
            ImginBin = itemView.findViewById(R.id.ImginBin);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }

        private void bindata(Note_Deleted note, NoteDelete_Event_Listener listener)
        {
            //Title Note
            if(note.getTitle().trim().isEmpty())
            {
                TitleinBin.setVisibility(View.GONE);
            }
            else
            {
                TitleinBin.setText(note.getTitle());
            }
            //Content Note
            if(note.getContent().trim().isEmpty())
            {
                ContentinBin.setVisibility(View.GONE);
            }
            else {
                ContentinBin.setText(note.getContent());
            }
            if(!note.getImage().isEmpty())
            {
                ImginBin.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                ImginBin.setVisibility(View.VISIBLE);
            }
            else
            {
                ImginBin.setVisibility(View.GONE);

            }

            btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null)
                    {
                        listener.OnDelete(note,getAdapterPosition());
                    }
                }
            });

            btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null)
                    {
                        listener.OnRestore(note,getAdapterPosition());
                    }
                }
            });



        }
    }
}
