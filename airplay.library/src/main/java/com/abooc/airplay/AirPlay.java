package com.abooc.airplay;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.abooc.airplay.model.Action;
import com.abooc.util.Debug;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;

public class AirPlay {

    private static int WEB_SOCKET_PORT = 23333;

    private static AirPlay ourInstance = new AirPlay();

    private Gson mGson = new Gson();
    private ArrayList<OnConnectListener> mOnConnectListeners = new ArrayList<>();
    private ArrayList<OnReceiveMessageListener> mListeners = new ArrayList<>();

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
        Debug.anchor(wsAddress);
        if (mClient != null && (mClient.isOpen()
                || mClient.isConnecting())) {
            mClient.close();
        }
        URI uri = URI.create(wsAddress);
        mClient = new Client(uri);
        mClient.connect();
    }

    public void closeAll() {

    }


    public void send(String message) {
        if (isConnecting()) {
            Debug.anchor("send:" + message);
            try {
                mClient.send(message);
            } catch (IllegalArgumentException e) {
                Debug.e(e.getMessage());
            }
        }
    }

//    public void send(String message, OnTimeOutListener listener) {
//        send(message);
//    }
//
//    private HashMap<String, CountDownTimer> mTimeOutListeners = new HashMap<>();
//
//    public interface OnTimeOutListener {
//        void onTimeout();
//    }
//
//    private CountDownTimer DownTimer = new CountDownTimer(3000, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//
//        }
//
//        @Override
//        public void onFinish() {
//
//        }
//    };

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


    public AirPlay registerOnConnectListener(OnConnectListener listener) {
        if (listener != null && !mOnConnectListeners.contains(listener))
            mOnConnectListeners.add(listener);
        return ourInstance;
    }

    public AirPlay unregisterOnConnectListener(OnConnectListener listener) {
        mOnConnectListeners.remove(listener);
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
            Debug.anchor(getConnection().getRemoteSocketAddress());
            UiThread.post(new Runnable() {
                @Override
                public void run() {
                    int size = mOnConnectListeners.size();
                    for (int i = 0; i < size; i++) {
                        mOnConnectListeners.get(i).onOpen(serverHandshake);
                    }
                }
            });
        }

        @Override
        public void onMessage(String message) {
            Debug.anchor(getConnection().getRemoteSocketAddress() + ":" + message);
            Message toUi = Message.obtain();
            toUi.obj = message;
            UiThread.sendMessage(toUi);
        }

        @Override
        public void onClose(final int code, final String reason, final boolean remote) {
            Debug.anchor(getConnection().getRemoteSocketAddress());
            UiThread.post(new Runnable() {
                @Override
                public void run() {
                    int size = mOnConnectListeners.size();
                    for (int i = 0; i < size; i++) {
                        mOnConnectListeners.get(i).onClose(code, reason, remote);
                    }
                }
            });

        }

        @Override
        public void onError(final Exception e) {
            Debug.anchor(getConnection().getRemoteSocketAddress() + ", Exception:" + e);

            UiThread.post(new Runnable() {
                @Override
                public void run() {
                    int size = mOnConnectListeners.size();
                    for (int i = 0; i < size; i++) {
                        mOnConnectListeners.get(i).onError(e);
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
                if (message.startsWith("{") && message.endsWith("}")) {
                    Action action = mGson.fromJson(message, Action.class);
                    mListeners.get(i).onReceiveMessage(action, message);
                } else {
                    mListeners.get(i).onReceiveMessage(null, message);
                }
            }
        }
    };


}
