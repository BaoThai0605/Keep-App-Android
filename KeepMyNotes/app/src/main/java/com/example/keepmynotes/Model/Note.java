package com.example.keepmynotes.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "title")
    protected String title;

    @ColumnInfo(name = "datetime")
    protected String datetime;


    @ColumnInfo(name = "subtitle")
    protected String subtitle;

    @ColumnInfo(name = "content")
    protected String content;

    @ColumnInfo(name = "image")
    protected String image;

    @ColumnInfo(name = "video")
    protected String video;

    @ColumnInfo(name = "sound")
    protected String sound;

    @ColumnInfo(name ="password")
    protected String password;

    @ColumnInfo(name ="color")
    protected String color;

    @ColumnInfo(name ="alarm")
    protected String alarmTime;

    @ColumnInfo(name ="date")
    protected String datealarm;

    @ColumnInfo(name ="labels")
    protected String labels;

    public String getLabels() {
        return labels;
    }

    public Note(int id, String title, String datetime, String subtitle, String content, String image, String video, String sound, String password, String color, String alarmTime, String datealarm, String labels) {
        this.id = id;
        this.title = title;
        this.datetime = datetime;
        this.subtitle = subtitle;
        this.content = content;
        this.image = image;
        this.video = video;
        this.sound = sound;
        this.password = password;
        this.color = color;
        this.alarmTime = alarmTime;
        this.datealarm = datealarm;
        this.labels = labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Note() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getDatealarm() {
        return datealarm;
    }

    public void setDatealarm(String datealarm) {
        this.datealarm = datealarm;
    }

            public Map<String ,Object> toMap()
            {
                HashMap<String ,Object> rs = new HashMap<>();
                rs.put("id", id);
                rs.put("title", title);
                rs.put("datetime", datetime);
                rs.put("subtitle", subtitle);
                rs.put("content", content);
                rs.put("image", image);
                rs.put("video", video);
                rs.put("sound", sound);
                rs.put("password", password);
                rs.put("color", color);
                rs.put("alarmTime", alarmTime);
                rs.put("datealarm", datealarm);
                rs.put("labels", labels);
                return  rs;
            }



    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", datetime='" + datetime + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", sound='" + sound + '\'' +
                ", password='" + password + '\'' +
                ", color='" + color + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                ", datealarm='" + datealarm + '\'' +
                ", labels='" + labels + '\'' +
                '}';
    }

    public String toSendNote()
    {
        return "Note["
                + "id: " + id +"\n"
                + "title: " + title +"\n"
                + "datetime: " + datetime +"\n"
                + "subtitle: " + subtitle +"\n"
                + "content: " + content +"\n"
                +"]";
    }
}
