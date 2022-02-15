package com.liyuliang.mytime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.bean.Gender;

import java.util.List;

/**
 * 选择性别的适配器
 * Created at 2018/11/28 13:38
 *
 * @author LiYuliang
 * @version 1.0
 */

public class GenderAdapter extends RecyclerView.Adapter<GenderAdapter.ListViewHolder> {

    private final List<Gender> list;
    private OnItemClickListener mItemClickListener;

    public GenderAdapter(List<Gender> lv) {
        list = lv;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gender, viewGroup, false);
        ListViewHolder listViewHolder = new ListViewHolder(view);
        listViewHolder.tvGender = view.findViewById(R.id.tvGender);
        listViewHolder.ivSelect = view.findViewById(R.id.iv_select);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        Gender gender = list.get(position);
        holder.tvGender.setText(gender.getGender());
        if (gender.isSelected()) {
            holder.ivSelect.setImageResource(R.drawable.checkbox_selected);
        } else {
            holder.ivSelect.setImageResource(R.drawable.checkbox_normal);
        }
        View itemView = holder.itemView;
        if (mItemClickListener != null) {
            itemView.setOnClickListener((v) -> mItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGender;
        private ImageView ivSelect;

        private ListViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

}
