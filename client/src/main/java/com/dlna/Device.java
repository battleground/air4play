package com.dlna;


import android.text.TextUtils;

import com.google.gson.Gson;

import org.lee.android.util.Log;

import tv.matchstick.flint.FlintDevice;


/**
 * 设备信息
 * <p/>
 * Created by author:大宇
 * email:allnet@live.cn
 */
public class Device implements Connectable {

    private FlintDevice flintDevice;
    private State mState = State.NO_CONNECT;
    private String name;

    public FlintDevice getFlintDevice() {
        return flintDevice;
    }

    public void setFlintDevice(FlintDevice flintDevice) {
        this.flintDevice = flintDevice;
    }

    public String getIp() {
        if (flintDevice == null) {
            return "";
        }
        return flintDevice.getIpAddress().getHostAddress();
    }

    public String getDeviceName() {
        if (flintDevice == null) {
            return name;
        }
        return flintDevice.getFriendlyName();
    }

    @Override
    public boolean isConnecting() {
        return mState == State.CONNECTING;
    }

    /**
     * 更改加载状态
     *
     * @param state 状态码
     */
    @Override
    public void changeState(State state) {
        Log.anchor(state);
        this.mState = state;
    }

    public State getState() {
        return mState;
    }

    public boolean isFailure() {
        return mState == State.ERROR;
    }

    public boolean isNormal() {
        return mState == State.NO_CONNECT;
    }

    public boolean isConnected() {
        return mState == State.CONNECTED;
    }

//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
//        return result;
//    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        return TextUtils.equals(getIp(), ((Device) other).getIp());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void setName(String name) {
        this.name = name;
    }
}
