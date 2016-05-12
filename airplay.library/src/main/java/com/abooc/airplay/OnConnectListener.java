package com.abooc.airplay;

import org.java_websocket.handshake.ServerHandshake;

public interface OnConnectListener {

    void onError(Exception e);

    void onClose(int code, String reason, boolean remote);

    void onOpen(ServerHandshake serverHandshake);
}