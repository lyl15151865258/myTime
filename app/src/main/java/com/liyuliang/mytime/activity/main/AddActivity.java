package com.liyuliang.mytime.activity.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.TimeUtils;
import com.liyuliang.mytime.widget.MyEditText;
import com.liyuliang.mytime.widget.MyToolbar;
import com.liyuliang.mytime.widget.textview.MyTextView;

/**
 * 通用新增页面
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class AddActivity extends SwipeBackActivity {

    private Context mContext;
    private MyToolbar toolbar;
    private MyTextView tvDatetime;
    private MyEditText etEventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mContext = this;
        toolbar = findViewById(R.id.myToolbar);
        int title = getIntent().getIntExtra("addTitle", R.string.app_name);
        toolbar.initToolBar(this, toolbar, title, R.drawable.back_white, -1, -1, R.string.save, onClickListener);
        tvDatetime = findViewById(R.id.tvDatetime);
        tvDatetime.setOnClickListener(onClickListener);
        tvDatetime.setText(TimeUtils.getCurrentDateTime());

    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.toolbarRight) {
            // 保存

        } else if (v.getId() == R.id.tvDatetime) {
            // 修改时间
            TimeUtils.showTimePicker(mContext, (TextView) v, "yyyy-MM-dd HH:mm:ss", true, true, true, true, true, true);
        }
    };

}