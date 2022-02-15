package com.liyuliang.mytime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.bean.Version;
import com.liyuliang.mytime.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 软件版本更新日志的适配器
 * Created at 2019/3/1 12:37
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class VersionLogAdapter extends RecyclerView.Adapter<VersionLogAdapter.VersionLogViewHolder> {

    private final List<Version> list;

    public VersionLogAdapter(List<Version> lv) {
        list = lv;
    }

    @Override
    @NotNull
    public VersionLogViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_version, viewGroup, false);
        VersionLogViewHolder versionLogViewHolder = new VersionLogViewHolder(view);
        versionLogViewHolder.tvVersionName = view.findViewById(R.id.tv_versionName);
        versionLogViewHolder.tvVersionTime = view.findViewById(R.id.tv_versionTime);
        versionLogViewHolder.tvVersionLog = view.findViewById(R.id.tv_versionLog);
        return versionLogViewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull VersionLogViewHolder viewHolder, int position) {
        Version version = list.get(position);
        viewHolder.tvVersionName.setText(version.getVersionName());
        viewHolder.tvVersionTime.setText(TimeUtils.getSimpleDate(version.getCreateTime()));
        viewHolder.tvVersionLog.setText(version.getVersionLog());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class VersionLogViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVersionName, tvVersionTime, tvVersionLog;

        private VersionLogViewHolder(View itemView) {
            super(itemView);
        }
    }

}
