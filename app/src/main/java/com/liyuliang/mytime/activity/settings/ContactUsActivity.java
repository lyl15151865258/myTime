package com.liyuliang.mytime.activity.settings;

import android.os.Bundle;
import android.view.View;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.widget.MyToolbar;

/**
 * 联系我们
 * Created by Li Yuliang on 2018/01/17.
 *
 * @author LiYuliang
 * @version 2018/01/17
 */

public class ContactUsActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.ContactUs, R.drawable.back_white, -1, -1, -1, onClickListener);


    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        }
    };

}
