package com.dlna;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
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
public class RoutersDialog extends Dialog implements View.OnClickListener,
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
    private WiFi mWiFi;

    public RoutersDialog(Context context) {
        super(context);
        init(context);
    }

    public RoutersDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected RoutersDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mWiFi = new WiFi();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.air_router_list_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        findViewById(R.id.cancel).setOnClickListener(this);
        mTitleView = (TextView) findViewById(android.R.id.title);
        mMessageText = (TextView) findViewById(R.id.MessageView);
        findViewById(R.id.retry).setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.ListView);
        mListView.setEmptyView(mMessageText);
        mListView.setOnItemClickListener(this);

        adapter = new DevicesAdapter(context, null);
        mListView.setAdapter(adapter);
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
        setTitle("选择设备");
        getContext().registerReceiver(mWiFi, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        Discover.getInstance().registerDiscoverListener(this);
        onDiscoveryChanged(Discover.getInstance().getDevicesList());
    }

    @Override
    public void dismiss() {
        Discover.getInstance().registerDiscoverListener(null);
        super.dismiss();
        getContext().unregisterReceiver(mWiFi);
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
        } else if (id == R.id.retry) {
            mMessageText.setText("扫描中...");
            onDiscoveryChanged(new ArrayList<Device>());
            Discover.getInstance().restart();
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
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(this, parent, view, position);
        }
    }

    @Override
    public void onDiscoveryChanged(ArrayList<Device> list) {
        if (list == null) {
            mMessageText.setText("没有发现可用设备");
        } else {
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }
    }

    private class WiFi extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLING:
                        mMessageText.setText("WiFi 关闭中...");
                        findViewById(R.id.retry).setEnabled(false);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        mMessageText.setText("WiFi 不可用");
                        findViewById(R.id.retry).setEnabled(false);
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        mMessageText.setText("WiFi 开启...");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        mMessageText.setText("扫描中...");
                        findViewById(R.id.retry).setEnabled(true);
                        break;
                }
            }
        }

    }

}
