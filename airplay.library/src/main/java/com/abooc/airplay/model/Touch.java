package com.abooc.airplay.model;

import android.support.annotation.Keep;

/**
 * 发送数据模型:
 * 触控板上单指,双指事件Json模型
 */
@Keep
public class Touch {
    /**
     * 事件 down, up, move
     */
    @Keep
    public int action;

    @Keep
    public int id0;
    @Keep
    public float x0;
    @Keep
    public float y0;

    @Keep
    public int id1;
    @Keep
    public float x1;
    @Keep
    public float y1;

    @Keep
    public int pointerCount;


}