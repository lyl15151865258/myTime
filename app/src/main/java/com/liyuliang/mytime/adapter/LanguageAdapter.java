package com.liyuliang.mytime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.bean.Language;
import com.liyuliang.mytime.utils.LanguageUtil;
import com.liyuliang.mytime.utils.ViewUtils;
import com.liyuliang.mytime.widget.textview.MyTextView;

import java.util.List;

/**
 * 选择语言的适配器
 * Created at 2018/11/28 13:38
 *
 * @author LiYuliang
 * @version 1.0
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ListViewHolder> {

    private final Context mContext;
    private final List<Language> list;
    private OnItemClickListener mItemClickListener;

    public LanguageAdapter(Context context, List<Language> lv) {
        mContext = context;
        list = lv;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_language, viewGroup, false);
        ListViewHolder listViewHolder = new ListViewHolder(view);
        listViewHolder.tvLanguage = view.findViewById(R.id.tv_language);
        listViewHolder.ivSelect = view.findViewById(R.id.iv_select);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        Language language = list.get(position);
        holder.tvLanguage.setText(language.getLanguageName());
        ViewUtils.updateViewLanguage(holder.tvLanguage);
        if (LanguageUtil.getLanguageLocal(mContext).equals(language.getLanguageCode())) {
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

        private MyTextView tvLanguage;
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
