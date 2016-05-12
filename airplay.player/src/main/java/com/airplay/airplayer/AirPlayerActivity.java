package com.airplay.airplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.abooc.airplay.OnReceiveMessageListener;
import com.abooc.airplay.Sender;
import com.abooc.airplay.model.Action;
import com.abooc.airplay.model.GYRO;
import com.abooc.airplay.model.SeekProcess;
import com.abooc.airplay.model.Touch;
import com.abooc.airplay.model.V;
import com.abooc.airplay.model.Xyz;
import com.abooc.airplay.server.AirServer;
import com.abooc.airplay.server.OnClientConnectListener;
import com.abooc.airplay.server.PlayerFeedBacker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.handshake.ClientHandshake;
import org.lee.android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;

import bf.cloud.BFMediaPlayerControllerVod;
import bf.cloud.android.base.BFVRConst;
import bf.cloud.android.playutils.BasePlayer;
import bf.cloud.android.playutils.DecodeMode;
import bf.cloud.android.playutils.VodPlayer;
import bf.cloud.black_board_ui.R;

import static com.abooc.airplay.model.Action.GOS;
import static com.abooc.airplay.model.Action.PAUSE;
import static com.abooc.airplay.model.Action.PLAYER_GOS;
import static com.abooc.airplay.model.Action.PLAYER_TOUCH;
import static com.abooc.airplay.model.Action.RESUME;
import static com.abooc.airplay.model.Action.SEEK;
import static com.abooc.airplay.model.Action.START;
import static com.abooc.airplay.model.Action.STOP;
import static com.abooc.airplay.model.Action.TOUCH;
import static com.abooc.airplay.model.Action.TOUCH_XYZ;
import static com.abooc.airplay.model.Action.VIDEO_INFO;

