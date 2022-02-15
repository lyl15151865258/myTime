package com.liyuliang.mytime.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
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
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.utils.RegexUtils;
import com.liyuliang.mytime.widget.MyToolbar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 设置邮箱地址页面
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class SetEmailActivity extends SwipeBackActivity {

    private Context mContext;
    private EditText etEmailAddress, etEmailPassword;
    private int flag, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_email);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.SetMailbox, R.drawable.back_white, -1, -1, -1, onClickListener);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etEmailPassword = findViewById(R.id.etEmailPassword);
        Button btnModify = findViewById(R.id.btnModify);
        btnModify.setOnClickListener(onClickListener);
        flag = getIntent().getIntExtra("flag", 0);
        User user = getIntent().getParcelableExtra("user");
        userId = user.getId();
        etEmailAddress.setText(user.getMail());
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.btnModify) {
            setEMailAddress();
        }
    };

    /**
     * 设置邮箱
     */
    private void setEMailAddress() {
        String key = "EmailAddress";
        String emailAddress = etEmailAddress.getText().toString().trim();
        String emailPassword = etEmailPassword.getText().toString().trim();
        if (TextUtils.isEmpty(emailAddress)) {
            showToast("请输入邮箱地址");
        } else if (!RegexUtils.isEmail(emailAddress)) {
            showToast("请输入正确的邮箱");
        } else if (TextUtils.isEmpty(emailPassword)) {
            showToast("请输入邮箱密码");
        } else {

        }
    }

    /**
     * 修改邮箱
     */
    private void modifyMailAddress(String key, String emailAddress) {
        JsonObject params = new JsonObject();
        params.addProperty("key", key);
        params.addProperty("value", emailAddress);
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
                    showToast("更新邮箱成功");
                    ActivityController.finishActivity(SetEmailActivity.this);
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast("更新邮箱失败");
                }
            }
        });
    }

}
