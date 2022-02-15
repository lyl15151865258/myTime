package com.liyuliang.mytime.fragment.guide.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.BaseFragment;

/**
 * 引导页面三
 * Created at 2019/6/14 18:28
 *
 * @author LiYuliang
 * @version 1.0
 */

public class GuideFragmentThree extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_guide_three, container, false);
    }

    @Override
    public void lazyLoad() {

    }

}
