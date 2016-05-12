package com.dlna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.dayu.airplay.R;

import java.util.ArrayList;

/**
 * DLNA
 * 媒体设备列表Dialog
 */
public class RouterListDialog extends Dialog implements View.OnClickListener,
        AdapterView.OnItemClickListener, Discover.OnDiscoverListener {

    /**
     * 设备列表项目点击事件
     */
    public interface OnItemClickListener {
        /**
         * 设备列表项目点击事件，增加本身对话框的访问
         *
         * @param dialog   当前设备列表对话框
         * @param parent   设备列表List
         * @param view
         * @param position
         */
        void onItemClick(DialogInterface dialog, AdapterView<?> parent, View view, int position);
    }

    private ListView mListView;
    private OnItemClickListener mOnItemClickListener;
    private TextView mTitleView;
    private TextView mMessageText;
    private DevicesAdapter adapter;
    private int mPosition;

    public RouterListDialog(Context context) {
        super(context);
        init(context);
    }

    public RouterListDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected RouterListDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
//        View view = LayoutInflater.from(context).inflate(R.layout.air_router_list_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.air_router_list_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        findViewById(R.id.cancel).setOnClickListener(this);
        mTitleView = (TextView) findViewById(android.R.id.title);
        mMessageText = (TextView) findViewById(R.id.MessageView);

        mListView = (ListView) findViewById(R.id.ListView);
//        mListView.setItemsCanFocus(false);
        mListView.setEmptyView(mMessageText);
        mListView.setOnItemClickListener(this);

        adapter = new DevicesAdapter(context, null);
        mListView.setAdapter(adapter);
        Discover.creator(getContext());
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitleView.setText(titleId);
    }

    public DevicesAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void show() {
        super.show();

        mMessageText.setText("扫描中...");
//        timer.start();

        Discover.getInstance().registerDiscoverListener(this);
        Discover.getInstance().start();
        adapter.setData(Discover.getInstance().getDevicesList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dismiss() {
        Discover.getInstance().registerDiscoverListener(null);
        Discover.getInstance().stop();
        super.dismiss();
        adapter.notifyDataSetChanged();
    }

    /**
     * 关闭对话框事件
     *
     * @param v 关闭按钮
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            cancel();
        }
    }

    /**
     * 设置设备列表项目点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 预留事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPosition = position;
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(this, parent, view, position);
        }
    }

    private int count = 0;
    private CountDownTimer timer = new CountDownTimer(3 * 1000, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (count < 3) {
                mMessageText.setText(mMessageText.getText().toString() + ".");
                count++;
            } else {
                mMessageText.setText("扫描中");
                count = 0;
            }
        }

        @Override
        public void onFinish() {
            Discover.getInstance().start();
            mMessageText.setText("扫描完成！");
        }
    };

    @Override
    public void onDiscoveryChanged(ArrayList<Device> list) {
        adapter.setData(list);
        adapter.notifyDataSetChanged();

        if (list == null) {
            mMessageText.setText("Wi-Fi已断开");
        } else if (list.size() == 0) {
            mMessageText.setText("媒体扫描中...");
        }
    }

}
