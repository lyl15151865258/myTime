package com.liyuliang.mytime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.bean.Countdown;
import com.liyuliang.mytime.widget.MyRecyclerView;

import java.util.List;

/**
 * 员工动态的适配器
 * Created by LiYuliang on 2018/4/11.
 *
 * @author LiYuliang
 * @version 2018/4/11
 */

public class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.CountdownViewHolder> {

    private Context mContext;
    private List<Countdown> list;
    private OnItemClickListener mListener;

    public CountdownAdapter(Context mContext, List<Countdown> lv) {
        this.mContext = mContext;
        list = lv;
    }

    @NonNull
    @Override
    public CountdownViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_countdown, viewGroup, false);
        CountdownViewHolder countdownViewHolder = new CountdownViewHolder(view);

        return countdownViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CountdownViewHolder viewHolder, int position) {
        Countdown dynamic = list.get(position);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CountdownViewHolder extends RecyclerView.ViewHolder {

        private CountdownViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        /**
         * item点击事件
         *
         * @param view     被点击的item控件
         * @param position 被点击的位置
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
