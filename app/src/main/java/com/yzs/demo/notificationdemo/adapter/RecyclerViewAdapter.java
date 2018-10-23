package com.yzs.demo.notificationdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzs.demo.notificationdemo.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private int itemsSize = 30;

    public RecyclerViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvName.setText("Item name's position:" + (position + 1));
    }

    @Override
    public int getItemCount() {
        return itemsSize;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgName;
        private TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgName = itemView.findViewById(R.id.img_name);
            tvName = itemView.findViewById(R.id.tv_name);
        }

    }
}
