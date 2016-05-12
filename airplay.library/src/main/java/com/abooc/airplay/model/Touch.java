package com.abooc.airplay.model;

/**
 * 发送数据模型:
 * 触控板上单指,双指事件Json模型
 */
public class Touch {
    /** 事件 down, up, move */
    public int action;

    public int id0;
    public float x0;
    public float y0;

    public int id1;
    public float x1;
    public float y1;

    public int pointerCount;


}