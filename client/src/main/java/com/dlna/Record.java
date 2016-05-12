package com.dlna;

import android.os.Bundle;

public class Record {

    public static final String KEY_JSON_VIDEO = "video";
    public static final String KEY_JSON_SERIES = "series";

    private String iVid;
    private String title;
    private boolean isPiandan;

    private Bundle bundle;
    private int xuanji;
    private int qingxidu;
    private int bili;


    private boolean playing;

    public void playing(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getVid() {
        return iVid;
    }

    public void setVid(String vid) {
        iVid = vid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void isPiandan(boolean isPiandan) {
        this.isPiandan = isPiandan;
    }

    public boolean isPiandan() {
        return isPiandan;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public int getXuanji() {
        return xuanji;
    }

    public void setXuanji(int xuanji) {
        this.xuanji = xuanji;
    }

    public int getQingxidu() {
        return qingxidu;
    }

    public void setQingxidu(int qingxidu) {
        this.qingxidu = qingxidu;
    }


    public int getbili() {
        return bili;
    }

    public void setBili(int bili) {
        this.bili = bili;
    }


}