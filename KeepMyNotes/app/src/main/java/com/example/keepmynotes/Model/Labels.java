package com.example.keepmynotes.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "tblabels")
public class Labels implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id_labels;

    @ColumnInfo(name = "name")
    private String namelabels;

    @ColumnInfo(name = "check")
    private boolean checkLabel;

    public Labels() {
    }

    public int getId_labels() {
        return id_labels;
    }

    public void setId_labels(int id_labels) {
        this.id_labels = id_labels;
    }

    public String getNamelabels() {
        return namelabels;
    }

    public void setNamelabels(String namelabels) {
        this.namelabels = namelabels;
    }

    public boolean isCheckLabel() {
        return checkLabel;
    }

    public void setCheckLabel(boolean checkLabel) {
        this.checkLabel = checkLabel;
    }

    @Override
    public String toString() {
        return "Labels{" +
                "id_labels=" + id_labels +
                ", namelabels='" + namelabels + '\'' +
                ", checkLabel=" + checkLabel +
                '}';
    }

    public Map<String ,Object> toMap()
    {
        HashMap<String ,Object> rs = new HashMap<>();
        rs.put("id_labels", id_labels);
        rs.put("namelabels", namelabels);
        rs.put("check", checkLabel);
        return  rs;
    }
}
