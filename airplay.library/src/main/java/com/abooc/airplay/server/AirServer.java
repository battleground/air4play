package com.abooc.airplay.server;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.abooc.airplay.OnReceiveMessageListener;
import com.abooc.airplay.model.Action;
import com.abooc.util.Debug;
import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class AirServer extends WebSocketServer {

    private Gson mGson = new Gson();
    private OnClientConnectListener mOnConnectListener;
    private OnReceiveMessageListener mOnReceiveMessageListener;


    public AirServer() throws UnknownHostException {
        super();
    }

    public AirServer(InetSocketAddress address) {
        super(address);
        Debug.anchor(address);
    }

    public void setOnConnectListener(OnClientConnectListener listener) {
        mOnConnectListener = listener;
    }

    public void setOnReceiveMessageListener(OnReceiveMessageListener listener) {
        mOnReceiveMessageListener = listener;
    }


    @Override
    public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
        String message = conn.getRemoteSocketAddress() + " 已连接, 共" + connections().size() + "个连接";
        Debug.anchor(message);

        UiThread.post(new Runnable() {
            @Override
            public void run() {
                if (mOnConnectListener != null) {
                    mOnConnectListener.onOpen(handshake);
                }
            }
        });
    }

    @Override
    public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
        String message = conn.getRemoteSocketAddress() + "已断开, code:" + code + ", remote:" + remote + ", reason:" + reason;
        Debug.anchor(message);

        UiThread.post(new Runnable() {
            @Override
            public void run() {
                if (mOnConnectListener != null) {
                    mOnConnectListener.onClose(code, reason, remote);
                }
            }
        });
    }


    public WebSocket send(String message) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.send(message);
        }
        return mWebSocket;
    }

    WebSocket mWebSocket;

    @Override
    public void onMessage(WebSocket conn, String message) {
        mWebSocket = conn;

        Debug.anchor(conn.getRemoteSocketAddress() + ":" + message);

        Message toUi = Message.obtain();
        toUi.obj = message;
        UiThread.sendMessage(toUi);

    }

    @Override
    public void onError(WebSocket conn, final Exception ex) {
        Debug.e(conn.getRemoteSocketAddress() + ", Exception:" + ex);

        UiThread.post(new Runnable() {
            @Override
            public void run() {
                if (mOnConnectListener != null) {
                    mOnConnectListener.onError(ex);
                }
            }
        });

    }

    private Handler UiThread = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (mOnReceiveMessageListener != null) {
                String message = msg.obj.toString();
                Action action = mGson.fromJson(message, Action.class);
                mOnReceiveMessageListener.onReceiveMessage(action, message);
            }
        }
    };

}
