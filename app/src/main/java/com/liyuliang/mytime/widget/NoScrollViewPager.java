package com.liyuliang.mytime.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.youth.banner.Banner;

/**
 * 自定义的不可滑动的ViewPager
 * Created at 2018/11/28 13:56
 *
 * @author LiYuliang
 * @version 1.0
 */

public class NoScrollViewPager extends ViewPager {

    private boolean noScroll = false;
    private boolean mIntercept = false;
    private float mDownPosX, mDownPosY;

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        /* return false;//super.onTouchEvent(arg0); */
        if (noScroll) {
            return false;
        } else {
            return super.onTouchEvent(arg0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (noScroll) {
            return false;
        } else if (mIntercept) {
            final float x = ev.getRawX();
            final float y = ev.getRawY();
            final int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownPosX = x;
                    mDownPosY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float deltaX = Math.abs(x - mDownPosX);
                    final float deltaY = Math.abs(y - mDownPosY);
                    // 这里是够拦截的判断依据是左右滑动
                    if (deltaX > deltaY) {
                        return false;
                    }
                default:
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setIntercept(boolean value) {
        mIntercept = value;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && (v instanceof Banner || v instanceof ViewPager)) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}
