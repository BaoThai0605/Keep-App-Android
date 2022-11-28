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

import com.example.keepmynotes.Controller.EventListener.Label_Listener;
import com.example.keepmynotes.Controller.EventListener.NoteDelete_Event_Listener;
import com.example.keepmynotes.Model.Labels;
import com.example.keepmynotes.Model.Note_Deleted;
import com.example.keepmynotes.R;

import java.util.List;

public class RecycleApdapterLabels extends RecyclerView.Adapter<RecycleApdapterLabels.Holder> {
    private List<Labels> list;
    private Label_Listener listener;

    public RecycleApdapterLabels(List<Labels> list, Label_Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater converter = LayoutInflater.from(parent.getContext());
        View rootview = converter.inflate(R.layout.layout_item_labels, parent, false);
        return new Holder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Labels note = list.get(position);
        holder.bindata(note, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView namelabel;
        Button btnDeletelabel;

        public Holder(@NonNull View itemView) {
            super(itemView);
            namelabel = itemView.findViewById(R.id.namelabel);
            btnDeletelabel = itemView.findViewById(R.id.btnDeletelabel);

        }

        private void bindata(Labels labels, Label_Listener listener) {
            namelabel.setText(labels.getNamelabels());
            btnDeletelabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        listener.OnClicktoDelete(labels,getAdapterPosition());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        listener.OnClickViewLabel(labels,getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener!=null)
                    {
                        listener.OnLongClick(labels,getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
