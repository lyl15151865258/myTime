package com.liyuliang.mytime.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.bean.Result;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 设置姓名页面
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class SetUserNameActivity extends SwipeBackActivity {

    private Context mContext;
    private EditText etUserName;
    private int flag, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_username);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.SetName, R.drawable.back_white, -1, -1, -1, onClickListener);
        etUserName = findViewById(R.id.etUserName);
        Button btnModify = findViewById(R.id.btnModify);
        btnModify.setOnClickListener(onClickListener);
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    btnModify.setEnabled(false);
                } else {
                    btnModify.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        flag = getIntent().getIntExtra("flag", 0);
        User user = getIntent().getParcelableExtra("user");
        userId = user.getId();
        etUserName.setText(user.getUsername());
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else {
            modifyUserName();
        }
    };

    /**
     * 保存姓名
     */
    private void modifyUserName() {
        String key = "UserName";
        String userName = etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showToast("请输入姓名");
        } else {
            JsonObject params = new JsonObject();
            params.addProperty("key", key);
            params.addProperty("value", userName);
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
                        showToast("更新姓名成功");
                        ActivityController.finishActivity(SetUserNameActivity.this);
                    } else if (result.getCode() == ErrorCode.FAIL) {
                        showToast("更新姓名失败");
                    }
                }
            });
        }
    }

}
