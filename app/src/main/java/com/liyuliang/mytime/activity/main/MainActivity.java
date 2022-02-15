package com.liyuliang.mytime.activity.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.BaseActivity;
import com.liyuliang.mytime.fragment.homepage.AnniversaryFragment;
import com.liyuliang.mytime.fragment.homepage.CountdownFragment;
import com.liyuliang.mytime.fragment.homepage.ScheduleFragment;
import com.liyuliang.mytime.fragment.homepage.SettingsFragment;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.widget.MyToolbar;
import com.liyuliang.mytime.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * @author LiYuliang
 * @version 1.0
 * @description 主页面
 * Created at 2021-12-23 16:07
 */
public class MainActivity extends BaseActivity {

    private NoScrollViewPager viewPager;
    private List<LinearLayout> menus;
    private List<Object> titles;
    private MyToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d("TimeRecord", "进入MainActivity时间戳：" + System.currentTimeMillis());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.countdown, -1, -1, R.drawable.add_toolbar, -1, onClickListener);
        titles = new ArrayList<>();
        menus = new ArrayList<>();
        initPage();
    }

    private void initPage() {
        titles.clear();
        menus.clear();
        // 判断是否是管理员，管理员多一个Fragment页面
        titles.add(R.string.countdown);
        titles.add(R.string.anniversary);
        titles.add(R.string.schedule);
        titles.add(R.string.settings);
        menus.add(findViewById(R.id.ll_a));
        menus.add(findViewById(R.id.ll_b));
        menus.add(findViewById(R.id.ll_c));
        menus.add(findViewById(R.id.ll_d));
        for (LinearLayout linearLayout : menus) {
            linearLayout.setOnClickListener(onClickListener);
        }

        viewPager = findViewById(R.id.viewPager);
        //设置页面可以左右滑动
        viewPager.setNoScroll(false);
        //拦截左右滑动事件
        viewPager.setIntercept(false);
        viewPager.setAdapter(viewPagerAdapter);
        //设置Fragment预加载
        viewPager.setOffscreenPageLimit(menus.size());
        // 默认选中第一个
        menus.get(0).setSelected(true);
        //viewPager添加滑动监听，用于控制TextView的展示
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (LinearLayout menu : menus) {
                    menu.setSelected(false);
                }
                menus.get(position).setSelected(true);
                toolbar.setTitle(titles.get(position));
                if (position == menus.size() - 1) {
                    toolbar.setRightButtonImage(-1);
                } else {
                    toolbar.setRightButtonImage(R.drawable.add_toolbar);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private final View.OnClickListener onClickListener = (view) -> {
        if (view.getId() == R.id.ll_a || view.getId() == R.id.ll_b || view.getId() == R.id.ll_c || view.getId() == R.id.ll_d) {
            for (int i = 0; i < menus.size(); i++) {
                menus.get(i).setSelected(false);
                menus.get(i).setTag(i);
            }
            //设置选择效果
            view.setSelected(true);
            //参数false代表瞬间切换，true表示平滑过渡
            viewPager.setCurrentItem((Integer) view.getTag(), false);
        } else if (view.getId() == R.id.toolbarRight) {
            Intent intent = null;
            switch (viewPager.getCurrentItem()) {
                case 0:
                    // 倒计时页面
                    intent = new Intent(this, AddActivity.class);
                    intent.putExtra("addTitle", R.string.addCountdown);
                    break;
                case 1:
                    // 纪念日页面
                    intent = new Intent(this, AddActivity.class);
                    intent.putExtra("addTitle", R.string.addAnniversary);
                    break;
                case 2:
                    // 日程表页面
                    intent = new Intent(this, AddActivity.class);
                    intent.putExtra("addTitle", R.string.addSchedule);
                    break;
                default:
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
        }
    };

    /**
     * ViewPager适配器
     */
    private final FragmentStatePagerAdapter viewPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return menus.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CountdownFragment();
                case 1:
                    return new AnniversaryFragment();
                case 2:
                    return new ScheduleFragment();
                case 3:
                    return new SettingsFragment();
                default:
                    break;
            }
            return new CountdownFragment();
        }
    };

    /**
     * 返回键拦截，如果当前显示的不是HomeFragment，则切换到HomeFragment
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0, false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}