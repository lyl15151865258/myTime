package com.liyuliang.mytime.activity.loginregister;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.utils.ViewUtils;
import com.liyuliang.mytime.widget.MyToolbar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 更改主账号密码
 * Created by LiYuliang on 2017/07/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class ResetPasswordActivity extends SwipeBackActivity {

    private Context mContext;
    private int userId;
    private EditText etOldPassword, etNewPassword1, etNewPassword2;
    private ImageView ivShowOldPassword, ivShowNewPassword1, ivShowNewPassword2;
    private Button btnModify;
    private String password;
    private Boolean isInvisibleOldPassword = true, isInvisibleNewPassword1 = true, isInvisibleNewPassword2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mContext = this;
        User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
        userId = user.getId();
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.ChangePassword, R.drawable.back_white, -1, -1, -1, onClickListener);
        btnModify = findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(onClickListener);
        etOldPassword = findViewById(R.id.et_oldPassword);
        etNewPassword1 = findViewById(R.id.et_newPassword1);
        etNewPassword2 = findViewById(R.id.et_newPassword2);
        etOldPassword.addTextChangedListener(textWatcher);
        etNewPassword1.addTextChangedListener(textWatcher);
        etNewPassword2.addTextChangedListener(textWatcher);
        ivShowOldPassword = findViewById(R.id.iv_showOldPassword);
        ivShowNewPassword1 = findViewById(R.id.iv_showNewPassword1);
        ivShowNewPassword2 = findViewById(R.id.iv_showNewPassword2);
        ivShowOldPassword.setOnClickListener(onClickListener);
        ivShowNewPassword1.setOnClickListener(onClickListener);
        ivShowNewPassword2.setOnClickListener(onClickListener);
        findViewById(R.id.tv_forgetPassword_login).setOnClickListener(onClickListener);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            btnModify.setEnabled(!TextUtils.isEmpty(etOldPassword.getText().toString()) && !TextUtils.isEmpty(etNewPassword1.getText().toString()) &&
                    !TextUtils.isEmpty(etNewPassword2.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(ResetPasswordActivity.this);
        } else if (v.getId() == R.id.btn_modify) {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword1 = etNewPassword1.getText().toString().trim();
            String newPassword2 = etNewPassword2.getText().toString().trim();
            int passwordLength = 6;
            if (TextUtils.isEmpty(oldPassword)) {
                showToast("请输入旧密码");
            } else if (TextUtils.isEmpty(newPassword1)) {
                showToast("请输入新密码");
            } else if (TextUtils.isEmpty(newPassword2)) {
                showToast("请再次输入新密码");
            } else if (!newPassword1.equals(newPassword2)) {
                showToast("两次输入的密码不一致");
            } else if (newPassword1.length() < passwordLength) {
                showToast("新密码长度小于6位");
            } else {
                password = newPassword2;
                updatePassword(oldPassword, password);
            }
        } else if (v.getId() == R.id.iv_showOldPassword) {
            ViewUtils.changePasswordState(isInvisibleOldPassword, etOldPassword, ivShowOldPassword);
            isInvisibleOldPassword = !isInvisibleOldPassword;
        } else if (v.getId() == R.id.iv_showNewPassword1) {
            ViewUtils.changePasswordState(isInvisibleNewPassword1, etNewPassword1, ivShowNewPassword1);
            isInvisibleNewPassword1 = !isInvisibleNewPassword1;
        } else if (v.getId() == R.id.iv_showNewPassword2) {
            ViewUtils.changePasswordState(isInvisibleNewPassword2, etNewPassword2, ivShowNewPassword2);
            isInvisibleNewPassword2 = !isInvisibleNewPassword2;
        } else if (v.getId() == R.id.tv_forgetPassword_login) {
            //忘记密码
            openActivity(RetrievePasswordActivity.class);
            ActivityController.finishActivity(this);
        }
    };

    /**
     * 修改密码的方法
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    private void updatePassword(String oldPassword, String newPassword) {
        JsonObject params = new JsonObject();
        params.addProperty("userId", String.valueOf(userId));
        params.addProperty("oldpassword", oldPassword);
        params.addProperty("newpassword", newPassword);

        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().updatePassword(params);
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
                if (result.getCode() == ErrorCode.SUCCESS) {
                    SPHelper.save("passWord", password);
                    showToast("密码更新成功");
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginRegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    ActivityController.finishActivity(ResetPasswordActivity.this);
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast("密码更新失败");
                }
            }
        });
    }
}
