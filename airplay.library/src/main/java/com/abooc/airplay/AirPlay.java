package com.abooc.airplay;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.abooc.airplay.model.Action;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.lee.android.util.Log;

import java.net.URI;
import java.util.ArrayList;

public class AirPlay {

    private static int WEB_SOCKET_PORT = 23333;

    private static AirPlay ourInstance = new AirPlay();

    private Gson mGson = new Gson();
    private OnConnectListener mOnConnectListener;
    ArrayList<OnReceiveMessageListener> mListeners = new ArrayList<>();

    private Client mClient;
    private RemotePlayer mRemotePlayer;

    private AirPlay() {

    }

    public static AirPlay getInstance() {
        return ourInstance;
    }

    public boolean isConnecting() {
        if (mClient != null) {
            return mClient.isOpen();
        }
        return false;
    }

    public void connect(String ip) {
        String wsAddress = "ws://" + ip + ":" + WEB_SOCKET_PORT;
        Log.anchor(wsAddress);
        URI uri = URI.create(wsAddress);

        if (mClient != null && (mClient.isOpen()
                || mClient.isConnecting())) {
            mClient.close();
        }

        mClient = new Client(uri);
        mClient.connect();
    }

    public void send(String message) {
        if (isConnecting()) {
            Log.anchor("send:" + message);
            try {
                mClient.send(message);
            } catch (IllegalArgumentException e) {
                Log.e(e.getMessage());
            }
        }
    }

    public RemotePlayer build() {
        if (mRemotePlayer == null) {
            mRemotePlayer = new RemotePlayer(new Sender() {
                @Override
                public void doSend(String message) {
                    send(message);
                }
            });
        }
        return mRemotePlayer;
    }


    public AirPlay setOnConnectListener(OnConnectListener listener) {
        mOnConnectListener = listener;
        return ourInstance;
    }

    public AirPlay registerOnReceiveMessageListener(OnReceiveMessageListener listener) {
        if (listener != null && !mListeners.contains(listener))
            mListeners.add(listener);
        return ourInstance;
    }

    public AirPlay unregisterOnReceiveMessageListener(OnReceiveMessageListener listener) {
        mListeners.remove(listener);
        return ourInstance;
    }

    public void close() {
        if (isConnecting()) {
            mClient.close();
        }
    }

    private class Client extends WebSocketClient {

        public Client(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(final ServerHandshake serverHandshake) {
            if (mRemotePlayer != null)
                mRemotePlayer.remoteOn();
            Log.anchor(getConnection().getRemoteSocketAddress());
            UiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onOpen(serverHandshake);
                    }
                }
            });
        }

        @Override
        public void onMessage(String message) {
            if (mRemotePlayer != null)
                mRemotePlayer.remoteOn();
            Log.anchor(getConnection().getRemoteSocketAddress() + ":" + message);
            Message toUi = Message.obtain();
            toUi.obj = message;
            UiThread.sendMessage(toUi);
        }

        @Override
        public void onClose(final int code, final String reason, final boolean remote) {
            if (mRemotePlayer != null)
                mRemotePlayer.remoteOff();
            Log.anchor(getConnection().getRemoteSocketAddress());
            UiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onClose(code, reason, remote);
                    }
                }
            });

        }

        @Override
        public void onError(final Exception e) {
            Log.anchor(getConnection().getRemoteSocketAddress() + ", Exception:" + e);

            UiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onError(e);
                    }
                }
            });
        }
    }


    private Handler UiThread = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            int size = mListeners.size();
            for (int i = 0; i < size; i++) {
                String message = msg.obj.toString();
                Action action = mGson.fromJson(message, Action.class);
                mListeners.get(i).onReceiveMessage(action, message);
            }
        }
    };


}