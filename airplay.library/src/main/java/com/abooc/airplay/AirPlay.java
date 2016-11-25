package com.abooc.airplay;

import android.os.CountDownTimer;
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
import java.util.HashMap;

public class AirPlay {

    private static int WEB_SOCKET_PORT = 23333;

    private static AirPlay ourInstance = new AirPlay();

    private Gson mGson = new Gson();
    private ArrayList<OnConnectListener> mOnConnectListeners = new ArrayList<>();
    private ArrayList<OnReceiveMessageListener> mListeners = new ArrayList<>();

    private Client mClient;
    private HashMap<Integer, OnResponseListener> mOnResponseListeners = new HashMap<>();

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

    /**
     * Send message
     *
     * @param message 内容，Json格式，见协议文档。
     */
    public void send(String message) {
        if (isConnecting()) {
            try {
                Debug.anchor("send:" + message);
                mClient.send(message);
            } catch (IllegalArgumentException e) {
                Debug.error(e.getMessage());
            }
        }
    }

    /**
     * Send message
     *
     * @param actionCode 通信指令
     * @param message    内容，Json格式，见协议文档。
     * @param listener   消息发送反馈
     */
    public void send(int actionCode, String message, OnResponseListener listener) {
        ResponseTimer.cancel();
        mOnResponseListeners.put(actionCode, listener);
        ResponseTimer.id = actionCode;
        ResponseTimer.start();
        send(message);
    }

    public RemotePlayer build() {
        RemotePlayer mRemotePlayer = new RemotePlayer(new Sender() {
            @Override
            public void doSend(String message) {
                send(message);
            }
        });
        return mRemotePlayer;
    }

    public VRPlayer buildVRPlayer() {
        VRPlayer vrPlayer = new VRPlayer(new Sender() {
            @Override
            public void doSend(String message) {
                send(message);
            }
        });
        return vrPlayer;
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

                    if (mOnResponseListeners.containsKey(action.getCode())) {
                        mOnResponseListeners.get(action.getCode()).onResponse(action, message);
                        mOnResponseListeners.remove(action.getCode());
                    }
//                } else {
//                    mListeners.get(i).onReceiveMessage(null, message);
                }
            }
        }
    };


    public interface OnResponseListener {
        void onNoResponse();

        void onResponse(Action action, String message);
    }

    private ResponseTimer ResponseTimer = new ResponseTimer(1000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (!mOnResponseListeners.isEmpty()) {
                mOnResponseListeners.get(id).onNoResponse();
                mOnResponseListeners.remove(id);
            }
        }
    };

    private abstract class ResponseTimer extends CountDownTimer {

        public int id;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public ResponseTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
    }

}
