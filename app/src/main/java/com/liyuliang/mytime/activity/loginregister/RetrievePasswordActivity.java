package com.liyuliang.mytime.activity.loginregister;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.activity.settings.ContactUsActivity;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.utils.RegexUtils;
import com.liyuliang.mytime.utils.ViewUtils;
import com.liyuliang.mytime.widget.MyToolbar;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 找回主账号密码
 * Created by LiYuliang on 2017/07/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class RetrievePasswordActivity extends SwipeBackActivity {

    private Context mContext;
    private EditText etPhoneNumberRetrievePassword, etSmsCode, etNewPassword1, etNewPassword2;
    private TextView tvGetConfirmCode;
    private ImageView ivShowNewPassword1, ivShowNewPassword2;
    private Button btnModify;
    private final MyHandler myHandler = new MyHandler(this);
    private Boolean isInvisibleNewPassword1, isInvisibleNewPassword2;
    private EventHandler eventHandler;
    private static final int COUNT_DOWN = -9, COUNT_DOWN_OVER = -8, RECEIVE_MESSAGE = 1;
    private int i = 30;
    public static ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        mContext = this;
        executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.RetrievePassword, R.drawable.back_white, -1, -1, -1, onClickListener);
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                myHandler.sendMessage(msg);
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
        findViewById(R.id.iv_delete_phoneNumber).setOnClickListener(onClickListener);
        btnModify = findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(onClickListener);
        etPhoneNumberRetrievePassword = findViewById(R.id.et_phoneNumber_retrieve_password);
        etSmsCode = findViewById(R.id.et_sms_code);
        etNewPassword1 = findViewById(R.id.et_newPassword1);
        etNewPassword2 = findViewById(R.id.et_newPassword2);
        etPhoneNumberRetrievePassword.addTextChangedListener(textWatcher);
        etSmsCode.addTextChangedListener(textWatcher);
        etNewPassword1.addTextChangedListener(textWatcher);
        etNewPassword2.addTextChangedListener(textWatcher);
        ivShowNewPassword1 = findViewById(R.id.iv_showNewPassword1);
        ivShowNewPassword2 = findViewById(R.id.iv_showNewPassword2);
        ivShowNewPassword1.setOnClickListener(onClickListener);
        ivShowNewPassword2.setOnClickListener(onClickListener);
        tvGetConfirmCode = findViewById(R.id.tv_getConfirmCode);
        tvGetConfirmCode.setOnClickListener(onClickListener);
        findViewById(R.id.tv_noSmsCode).setOnClickListener(onClickListener);
        isInvisibleNewPassword1 = true;
        isInvisibleNewPassword2 = true;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (TextUtils.isEmpty(etPhoneNumberRetrievePassword.getText().toString()) || TextUtils.isEmpty(etSmsCode.getText().toString()) ||
                    TextUtils.isEmpty(etNewPassword1.getText().toString()) || TextUtils.isEmpty(etNewPassword2.getText().toString())) {
                btnModify.setEnabled(false);
            } else {
                btnModify.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phoneNumber = etPhoneNumberRetrievePassword.getText().toString().trim();
            String newPassword1 = etNewPassword1.getText().toString().trim();
            String newPassword2 = etNewPassword2.getText().toString().trim();
            switch (v.getId()) {
                case R.id.toolbarLeft:
                    ActivityController.finishActivity(RetrievePasswordActivity.this);
                    break;
                case R.id.iv_delete_phoneNumber:
                    etPhoneNumberRetrievePassword.setText("");
                    break;
                case R.id.btn_modify:
                    int passwordLength = 6;
                    if (!newPassword1.equals(newPassword2)) {
                        showToast("两次输入的密码不一致");
                        return;
                    } else if (newPassword1.length() < passwordLength) {
                        showToast("新密码长度小于6位");
                        return;
                    }
                    //校验手机验证码
                    confirmCode();
                    break;
                case R.id.iv_showNewPassword1:
                    ViewUtils.changePasswordState(isInvisibleNewPassword1, etNewPassword1, ivShowNewPassword1);
                    isInvisibleNewPassword1 = !isInvisibleNewPassword1;
                    break;
                case R.id.iv_showNewPassword2:
                    ViewUtils.changePasswordState(isInvisibleNewPassword2, etNewPassword2, ivShowNewPassword2);
                    isInvisibleNewPassword2 = !isInvisibleNewPassword2;
                    break;
                case R.id.tv_getConfirmCode:
                    //获取短信验证码
                    if (TextUtils.isEmpty(phoneNumber)) {
                        showToast("请输入手机号");
                        return;
                    }
                    if (!RegexUtils.checkMobile(phoneNumber)) {
                        showToast("请输入正确的手机号");
                        return;
                    }
                    SMSSDK.getVerificationCode("86", phoneNumber, onSendMessageHandler);
                    tvGetConfirmCode.setClickable(false);
                    tvGetConfirmCode.setText(String.format(getString(R.string.resend_info_time), i));
                    Runnable runnable = () -> {
                        for (; i > 0; i--) {
                            myHandler.sendEmptyMessage(COUNT_DOWN);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        myHandler.sendEmptyMessage(COUNT_DOWN_OVER);
                    };
                    executorService.submit(runnable);
                    break;
                case R.id.tv_noSmsCode:
                    //收不到短信验证码
                    openActivity(ContactUsActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 验证手机验证码
     */
    private void confirmCode() {
        String phoneNumber = etPhoneNumberRetrievePassword.getText().toString();
        String smsCode = etSmsCode.getText().toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, smsCode);
    }

    /**
     * 是否接收短信的验证
     */
    private OnSendMessageHandler onSendMessageHandler = (s, s1) -> {
        //此方法在发送验证短信前被调用，传入参数为接收者号码,返回true表示此号码无须实际接收短信
        return false;
    };

    /**
     * 重置密码
     *
     * @param phoneNumber 手机号
     * @param newPassword 密码
     */
    private void resetPassword(String phoneNumber, String newPassword) {
        JsonObject params = new JsonObject();
        params.addProperty("phoneNumber", phoneNumber);
        params.addProperty("password", newPassword);

        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().resetPassword(params);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                } else {
                    showLoadingDialog(mContext, "重置中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(Result result) {
                cancelDialog();
                super.onNext(result);
                if (result.getCode() == ErrorCode.SUCCESS) {
                    SPHelper.save("userId", etPhoneNumberRetrievePassword.getText().toString());
                    SPHelper.save("passWord", etNewPassword1.getText().toString());
                    showToast("密码重置成功");
                    ActivityController.finishActivity(RetrievePasswordActivity.this);
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast("密码重置失败");
                }
            }
        });
    }

    private static class MyHandler extends Handler {
        WeakReference<RetrievePasswordActivity> mActivity;

        MyHandler(RetrievePasswordActivity retrievePasswordActivity) {
            mActivity = new WeakReference<>(retrievePasswordActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            RetrievePasswordActivity theActivity = mActivity.get();
            if (msg.what == COUNT_DOWN) {
                theActivity.tvGetConfirmCode.setText(String.format(theActivity.getString(R.string.resend_info_time), theActivity.i));
            } else if (msg.what == COUNT_DOWN_OVER) {
                theActivity.tvGetConfirmCode.setText("获取验证码");
                theActivity.tvGetConfirmCode.setClickable(true);
                theActivity.i = 30;
            } else if (msg.what == RECEIVE_MESSAGE) {
                theActivity.etSmsCode.setText(msg.obj.toString());
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //验证码验证成功
                        //由于登陆注册页面也有短信监听，所以需要先判断栈顶的Activity是否是当前Activity对象
                        AppCompatActivity currentActivity = (AppCompatActivity) ActivityController.getInstance().getCurrentActivity();
                        if (currentActivity instanceof RetrievePasswordActivity) {
                            //验证码验证成功重置密码
                            String phoneNumber = theActivity.etPhoneNumberRetrievePassword.getText().toString().trim();
                            String newPassword = theActivity.etNewPassword1.getText().toString().trim();
                            theActivity.resetPassword(phoneNumber, newPassword);
                        }
                    } else if (result == SMSSDK.RESULT_ERROR) {
                        theActivity.showToast("验证码输入错误");
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        theActivity.showToast("验证码已经发送");
                    } else if (result == SMSSDK.RESULT_ERROR) {
                        theActivity.showToast("验证码发送失败");
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        SMSSDK.unregisterEventHandler(eventHandler);
        executorService.shutdown();
    }
}
