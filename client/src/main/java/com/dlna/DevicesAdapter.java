//package com.dlna;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//
//import com.example.dayu.airplay.R;
//
//import java.util.List;
//
///**
// * DLNA设备列表Adapter
// */
//public class DevicesAdapter extends BaseAdapter {
//
//    private List<Device> mData;
//    private final Context mContext;
//
//    /**
//     * DLNA设备列表Adapter
//     *
//     * @param context
//     * @param objects Nullable
//     */
//    public DevicesAdapter(Context context, List<Device> objects) {
//        this.mContext = context;
//        this.mData = objects;
//    }
//
//    public void setData(List<Device> objects) {
//        this.mData = objects;
//    }
//
//    @Override
//    public int getCount() {
//        return mData == null ? 0 : mData.size();
//    }
//
//    @Override
//    public Device getItem(int position) {
//        return mData.get(position % mData.size());
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.air_router_list_dialog_list_item, null);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        Device device = getItem(position);
//        holder.bindData(device);
////        holder.changeLoad(device);
//        return convertView;
//    }
//
//    /**
//     * Item UI构建器
//     */
//    private static class ViewHolder {
//
//        private TextView name;
//        private TextView ip;
//        private ImageView warning;
//        private ProgressBar progress;
//
//        private ViewHolder(View convertView) {
//            name = (TextView) convertView.findViewById(R.id.name);
//            ip = (TextView) convertView.findViewById(R.id.Ip);
//            warning = (ImageView) convertView.findViewById(R.id.warning);
//            progress = (ProgressBar) convertView.findViewById(R.id.progress);
//        }
//
//        private void bindData(Device device) {
//            if (device != null) {
//                name.setText(device.getDeviceName());
//                ip.setText(device.getIp());
//            }
//        }
//
//        /**
//         * 更新显示连接状态
//         *
//         * @param device 当前连接的设备，信息中包括连接状态
//         */
//        private void changeLoad(Device device) {
//            if (device.isNormal()) {
//                warning.setVisibility(View.INVISIBLE);
//                progress.setVisibility(View.INVISIBLE);
//                return;
//            }
//
//            if (device.isConnecting()) {
//                progress.setVisibility(View.VISIBLE);
//                return;
//            } else {
//                progress.setVisibility(View.INVISIBLE);
//            }
//
//            if (device.isConnected()) {
//                warning.setVisibility(View.VISIBLE);
//                warning.setImageResource(R.drawable.ic_cast_dark);
//            } else if (device.isFailure()) {
////                warning.setVisibility(View.VISIBLE);
////                warning.setImageResource(R.drawable.ic_close_light);
////            } else {
//                warning.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//}