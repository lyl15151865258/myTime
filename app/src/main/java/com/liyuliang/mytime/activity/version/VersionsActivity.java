package com.liyuliang.mytime.activity.version;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.adapter.VersionLogAdapter;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.bean.Version;
import com.liyuliang.mytime.bean.VersionLog;
import com.liyuliang.mytime.constant.ApkInfo;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.ApkUtils;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.widget.MyToolbar;
import com.liyuliang.mytime.widget.RecyclerViewDivider;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * App历史版本页面
 * Created at 2019/3/1 11:15
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class VersionsActivity extends SwipeBackActivity {

    private Context mContext;

    private RecyclerView lvVersionLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.VersionView, R.drawable.back_white, -1, -1, -1, onClickListener);
        ((TextView) findViewById(R.id.tv_version)).setText(ApkUtils.getVersionName(this));

        lvVersionLog = findViewById(R.id.lv_versionLog);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvVersionLog.setLayoutManager(linearLayoutManager);
        lvVersionLog.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, 2, ContextCompat.getColor(this, R.color.gray_slight)));

        JsonObject params = new JsonObject();
        params.addProperty("apkTypeId", ApkInfo.APK_TYPE_ID_MyTime);
        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().getVersionLog(params);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast(getString(R.string.NetworkUnavailable));
                } else {
                    showLoadingDialog(mContext, getString(R.string.Searching), true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(@NonNull Result result) {
                cancelDialog();
                super.onNext(result);
                if (result.getCode() == ErrorCode.SUCCESS) {
                    VersionLog versionLog = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), VersionLog.class);
                    List<Version> versionList = versionLog.getVersionInfoList();
                    VersionLogAdapter versionLogAdapter = new VersionLogAdapter(versionList);
                    lvVersionLog.setAdapter(versionLogAdapter);
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast("查询失败");
                }
            }
        });
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        }
    };

}
