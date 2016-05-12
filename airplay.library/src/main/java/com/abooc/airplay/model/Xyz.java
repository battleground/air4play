package com.abooc.airplay.model;

/**
 * 发送数据模型:
 * <p/>
 * SDK播放屏幕上进行单指或双指拖拽事件的Json模型
 */
public class Xyz {

    public float x;
    public float y;
    public float z;

    public Xyz(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}