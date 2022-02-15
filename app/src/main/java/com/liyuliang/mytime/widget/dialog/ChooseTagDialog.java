package com.liyuliang.mytime.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.widget.MyRecyclerView;
import com.liyuliang.mytime.widget.RecyclerViewDivider;

import java.util.List;

/**
 * 选择标签的弹窗
 * Created at 2019/9/8 14:12
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ChooseTagDialog extends Dialog {

    private Context context;
    private String title;
    private List<String> tagList;
    private TimbreAdapter timbreAdapter;
    private OnItemClickListener mListener;

    public ChooseTagDialog(Context context,String title, List<String> tagList) {
        super(context);
        this.context = context;
        this.title = title;
        this.tagList = tagList;
        initView();
    }

    //初始化View
    private void initView() {
        setContentView(R.layout.dialog_choose_tag);
        TextView title_name = findViewById(R.id.title_name);
        title_name.setText(title);

        MyRecyclerView rvTag = findViewById(R.id.rvTag);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rvTag.setLayoutManager(linearLayoutManager1);
        rvTag.addItemDecoration(new RecyclerViewDivider(context, LinearLayoutManager.HORIZONTAL, 3, ContextCompat.getColor(context, R.color.transparent)));
        timbreAdapter = new TimbreAdapter(tagList);

        timbreAdapter.setOnItemClickListener(position -> {
            if (mListener != null) {
                mListener.onItemClick(position);
            }
        });

        rvTag.setAdapter(timbreAdapter);
        initWindow();
    }

    /**
     * 添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
            lp.width = (int) (d.widthPixels * 0.9); //宽度为屏幕80%
            lp.gravity = Gravity.CENTER;  //中央居中
            dialogWindow.setAttributes(lp);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class TimbreAdapter extends RecyclerView.Adapter<TimbreAdapter.MalfunctionViewHolder> {

        private List<String> stringList;
        private OnItemClickListener mListener;

        public TimbreAdapter(List<String> stringList) {
            this.stringList = stringList;
        }

        @Override
        public MalfunctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
            return new MalfunctionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MalfunctionViewHolder viewHolder, int position) {
            viewHolder.tv_tag.setText(stringList.get(position));
            viewHolder.itemView.setOnClickListener((v) -> {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        public class MalfunctionViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_tag;

            private MalfunctionViewHolder(View itemView) {
                super(itemView);
                tv_tag = itemView.findViewById(R.id.tv_tag);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

    }


}

