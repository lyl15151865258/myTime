package com.liyuliang.mytime.activity.loginregister;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseActivity;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.fragment.guide.fragment.GuideFragmentForth;
import com.liyuliang.mytime.fragment.guide.fragment.GuideFragmentOne;
import com.liyuliang.mytime.fragment.guide.fragment.GuideFragmentThree;
import com.liyuliang.mytime.fragment.guide.fragment.GuideFragmentTwo;
import com.liyuliang.mytime.pageindicator.PageIndicatorView;
import com.liyuliang.mytime.pageindicator.animation.type.AnimationType;
import com.liyuliang.mytime.pageindicator.draw.data.Orientation;
import com.liyuliang.mytime.pageindicator.draw.data.RtlMode;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 * Created at 2019/6/14 11:17
 *
 * @author LiYuliang
 * @version 1.0
 */

public class GuideActivity extends BaseActivity {

    private List<Fragment> fragmentList;
    private PageIndicatorView pageIndicatorView;
    private Button btnStartUsing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        fragmentList = new ArrayList<>();
        fragmentList.add(new GuideFragmentOne());
        fragmentList.add(new GuideFragmentTwo());
        fragmentList.add(new GuideFragmentThree());
        fragmentList.add(new GuideFragmentForth());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        btnStartUsing = findViewById(R.id.btnStartUsing);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        btnStartUsing.setOnClickListener(onClickListener);
        updateIndicator();
    }

    private final View.OnClickListener onClickListener = (v) -> {
        // 开始使用
        SPHelper.save("isFirstUse", false);
        openActivity(MainActivity.class);
        ActivityController.finishActivity(this);
    };

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, findViewById(R.id.myToolbar));
    }

    /**
     * ViewPager适配器
     */
    private final FragmentStatePagerAdapter viewPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    };

    /**
     * 页面切换监听
     */
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (i == fragmentList.size() - 1) {
                btnStartUsing.setVisibility(View.VISIBLE);
            } else {
                btnStartUsing.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 更新指示器样式
     */
    private void updateIndicator() {
        pageIndicatorView.setRadius(5);
        pageIndicatorView.setSelectedColor(ContextCompat.getColor(this, R.color.colorGreenPrimary));
        pageIndicatorView.setUnselectedColor(ContextCompat.getColor(this, R.color.gray));
        pageIndicatorView.setAnimationType(AnimationType.SWAP);
        pageIndicatorView.setOrientation(Orientation.HORIZONTAL);
        pageIndicatorView.setRtlMode(RtlMode.Off);
        pageIndicatorView.setInteractiveAnimation(false);
        pageIndicatorView.setAutoVisibility(true);
        pageIndicatorView.setFadeOnIdle(false);
    }

}
