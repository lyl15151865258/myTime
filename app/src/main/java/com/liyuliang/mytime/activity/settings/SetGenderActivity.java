package com.liyuliang.mytime.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.adapter.GenderAdapter;
import com.liyuliang.mytime.bean.Gender;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.bean.UserOperateResult;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.widget.MyToolbar;
import com.liyuliang.mytime.widget.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 选择性别页面
 * Created at 2018/11/28 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class SetGenderActivity extends SwipeBackActivity {

    private Context mContext;
    private List<Gender> genderList;
    private GenderAdapter genderAdapter;
    private int flag, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gender);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.ModifyGender, R.drawable.back_white, -1, -1, -1, onClickListener);
        RecyclerView rvGender = findViewById(R.id.rvGender);
        genderList = new ArrayList<>();
        addGenders();
        //垂直线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvGender.setLayoutManager(linearLayoutManager);
        rvGender.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(this, R.color.gray_slight)));
        genderAdapter = new GenderAdapter(genderList);
        rvGender.setAdapter(genderAdapter);
        genderAdapter.setOnItemClickListener((view, position) -> {
            for (int i = 0; i < genderList.size(); i++) {
                genderList.get(i).setSelected(i == position);
            }
            genderAdapter.notifyDataSetChanged();
        });
        findViewById(R.id.btnModify).setOnClickListener(onClickListener);
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.btnModify) {
            modifyGender();
        }
    };

    /**
     * 添加性别种类
     */
    private void addGenders() {
        flag = getIntent().getIntExtra("flag", 0);
        User user = getIntent().getParcelableExtra("user");
        userId = user.getId();
        genderList.add(new Gender("男", false));
        genderList.add(new Gender("女", false));
        genderList.add(new Gender("其他", false));
        int genderId = user.getId();
        for (int i = 0; i < genderList.size(); i++) {
            if (i + 1 == genderId) {
                genderList.get(i).setSelected(true);
            }
        }
    }

    /**
     * 修改性别
     */
    private void modifyGender() {
        String key = "Gender";
        int genderId = 0;
        for (int i = 0; i < genderList.size(); i++) {
            if (genderList.get(i).isSelected()) {
                genderId = i + 1;
            }
        }
        if (genderId == 0) {
            showToast("请选择性别");
        } else {
            JsonObject params = new JsonObject();
            params.addProperty("key", key);
            params.addProperty("value", genderId);
            params.addProperty("userId", String.valueOf(userId));

            Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().updateInfo(params);
            resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

                @Override
                public void onSubscribe(Disposable d) {
                    //接下来可以检查网络连接等操作
                    if (!NetworkUtil.isNetworkAvailable(mContext)) {
                        showToast("当前网络不可用，请检查网络");
                    } else {
                        showLoadingDialog(mContext, "更新中", true);
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
                    UserOperateResult userOperateResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), UserOperateResult.class);
                    if (result.getCode() == ErrorCode.SUCCESS) {
                        if (flag == 1) {
                            // 更新存储的User对象
                            SPHelper.save("User", GsonUtils.convertJSON(userOperateResult.getUser()));
                        }
                        showToast("更新性别成功");
                        ActivityController.finishActivity(SetGenderActivity.this);
                    } else if (result.getCode() == ErrorCode.FAIL) {
                        showToast("更新性别失败");
                    }
                }
            });
        }
    }

}