public class AirPlayerActivity extends AppCompatActivity implements
        OnClientConnectListener, OnReceiveMessageListener {

    private VodPlayer mVodPlayer = null;
    private BFMediaPlayerControllerVod mMediaController = null;
    private long mHistory = -1;

    private Button mLogPanel;
    private FrameLayout mTouchPanel = null;

    StringBuffer mBuffer = new StringBuffer();
    private AirServer mAirServer;
    PlayerFeedBacker mPlayerFeedBacker;

    private static final int DEFAULT_PORT = 23333;

    private Gson mGson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airplayer);

        init();


        mPlayerFeedBacker = new PlayerFeedBacker(new Sender() {
            @Override
            public void doSend(String message) {
                mAirServer.send(message);
            }
        });

        mMediaController = (BFMediaPlayerControllerVod) findViewById(R.id.vod_media_controller_vod);
        mMediaController.setAutoChangeScreen(false);
        mMediaController.setTimerTicker(new BFMediaPlayerControllerVod.OnTimerTicker() {
            @Override
            public void onTicker(int position) {
                mPlayerFeedBacker.ticker(position);
            }
        });
        mVodPlayer = (VodPlayer) mMediaController.getPlayer();
        mVodPlayer.setDecodeMode(DecodeMode.MEDIAPLYAER);

        mAirServer = new AirServer(new InetSocketAddress(DEFAULT_PORT));
        mAirServer.setOnReceiveMessageListener(this);
        mAirServer.setOnConnectListener(this);
        mAirServer.start();
    }

    private void init() {
        final ScrollView mScrollView = (ScrollView) findViewById(R.id.ScrollView);
        mLogPanel = (Button) findViewById(R.id.LogPanel);
        mLogPanel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mScrollView.smoothScrollTo(0, mLogPanel.getBottom());
                } else {
                    mScrollView.smoothScrollTo(0, 0);
                }
            }
        });
        mTouchPanel = (FrameLayout) findViewById(R.id.TouchPanel);
        mTouchPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MotionEvent newEvent = MotionEvent.obtain(0, 0, event.getAction(), event.getX(), event.getY(), 0);
                mVodPlayer.onTouch(newEvent);

                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        if (mHistory > 0) {
            mVodPlayer.start((int) mHistory);
            mHistory = -1;
        }
        super.onStart();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                mVodPlayer.start();
                break;
            case R.id.resume:
                mVodPlayer.resume();
                mPlayerFeedBacker.resume();
                break;
            case R.id.pause:
                mVodPlayer.pause();
                mPlayerFeedBacker.pause();
                break;
            case R.id.stop:
                mVodPlayer.stop();
                try {
                    mMediaController.finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                mPlayerFeedBacker.stop();
                break;
            case R.id.get_duration:
                break;
        }
    }


    @Override
    protected void onPause() {
        mHistory = mVodPlayer.getCurrentPosition();
        mVodPlayer.stop();
        super.onPause();
    }


    @Override
    public void onError(Exception e) {

        print(e.toString());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        print("code:" + code + ", remote:" + remote + ", reason:" + reason);
    }

    @Override
    public void onOpen(ClientHandshake clientHandshake) {
        print("连接:" + " OK");

    }

    @Override
    public void onReceiveMessage(Action action, String message) {
        switch (action.getCode()) {
            case START:
                mVodPlayer.stop();

                Action<V> vAction = mGson.fromJson(message, new TypeToken<Action<V>>(){}.getType());
                V video = vAction.getInfo();

                print("播放, type:" + video.type + ", url:" + video.url);
                mVodPlayer.setPlayerType(BasePlayer.PLAYER_TYPE.FULL_SIGHT);
                mVodPlayer.setDataSource(video.url);
                mVodPlayer.start();

                mVodPlayer.seekTo((int) video.position);

                mVodPlayer.changedFullSightMode(BFVRConst.RenderMode.FULLVIEW, BFVRConst.ControlMode.GYROSCOPE);
                mVodPlayer.setIfUsingHeadtrack(false);
                break;

            case Action.FULL_VIEW_SIGHT:
                mVodPlayer.changedFullSightMode(BFVRConst.RenderMode.FULLVIEW);
                break;
            case Action.FULL_VIEW_3D:
                mVodPlayer.changedFullSightMode(BFVRConst.RenderMode.FULLVIEW3D);
                break;
            case PLAYER_TOUCH:
                mVodPlayer.changedFullSightMode(BFVRConst.ControlMode.TOUCH);
                break;
            case PLAYER_GOS:
                mVodPlayer.changedFullSightMode(BFVRConst.RenderMode.FULLVIEW, BFVRConst.ControlMode.GYROSCOPE);
                mVodPlayer.setIfUsingHeadtrack(false);
                break;

            case SEEK:
                Action<SeekProcess> seekAction = mGson.fromJson(message, new TypeToken<Action<SeekProcess>>(){}.getType());
                SeekProcess seekProcess = seekAction.getInfo();
                mVodPlayer.seekTo(seekProcess.position);
//                mSeekBar.setMax(seekProcess.duration);
//                mSeekBar.setProgress(seekProcess.position);
//
//                mStart.setText(Utils.formatSecondTime(seekProcess.position));
//                mEnd.setText(Utils.formatSecondTime(seekProcess.duration));
                break;
            case TOUCH:
//                Action<Touch> touchAction = mGson.fromJson(message, new TypeToken<Action<Touch>>(){}.getType());
//                Touch touch = touchAction.getInfo();
//                MotionEvent event = MotionEvent.obtain(0, 0, touch.touch_action, touch.touch_x, touch.touch_y, 0);
//                mVodPlayer.onTouch(event);
                break;
            case TOUCH_XYZ:

                Action<Xyz> xyzAction = mGson.fromJson(message, new TypeToken<Action<Xyz>>(){}.getType());
                Xyz xyz = xyzAction.getInfo();
                mVodPlayer.setRotation(xyz.x, xyz.y, xyz.z);
                break;
            case GOS:
                Action<GYRO> gyroAction = mGson.fromJson(message, new TypeToken<Action<GYRO>>(){}.getType());
                GYRO gyro = gyroAction.getInfo();

                Log.anchor("array:" + (gyro.array == null ? "NULL" : toString(gyro.array)));
                mVodPlayer.setRotation(gyro.array);
                break;
            case RESUME:
                mVodPlayer.resume();
                print("继续播放");
                break;
            case PAUSE:
                mVodPlayer.pause();
                print("暂停");
                break;
            case STOP:
                mVodPlayer.stop();
                print("视频停止");
                break;
            case VIDEO_INFO:
                print("获取在播视频信息");
                long duration = mVodPlayer.getDuration() / 1000;
                long position = mVodPlayer.getCurrentPosition() / 1000;
                String name = mVodPlayer.getVideoName();
                mVodPlayer.getId();
                mPlayerFeedBacker.sendVideoInfo(V.Type.TYPE_BAOFENG_VR, name, position, duration);
                break;

        }

    }


    public static String toString(float[] array) {
        StringBuffer buffer = new StringBuffer();
        float[] var5 = array;
        int var4 = array.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            float i = var5[var3];
            buffer.append(i + ",");
        }

        return buffer.toString();
    }

    private String getIp() {
        if (mAirServer.connections().iterator().hasNext()) {
            return mAirServer.connections().iterator().next().getRemoteSocketAddress().toString();
        }
        return "没有连接. ";
    }

    private void print(String message) {
//        mBuffer.append(message).append("\n");
//        mLeft.setText(mBuffer.toString());
        mLogPanel.setText(getIp() + ":" + message);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mVodPlayer.stop();
        try {
            mMediaController.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onDestroy();
        try {
            mAirServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public Touch createTouchBean(MotionEvent event) {
        Touch touchBean = new Touch();
        touchBean.action = event.getAction();
        int pointerCount = event.getPointerCount();
        touchBean.pointerCount = pointerCount;
        for (int i = 0; i < pointerCount; i++) {
            if (i == 0) {
                touchBean.id0 = event.getPointerId(i);
                touchBean.x0 = event.getX(i);
                touchBean.y0 = event.getY(i);
                continue;
            } else if (i == 1) {
                touchBean.id1 = event.getPointerId(i);
                touchBean.x1 = event.getX(i);
                touchBean.y1 = event.getY(i);
                continue;
            }
        }
        return touchBean;
    }
}
