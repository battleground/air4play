package com.abooc.airremoter.model;

import android.support.annotation.Keep;

/**
 * 陀螺仪
 */
@Keep
public class GYRO {

    @Keep
    public float[] array;

    public GYRO(float[] array) {
        this.array = array;
    }

}