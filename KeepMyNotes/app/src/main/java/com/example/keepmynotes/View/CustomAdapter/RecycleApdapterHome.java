package com.example.keepmynotes.View.CustomAdapter;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepmynotes.Controller.EventListener.NoteEventListener;
import com.example.keepmynotes.Model.Database.DatabaseSetting;
import com.example.keepmynotes.Model.Note;
import com.example.keepmynotes.Model.SettingNotes;
import com.example.keepmynotes.R;

import java.util.List;

public class RecycleApdapterHome extends RecyclerView.Adapter<RecycleApdapterHome.Holder> {
    private List<Note> list;
    private NoteEventListener listener;
    private SettingNotes settingNotes;
    public RecycleApdapterHome(List<Note> list, NoteEventListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        settingNotes = DatabaseSetting.getInstance(parent.getContext()).settingDAO().getCurrentSetting();
        LayoutInflater converter = LayoutInflater.from(parent.getContext());
        View rootview = converter.inflate(R.layout.note_item_layout_1,parent,false);
        return new Holder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Note note = list.get(position);
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
        TextView title_note,subtitle_note,content_note,datetime_note;
        LinearLayout layoutfornote;
        ImageView imgViewNote,passdisplay,alarmdisplay;
        VideoView videoViewNote;
        LinearLayout LabelsNotes;
        public Holder(@NonNull View itemView) {
            super(itemView);
            title_note = itemView.findViewById(R.id.title_note);
            subtitle_note = itemView.findViewById(R.id.subtitle_note);
            content_note = itemView.findViewById(R.id.content_note);
            datetime_note = itemView.findViewById(R.id.datetime_note);
            layoutfornote = itemView.findViewById(R.id.layoutfornote);
            imgViewNote = itemView.findViewById(R.id.imgViewNote);
            passdisplay = itemView.findViewById(R.id.passdisplay);
            alarmdisplay = itemView.findViewById(R.id.alarmdisplay);
            videoViewNote = itemView.findViewById(R.id.videoViewNote);
            LabelsNotes = itemView.findViewById(R.id.LabelsNotes);
        }

        private void bindata(Note note, NoteEventListener listener)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    if (listener!=null)
                    {
                        // Không nên thực thi việc click ở trong adapter, chúng ta nên thẩy nó ra hàm main để xử lí, Vì vậy chúng ta nên sử dụng Interface để xử lí việc này.
                        //Tạo interface MyIconClickListener
                        listener.OnClicktoVieworUpdate(note,getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener!=null)
                    {
                        // Không nên thực thi việc click ở trong adapter, chúng ta nên thẩy nó ra hàm main để xử lí, Vì vậy chúng ta nên sử dụng Interface để xử lí việc này.
                        //Tạo interface MyIconClickListener
                        listener.OnLongClicktoSetAlarm(note,getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
            //Title Note
            if(note.getTitle().trim().isEmpty())
            {
                title_note.setVisibility(View.GONE);
            }
            else
            {
                title_note.setText(note.getTitle());
            }
            //SubTitle Note
            if(note.getSubtitle().trim().isEmpty())
            {
                subtitle_note.setVisibility(View.GONE);
            }
            else
            {
                subtitle_note.setText(note.getSubtitle());
            }
            //Content Title Note
            if(note.getContent().trim().isEmpty())
            {
                content_note.setVisibility(View.GONE);
            }
            else
            {
                content_note.setText(note.getContent());
            }
            if(note.getColor()!=null)
            {
                layoutfornote.setBackgroundColor(Color.parseColor(note.getColor()));
            }
            if(!note.getImage().isEmpty())
            {
                imgViewNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                imgViewNote.setVisibility(View.VISIBLE);
            }
            else
            {
                imgViewNote.setVisibility(View.GONE);
            }

            if(!note.getVideo().isEmpty())
            {
                videoViewNote.setVideoPath(note.getVideo());
                videoViewNote.setVisibility(View.VISIBLE);
                videoViewNote.start();
            }
            else
            {
                videoViewNote.setVisibility(View.GONE);
            }

            if(note.getPassword()!=null && !note.getPassword().isEmpty())
            {
                passdisplay.setVisibility(View.VISIBLE);
            }

            if( !note.getDatealarm().isEmpty())
            {
                alarmdisplay.setVisibility(View.VISIBLE);
            }

            datetime_note.setText(note.getDatetime());
            //Setting Font and size
            SettingFontAndSize();
            DisplayLabel(note.getLabels());

        }

        private void SettingFontAndSize() {
            /////////////Size///////////////////////
            title_note.setTextSize(settingNotes.getTextsize());
            subtitle_note.setTextSize(settingNotes.getTextsize());
            content_note.setTextSize(settingNotes.getTextsize());
            ////////////////Font///////////////////////
            title_note.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.BOLD_ITALIC));
            subtitle_note.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
            content_note.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
            datetime_note.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
            ///////////////////////////////////////////

        }
        private void DisplayLabel(String label) {
            String[] words = label.split(",");
            LabelsNotes.removeAllViews();
            for(String s : words)
            {
                TextView textView = new TextView(itemView.getContext());
                textView.setText(s);
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#FF000000"));
                textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5,5,5,5);
                textView.setLayoutParams(params);
                textView.setTypeface(Typeface.create(settingNotes.getFontText(), Typeface.NORMAL));
                LabelsNotes.addView(textView);
            }
        }
    }
}
