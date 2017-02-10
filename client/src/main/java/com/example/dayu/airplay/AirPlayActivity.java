//package com.example.dayu.airplay;
//
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ScrollView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.abooc.airplay.AirPlay;
//import com.abooc.airplay.OnConnectListener;
//import com.abooc.airplay.OnReceiveMessageListener;
//import com.abooc.airplay.Utils;
//import com.abooc.airplay.VRPlayer;
//import com.abooc.airplay.model.Action;
//import com.abooc.airplay.model.SeekProcess;
//import com.abooc.airplay.model.Touch;
//import com.abooc.airplay.model.V;
//import com.abooc.util.Debug;
//import com.airplay.PlayActivity;
//import com.dlna.Device;
//import com.dlna.RoutersDialog;
//import com.example.dayu.airplay.Discover;
//import com.google.gson.Gson;
//
//import org.java_websocket.handshake.ServerHandshake;
//
//import static com.abooc.airplay.model.Action.PAUSE;
//import static com.abooc.airplay.model.Action.REMOTE_CLIENT_CODE;
//import static com.abooc.airplay.model.Action.RESUME;
//import static com.abooc.airplay.model.Action.SEEK;
//import static com.abooc.airplay.model.Action.TICKER;
//import static com.abooc.airplay.model.Action.VIDEO_INFO;
//
//public class AirPlayActivity extends PlayActivity implements
//        OnConnectListener, OnReceiveMessageListener {
//
//
//    private TextView mStart;
//    private TextView mEnd;
//    private SeekBar mSeekBar;
//
//    private ScrollView mScrollView;
//    private View mTouchPanel;
//    private TextView mResult;
//
//    private AirPlay mAirPlay;
//    private VRPlayer mRemotePlayer;
//
//
//    // https://github.com/openflint/simple-player-demo/tree/master/sender
//
//    private boolean down;
//    private float mx;
//    private float my;
//    private static final int IGNORE_ACTION_NUMS = 2;
//    private int mIgnoreActionNums;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getSupportActionBar().setSubtitle(Version.getFullVersion(this));
//
//        Discover.creator(this);
//        setContentView(R.layout.activity_air);
//        bindVideoView();
////        bindTouchPanel();
//
//
//        mTouchPanel = findViewById(R.id.TouchPanel);
//        mTouchPanel.setOnTouchListener(onTouchListener());
//
//        mScrollView = (ScrollView) findViewById(R.id.ScrollView);
//        mResult = (TextView) findViewById(R.id.Result);
//
//        mAirPlay = AirPlay.getInstance();
//        mAirPlay.registerOnConnectListener(this);
//        mAirPlay.registerOnReceiveMessageListener(this);
//        mRemotePlayer = AirPlay.getInstance().buildVRPlayer();
//    }
//
//    private void bindVideoView() {
//
//        mStart = (TextView) findViewById(R.id.start);
//        mEnd = (TextView) findViewById(R.id.end);
//
//        mSeekBar = (SeekBar) findViewById(R.id.SeekBar);
//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (isConnecting()) {
////                    Log.anchor("progress:" + progress);
////                    mRemotePlayer.seek(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // 停止接收message改变进度条
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (isConnecting()) {
//                    Debug.anchor("progress:" + seekBar.getProgress());
//                    mRemotePlayer.seek(seekBar.getProgress());
//                }
//
//            }
//        });
//    }
//
//    private void bindTouchPanel() {
//        mTouchPanel = findViewById(R.id.TouchPanel);
//        mTouchPanel.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        down = true;
//                        mx = event.getX();
//                        my = event.getY();
//                        mIgnoreActionNums = 0;
//                        Touch touch = createTouchBean(event);
//                        mRemotePlayer.getVRController().touch(touch);
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP: {
//                        down = false;
//                        mIgnoreActionNums = 0;
//                        Touch touch = createTouchBean(event);
//                        mRemotePlayer.getVRController().touch(touch);
//                        break;
//                    }
//                    case MotionEvent.ACTION_MOVE: {
//                        if (down) {
//                            ++mIgnoreActionNums;
//                            if (mIgnoreActionNums > IGNORE_ACTION_NUMS && mx >= 0.0F && my >= 0.0F) {
//                                mx = event.getX();
//                                my = event.getY();
//                                Touch touch = createTouchBean(event);
//                                mRemotePlayer.getVRController().touch(touch);
//                            }
//                        }
//                        break;
//                    }
//                }
//                return true;
//            }
//        });
//    }
//
//
//    private float dX;
//    private float dY;
//    private boolean isClick;
//
//    private View.OnTouchListener onTouchListener() {
//        return new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        dX = event.getX();
//                        dY = event.getY();
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:
//                        if (isClick) {
//                            isClick = false;
//                            doClick(event);
//                        } else {
//
//                            float x = event.getX();
//                            float y = event.getY();
//
//                            if (Math.abs(x - dX) > Math.abs(y - dY)) {// 横向
//                                if (x > dX) { // →
//                                    onRight();
//                                } else { // ←
//                                    onLeft();
//                                }
//                            } else { //纵向
//                                if (y > dY) { // 下
//                                    onDown();
//                                } else { // 上
//                                    onUp();
//                                }
//                            }
//                        }
//                        dX = 0;
//                        dY = 0;
//                        break;
//                    case MotionEvent.ACTION_MOVE: {
//                        float x = event.getX();
//                        float y = event.getY();
//
//                        if (Math.abs(dX - x) > 10 || Math.abs(dY - y) > 10) {
//                            isClick = false;
//
////                            if (Math.abs(x - dX) > Math.abs(y - dY)) {// 横向
////                                if (x > dX) { // →
////
////                                    dX = x;
////                                    dY = y;
////                                    onRight();
////
////                                } else { // ←
////
////                                    dX = x;
////                                    dY = y;
////                                    onLeft();
////                                }
////                            } else { //纵向
////                                if (y > dY) { // 下
////
////                                    dX = x;
////                                    dY = y;
////                                    onDown();
////                                } else { // 上
////
////                                    dX = x;
////                                    dY = y;
////                                    onUp();
////                                }
////                            }
//
//                        } else {
//                            isClick = true;
//                        }
//                        break;
//                    }
//                }
//                return true;
//            }
//
//        };
//    }
//
//    private void onUp() {
//        mResult.setText("上上");
//
//    }
//
//    private void onDown() {
//        mResult.setText("下下");
//
//    }
//
//    private void onLeft() {
//        mResult.setText("←←←←");
//
//    }
//
//    private void onRight() {
//        mResult.setText("→→→→");
//
//    }
//
//    /**
//     * Touch事件转 onClick
//     *
//     * @param event
//     */
//    private void doClick(MotionEvent event) {
//        mResult.setText("doClick");
//    }
//
//    public Touch createTouchBean(MotionEvent event) {
//        Touch touchBean = new Touch();
//        touchBean.action = event.getAction();
//        int pointerCount = event.getPointerCount();
//        touchBean.pointerCount = pointerCount;
//        for (int i = 0; i < pointerCount; i++) {
//            if (i == 0) {
//                touchBean.id0 = event.getPointerId(i);
//                touchBean.x0 = event.getX(i);
//                touchBean.y0 = event.getY(i);
//            } else if (i == 1) {
//                touchBean.id1 = event.getPointerId(i);
//                touchBean.x1 = event.getX(i);
//                touchBean.y1 = event.getY(i);
//            }
//        }
//        return touchBean;
//    }
//
//
//    private RoutersDialog mRouterDialog;
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_select:
//                showRoutersDialog();
//                break;
//            case R.id.menu_close:
//                if (isConnecting())
//                    mRemotePlayer.stop();
//                break;
//            case R.id.menu_248:
//                connect("192.168.8.248");
//                break;
//            case R.id.menu_191:
//                connect("192.168.1.191");
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        Debug.anchor();
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        Debug.anchor();
//        super.onPause();
//    }
//
//    public void showRoutersDialog() {
//        if (mRouterDialog == null) {
//            mRouterDialog = new RoutersDialog(this);
//            mRouterDialog.setTitle("选择设备");
//            mRouterDialog.setCanceledOnTouchOutside(false);
//            mRouterDialog.setOnItemClickListener(new RoutersDialog.OnItemClickListener() {
//                @Override
//                public void onItemClick(DialogInterface dialog, AdapterView<?> parent, View view, int position) {
//                    Device device = mRouterDialog.getAdapter().getItem(position);
//
//                    mAirPlay.connect(device.getIp());
//
//                }
//            });
//        }
//        mRouterDialog.show();
//    }
//
//    private boolean isShowing() {
//        if (mRouterDialog != null)
//            return mRouterDialog.isShowing();
//        return false;
//    }
//
//    public void onPlayVideo(View view) {
//        Debug.anchor();
//        if (isConnecting()) {
//            V v = new V();
//            v.url = Build.VIDEO_VR_URL;
//            v.type = V.Type.TYPE_BAOFENG_VR.value();
//            mRemotePlayer.start(V.Type.TYPE_BAOFENG_VR, v.url, v.name, v.position);
//
//        }
//    }
//
//    public void onVideoInfo(View view) {
//        Debug.anchor();
//
//        mRemotePlayer.getVideoInfo();
//    }
//
//    public void onClose(View view) {
//        if (isConnecting()) {
//            mAirPlay.close();
//        }
//    }
//
//    void connect(String ip) {
//        mAirPlay.connect(ip);
//    }
//
//    @Override
//    public void onError(Exception e) {
//        print(e.toString());
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//        print("code:" + code + ", remote:" + remote + ", reason:" + reason);
//    }
//
//    @Override
//    public void onOpen(ServerHandshake serverHandshake) {
//        print("连接:" + " OK");
//
//        if (isShowing()) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mRouterDialog.dismiss();
//                }
//            }, 500);
//        }
//    }
//
//    private Gson mGson = new Gson();
//
//    @Override
//    public void onReceiveMessage(Action action, String message) {
//        switch (action.getCode()) {
//            case SEEK + REMOTE_CLIENT_CODE:
//            case TICKER:
//                SeekProcess seekProcess = mGson.fromJson(action.getInfo().toString(), SeekProcess.class);
//
////                mSeekBar.setMax(seekProcess.duration);
//                mSeekBar.setProgress(seekProcess.position);
//
//                mStart.setText(Utils.formatSecondTime(seekProcess.position));
////                mEnd.setText(Utils.formatSecondTime(seekProcess.duration));
//                break;
//            case RESUME + REMOTE_CLIENT_CODE:
//
//                print("播放");
//                break;
//            case PAUSE + REMOTE_CLIENT_CODE:
//
//                print("暂停");
//                break;
//            case VIDEO_INFO + REMOTE_CLIENT_CODE:
//
//                V video = mGson.fromJson(action.getInfo().toString(), V.class);
//                mSeekBar.setMax((int) video.duration);
//                mSeekBar.setProgress((int) video.position);
//
//                mStart.setText(Utils.formatSecondTime(video.position));
//                mEnd.setText(Utils.formatSecondTime(video.duration));
//                print("远程端返回在播视频信息:" + video.toString());
//                break;
//        }
//
//    }
//
//    private StringBuffer mBuffer = new StringBuffer();
//
//    public void onPlay(View view) {
//        if (!isConnecting()) return;
//        mRemotePlayer.resume();
//    }
//
//    public void onPause(View view) {
//        if (!isConnecting()) return;
//        mRemotePlayer.pause();
//    }
//
//    public void onStop(View view) {
//        if (!isConnecting()) return;
//        mRemotePlayer.stop();
//    }
//
//    public void onVolumeUp(View view) {
//        if (!isConnecting()) return;
//        mRemotePlayer.volume(1);
//    }
//
//    public void onVolumeDown(View view) {
//        if (!isConnecting()) return;
//        mRemotePlayer.volume(-1);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (isConnecting()) {
//            mAirPlay.close();
//            mRemotePlayer.destroy();
//        }
//        super.onDestroy();
//        mAirPlay.unregisterOnConnectListener(this);
//    }
//
//    private boolean isConnecting() {
//        return mAirPlay.isConnecting();
//    }
//
//    private void print(String message) {
//        mBuffer.append(message).append("\n");
//        mResult.setText(mBuffer.toString());
//        mScrollView.scrollTo(0, mResult.getBottom());
//    }
//
//}
