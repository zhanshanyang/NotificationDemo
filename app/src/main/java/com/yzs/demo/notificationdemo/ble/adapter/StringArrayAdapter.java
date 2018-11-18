package com.yzs.demo.notificationdemo.ble.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yzs.demo.notificationdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringArrayAdapter extends RecyclerView.Adapter<StringArrayAdapter.MyViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> mData;

    public StringArrayAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void updateData(Map<String, BluetoothDevice> data) {
        if (data != null) {
            mData.clear();
            for (Map.Entry<String, BluetoothDevice> entry : data.entrySet()) {
                mData.add(entry.getValue());
            }
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ble_str, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BluetoothDevice device = mData.get(position);
        if (device != null) {
            holder.tvDeviceName.setText(device.getName());
            holder.tvDeviceAddr.setText(device.getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvDeviceName;
        TextView tvDeviceAddr;

        MyViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            tvDeviceAddr = itemView.findViewById(R.id.tv_device_addr);
        }
    }
}
