package com.example.keepmynotes.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Setting")
public class SettingNotes implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id_setting;
    private int textsize;
    private String fontText;
    private int Timedelete;
    private String TimeUnit;

    public SettingNotes() {
    }

    public int getId_setting() {
        return id_setting;
    }

    public void setId_setting(int id_setting) {
        this.id_setting = id_setting;
    }

    public int getTextsize() {
        return textsize;
    }

    public void setTextsize(int textsize) {
        this.textsize = textsize;
    }

    public String getFontText() {
        return fontText;
    }

    public void setFontText(String fontText) {
        this.fontText = fontText;
    }

    public int getTimedelete() {
        return Timedelete;
    }

    public void setTimedelete(int timedelete) {
        Timedelete = timedelete;
    }

    public String getTimeUnit() {
        return TimeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        TimeUnit = timeUnit;
    }

    public static SettingNotes getDefaultSetting()
    {
        SettingNotes setting = new SettingNotes();
        setting.setTextsize(20);
        setting.setFontText("sans-serif");
        setting.setTimedelete(20);
        setting.setTimeUnit("SECONDS");

        return  setting;
    }

}
