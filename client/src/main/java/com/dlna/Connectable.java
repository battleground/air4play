package com.dlna;

/**
 * 加载状态interface，为方便当前设备列表展现设备连接状态
 */
public interface Connectable {

    /**
     * 加载状态值，包括正常状态、连接中、连接成功、失败
     */
    enum State {
        NO_CONNECT, CONNECTING, CONNECTED, ERROR
    }

    /**
     * 是否加载中...
     *
     * @return
     */
    boolean isConnecting();

    /**
     * 更改加载状态
     *
     * @param state 状态码
     */
    void changeState(State state);

    /**
     * 获取当前状态值
     *
     * @return
     */
    State getState();

}
