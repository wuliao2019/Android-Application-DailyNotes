package com.cqu.notes.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class NotesItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int type;
    private int pos;
    private long modifiedTime;
    private long eventTime;
    private String title;
    private String remark;
    private String noteType;
    private boolean done;

    public NotesItem(int id, int type, int pos, long modifiedTime, long eventTime, String title, String remark, String noteType, boolean done) {
        this.id = id;
        this.type = type;
        this.pos = pos;
        this.modifiedTime = modifiedTime;
        this.eventTime = eventTime;
        this.title = title;
        this.remark = remark;
        this.noteType = noteType;
        this.done = done;
    }

    @Ignore
    public NotesItem(int type, int pos, String title) {
        this.type = type;
        this.pos = pos;
        this.title = title;
    }

    @Ignore
    public NotesItem(int type, long modifiedTime, String title, String remark, String noteType) {
        this.type = type;
        this.modifiedTime = modifiedTime;
        this.title = title;
        this.remark = remark;
        this.noteType = noteType;
    }

    @Ignore
    public NotesItem(int type, long eventTime, String title, boolean done) {
        this.type = type;
        this.eventTime = eventTime;
        this.title = title;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
