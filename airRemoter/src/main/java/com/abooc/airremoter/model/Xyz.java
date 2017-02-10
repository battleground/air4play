package com.abooc.airremoter.model;

import android.support.annotation.Keep;

/**
 * 发送数据模型:
 * <br>
 * SDK播放屏幕上进行单指或双指拖拽事件的Json模型
 */
@Keep
public class Xyz {

    @Keep
    public float x;
    @Keep
    public float y;
    @Keep
    public float z;

    public Xyz(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}