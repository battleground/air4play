package com.abooc.airplay.server;


import org.java_websocket.handshake.ClientHandshake;

public interface OnClientConnectListener {

    void onError(Exception e);

    void onClose(int code, String reason, boolean remote);

    void onOpen(ClientHandshake serverHandshake);
}