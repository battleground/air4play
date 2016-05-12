package bf.cloud.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import bf.cloud.BFMediaPlayerControllerVod;
import bf.cloud.android.playutils.BasePlayer;
import bf.cloud.android.playutils.BasePlayer.PLAYER_TYPE;
import bf.cloud.android.playutils.BasePlayer.RATIO_TYPE;
import bf.cloud.android.playutils.DecodeMode;
import bf.cloud.android.playutils.VodPlayer;
import bf.cloud.black_board_ui.R;

public class VodDemo extends AppCompatActivity {
    private final String TAG = VodDemo.class.getSimpleName();

    private VodPlayer mVodPlayer = null;
    private BFMediaPlayerControllerVod mMediaController = null;
    private String[] mUrls = {
            "servicetype=1&uid=23659214&fid=8F2FF293DBAD57BDE156A3082DABF3B7",
            "servicetype=1&uid=4995606&fid=D754D209A442A6787962AB1552FF9412",
            "servicetype=1&uid=23896155&fid=967E8957F7ACA54C7FACA0EE80EF4CCE",
            "servicetype=1&uid=11165311&fid=226ED66AD284488DA46DF1859D6CBAB6"};

    private int mVideoIndex = 0;
    private FrameLayout mTouchPanel = null;
    private EditText mInputUrl = null;
    private EditText mInputToken = null;
    private Toast mNotice = null;
    private long mHistory = -1;

