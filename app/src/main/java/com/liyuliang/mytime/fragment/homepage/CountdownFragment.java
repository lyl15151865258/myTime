package com.liyuliang.mytime.fragment.homepage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseFragment;
import com.liyuliang.mytime.adapter.CountdownAdapter;
import com.liyuliang.mytime.bean.Countdown;
import com.liyuliang.mytime.widget.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * 倒计时页面
 * Created at 2019/1/21 13:57
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class CountdownFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;
    private CountdownAdapter countdownAdapter;
    private List<Countdown> countdownList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_countdown, container, false);

        //菜单网格
        RecyclerView reCountdown = view.findViewById(R.id.reCountdown);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reCountdown.setLayoutManager(linearLayoutManager);
        reCountdown.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 2, ContextCompat.getColor(mContext, R.color.gray_slight)));
        countdownList = new ArrayList<>();
        addData();
        return view;
    }

    @Override
    public void lazyLoad() {

    }

    private void addData() {

    }

}
