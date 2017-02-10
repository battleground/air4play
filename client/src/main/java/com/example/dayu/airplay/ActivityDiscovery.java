//package com.example.dayu.airplay;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.abooc.util.Debug;
//import com.dlna.Device;
//
//import java.util.ArrayList;
//
//public class ActivityDiscovery extends Activity {
//
//
//    class Adapter extends BaseAdapter {
//
//        private ArrayList<Device> list;
//
//        public void update(ArrayList<Device> list) {
//            this.list = list;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return list == null ? 0 : list.size();
//        }
//
//        @Override
//        public Device getItem(int position) {
//            return list.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(ActivityDiscovery.this).inflate(android.R.layout.simple_list_item_2, null);
//            }
//
//            TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
//            TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);
//
//            Device device = getItem(position);
//
//            textView1.setText(device.getDeviceName());
//            textView2.setText(device.getIp());
//
//            return convertView;
//        }
//
//        public void clear() {
//            if(list != null){
//                list.clear();
//                notifyDataSetChanged();
//            }
//        }
//    }
//
//    private WiFi mWiFi;
//    private Button mStartButton;
//    private Button mStopButton;
//    private Button mMessageButton;
//    private Adapter mAdapter;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_discovery);
//
//        mAdapter = new Adapter();
//
//        mStartButton = (Button) findViewById(R.id.Start);
//        mStopButton = (Button) findViewById(R.id.Stop);
//        mMessageButton = (Button) findViewById(R.id.Message);
//
//        ListView iListView = (ListView) findViewById(R.id.ListView);
//        iListView.setAdapter(mAdapter);
//
////        Discover.creator(this);
////        Discover.getInstance().registerDiscoverListener(new Discover.OnDiscoverListener() {
////            @Override
////            public void onDiscoveryChanged(ArrayList<Device> list) {
////                if (list == null) {
////                    mAdapter.clear();
////                    mMessageButton.setText("WiFi已关闭");
////                } else {
////                    mMessageButton.setEnabled(true);
////                    mMessageButton.setText("扫描中..." + list.size() + "个设备"
////                            + "\n\n<点击刷新>");
////                    mAdapter.update(list);
////                }
////            }
////        });
//
//
//        mWiFi = new WiFi();
//        registerReceiver(mWiFi, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
//    }
//
//    public void onStart(View view) {
//        mMessageButton.setText("扫描中...");
////        Discover.getInstance().start();
//        mStartButton.setEnabled(false);
//        mStopButton.setEnabled(true);
//    }
//
//    public void onRestart(View view) {
//        mMessageButton.setText("重新扫描...");
//        mMessageButton.setEnabled(false);
//        mAdapter.clear();
////        Discover.getInstance().restart();
//        mStartButton.setEnabled(false);
//        mStopButton.setEnabled(true);
//    }
//
//    public void onStop(View view) {
//        mMessageButton.setText("扫描已停止");
////        Discover.getInstance().stop();
//        mStartButton.setEnabled(true);
//        mStopButton.setEnabled(false);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mWiFi);
//    }
//
//    class WiFi extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Debug.anchor(intent.toString());
//            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
//
//                switch (wifiState) {
//                    case WifiManager.WIFI_STATE_DISABLING:
//                        mMessageButton.setText("WiFi 关闭中...");
//                        break;
//                    case WifiManager.WIFI_STATE_DISABLED:
//                        mMessageButton.setText("WiFi 不可用");
//                        mStartButton.setEnabled(true);
//                        mStopButton.setEnabled(false);
//                        break;
//                    case WifiManager.WIFI_STATE_ENABLING:
//                        mMessageButton.setText("WiFi 开启...");
//                        break;
//                    case WifiManager.WIFI_STATE_ENABLED:
//                        mMessageButton.setText("WiFi 可用，扫描中...");
//                        mStartButton.setEnabled(false);
//                        mStopButton.setEnabled(true);
//                        break;
//                }
//            }
//        }
//
//    }
//
//}
