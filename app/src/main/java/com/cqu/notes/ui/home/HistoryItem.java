package com.cqu.notes.ui.home;

public class HistoryItem {
    private final String date;
    private final String info;

    public HistoryItem(String date, String info) {
        this.date = date;
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }

}
