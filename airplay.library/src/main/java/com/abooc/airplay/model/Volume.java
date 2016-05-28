package com.abooc.airplay.model;

import android.support.annotation.Keep;

@Keep
public class Volume {

    @Keep
    public int volume;

    public Volume(int v) {
        this.volume = v;
    }

}