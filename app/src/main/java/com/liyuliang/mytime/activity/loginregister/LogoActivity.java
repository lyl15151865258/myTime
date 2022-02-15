package com.liyuliang.mytime.activity.loginregister;

import android.os.Bundle;
import android.view.KeyEvent;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseActivity;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.StatusBarUtil;

/**
 * Logo页面
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class LogoActivity extends BaseActivity {

    private boolean isFirstUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        isFirstUse = SPHelper.getBoolean("isFirstUse", true);
        open();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, findViewById(R.id.myToolbar));
    }

    /**
     * 处理没有广告，直接跳转
     */
    private void open() {
        // 如果用户是第一次使用，则直接调转到引导界面
        if (isFirstUse) {
            openActivity(GuideActivity.class);
        } else {
            openActivity(MainActivity.class);
        }
        ActivityController.finishActivity(this);
    }

    /**
     * Logo页面不允许退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