    String video_url = "http://183.131.106.56:443/162BB27E455043AF8EDC91E7058B82F5987FF591.mp4?key=6AE79EA0ED1D9B6D7884149CF7DD8B576B6B5B820C226EE1FF79227A3F28DD14";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_vod);
        init();

    }

    private void init() {
        mVideoIndex = 0;
        mMediaController = (BFMediaPlayerControllerVod) findViewById(R.id.vod_media_controller_vod);
        mTouchPanel = (FrameLayout) findViewById(R.id.TouchPanel);
        mInputUrl = (EditText) findViewById(R.id.play_url);
        mInputToken = (EditText) findViewById(R.id.play_token);
        mVodPlayer = (VodPlayer) mMediaController.getPlayer();
        // 预设一个DataSource
        mVodPlayer.setDataSource(mUrls[0]);

        mTouchPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MotionEvent newEvent = MotionEvent.obtain(0, 0, event.getAction(), event.getX(), event.getY(), 0);
                mVodPlayer.onTouch(newEvent);

                return true;
            }
        });
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.change_port: {
                break;
            }
            case R.id.change_decode_mode: {
                String[] items = {"自动(ExoPlayer优先)", "软解", "系统原生解码(MediaPlayer)"};
                int checkedItem = -1;
                if (mVodPlayer.getDecodeMode() == DecodeMode.HARD) {
                    checkedItem = 0;
                } else if (mVodPlayer.getDecodeMode() == DecodeMode.SOFT) {
                    checkedItem = 1;
                } else
                    checkedItem = 2;
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(items, checkedItem, null)
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        mVodPlayer.stop();
                                        int position = ((AlertDialog) dialog)
                                                .getListView()
                                                .getCheckedItemPosition();
                                        if (position == 0) {
                                            mVodPlayer
                                                    .setDecodeMode(DecodeMode.HARD);
                                        } else if (position == 1) {
                                            mVodPlayer
                                                    .setDecodeMode(DecodeMode.SOFT);
                                        } else {
                                            mVodPlayer.setDecodeMode(DecodeMode.MEDIAPLYAER);
                                        }
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                break;
            }
            case R.id.change_player_type: {
                String[] items = {"普通播放器", "全景播放器"};
                int checkedItem = -1;
                if (mVodPlayer.getPlayerType() == PLAYER_TYPE.NORMAL) {
                    checkedItem = 0;
                } else if (mVodPlayer.getPlayerType() == PLAYER_TYPE.FULL_SIGHT) {
                    checkedItem = 1;
                }
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(items, checkedItem, null)
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        mVodPlayer.stop();
                                        int position = ((AlertDialog) dialog)
                                                .getListView()
                                                .getCheckedItemPosition();
                                        if (position == 0) {
                                            mVodPlayer
                                                    .setPlayerType(PLAYER_TYPE.NORMAL);
                                        } else if (position == 1) {
                                            mVodPlayer
                                                    .setPlayerType(PLAYER_TYPE.FULL_SIGHT);
                                        }
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                break;
            }
            case R.id.start: {
                mVodPlayer.stop();
                String inputUrl = mInputUrl.getText().toString();
                String inputToken = mInputToken.getText().toString();
                if (inputUrl != null && inputUrl.length() != 0)
                    mVodPlayer.setDataSource(inputUrl, inputToken);
                else {
                    mVodPlayer.setDataSource(mUrls[mVideoIndex], inputToken);
                    if (mNotice != null)
                        mNotice.cancel();
                    mNotice = Toast.makeText(this, "开始播放默认视频", Toast.LENGTH_SHORT);
                    mNotice.show();
                }
                mVodPlayer.start();
                break;
            }
            case R.id.stop:
                mVodPlayer.stop();
                break;
            case R.id.change_video: {
                mVideoIndex++;
                if (mVideoIndex > mUrls.length - 1)
                    mVideoIndex = 0;
                mVodPlayer.stop();
                mVodPlayer.setDataSource(mUrls[mVideoIndex]);
                mVodPlayer.start();
                if (mNotice != null)
                    mNotice.cancel();
                mNotice = Toast.makeText(this, "开始播放默认视频", Toast.LENGTH_SHORT);
                mNotice.show();
                break;
            }
            case R.id.pause:
                mVodPlayer.pause();
                break;
            case R.id.resume:
                mVodPlayer.resume();
                break;
            case R.id.seekto:
                mVodPlayer.seekTo(30000);
                break;
            case R.id.inc_volume:
                mVodPlayer.incVolume();
                break;
            case R.id.dec_volume:
                mVodPlayer.decVolume();
                break;
            case R.id.get_current_volume: {
                int value = mVodPlayer.getCurrentVolume();
                Toast.makeText(VodDemo.this, "" + value, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.get_max_volume: {
                int value = mVodPlayer.getMaxVolume();
                Toast.makeText(VodDemo.this, "" + value, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.get_cur_position: {
                long value = mVodPlayer.getCurrentPosition();
                Toast.makeText(VodDemo.this, "" + value, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.get_duration: {
                long value = mVodPlayer.getDuration();
                Toast.makeText(VodDemo.this, "" + value, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.auto_screen: {
                String[] items = {"是", "否"};
                int checkedItem = -1;
                if (mMediaController.getAutoChangeScreen()) {
                    checkedItem = 0;
                } else {
                    checkedItem = 1;
                }
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(items, checkedItem, null)
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        int position = ((AlertDialog) dialog)
                                                .getListView()
                                                .getCheckedItemPosition();
                                        if (position == 0) {
                                            mMediaController
                                                    .setAutoChangeScreen(true);
                                        } else if (position == 1) {
                                            mMediaController
                                                    .setAutoChangeScreen(false);
                                        }
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                break;
            }
            case R.id.changed_ratio: {
                String[] items = {"16:9", "4:3", "原视频输出"};
                int checkedItem = 0;
                if (mVodPlayer.getVideoAspectRatio() == RATIO_TYPE.TYPE_16_9) {
                    checkedItem = 0;
                } else if (mVodPlayer.getVideoAspectRatio() == RATIO_TYPE.TYPE_4_3) {
                    checkedItem = 1;
                } else if (mVodPlayer.getVideoAspectRatio() == RATIO_TYPE.TYPE_ORIGENAL) {
                    checkedItem = 2;
                }
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(items, checkedItem, null)
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        int position = ((AlertDialog) dialog)
                                                .getListView()
                                                .getCheckedItemPosition();
                                        if (position == 0) {
                                            mVodPlayer.setVideoAspectRatio(BasePlayer.RATIO_TYPE.TYPE_16_9);
                                        } else if (position == 1) {
                                            mVodPlayer.setVideoAspectRatio(BasePlayer.RATIO_TYPE.TYPE_4_3);
                                        } else if (position == 2) {
                                            mVodPlayer.setVideoAspectRatio(BasePlayer.RATIO_TYPE.TYPE_ORIGENAL);
                                        }
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                break;
            }
            default:
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
    protected void onStart() {
        if (mHistory > 0) {
            mVodPlayer.start((int) mHistory);
            mHistory = -1;
        }
        super.onStart();
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
