package com.example.keepmynotes.View.CustomAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepmynotes.Controller.EventListener.LabelCheck;
import com.example.keepmynotes.Controller.EventListener.Label_Listener;
import com.example.keepmynotes.Model.Labels;
import com.example.keepmynotes.R;

import java.util.List;

public class RecycleApdapterLabelsSeleted extends RecyclerView.Adapter<RecycleApdapterLabelsSeleted.Holder> {
    private List<Labels> list;
    private String labelString;
    private LabelCheck listener;


    public RecycleApdapterLabelsSeleted(List<Labels> list,String label, LabelCheck listener) {
        this.list = list;
        this.labelString = label;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater converter = LayoutInflater.from(parent.getContext());
        View rootview = converter.inflate(R.layout.layout_item_label_seletecd, parent, false);
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
        TextView name_lb;
        CheckBox cb_lb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            name_lb = itemView.findViewById(R.id.name_lb);
            cb_lb = itemView.findViewById(R.id.cb_lb);

        }

        private void bindata(Labels labels, LabelCheck listener) {
            name_lb.setText(labels.getNamelabels());
           
            cb_lb.setChecked(labels.isCheckLabel());
            cb_lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener!=null)
                    {
                        list.get(getAdapterPosition()).setCheckLabel(cb_lb.isChecked());
                        listener.OnCheck(cb_lb.isChecked(), getAdapterPosition());
                    }
                }
            });



        }
    }
}
