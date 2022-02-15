package com.liyuliang.mytime.activity.settings;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.liyuliang.mytime.utils.RegexUtils;
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
 * 设置电话页面
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class SetPhoneNumberActivity extends SwipeBackActivity {

    private Context mContext;
    private EditText etPhoneNumber, etSmsCode;
    private TextView tvGetConfirmCode;
    private Button btnModify;

    private final MyHandler myHandler = new MyHandler(this);
    private EventHandler eventHandler;
    private static final int COUNT_DOWN = -9, COUNT_DOWN_OVER = -8, RECEIVE_MESSAGE = 1;
    private int i = 30;
    public static ExecutorService executorService;
    private int flag, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phonenumber);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.SetPhoneNumber, R.drawable.back_white, -1, -1, -1, onClickListener);
        executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
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
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnModify = findViewById(R.id.btnModify);
        btnModify.setOnClickListener(onClickListener);
        etPhoneNumber.addTextChangedListener(textWatcher);
        etSmsCode = findViewById(R.id.et_sms_code);
        etSmsCode.addTextChangedListener(textWatcher);
        flag = getIntent().getIntExtra("flag", 0);
        User user = getIntent().getParcelableExtra("user");
        userId = user.getId();
        etPhoneNumber.setText(user.getPhone());
        tvGetConfirmCode = findViewById(R.id.tv_getConfirmCode);
        tvGetConfirmCode.setOnClickListener(onClickListener);
        findViewById(R.id.tv_noSmsCode).setOnClickListener(onClickListener);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            btnModify.setEnabled(!TextUtils.isEmpty(etPhoneNumber.getText().toString()) && !TextUtils.isEmpty(etSmsCode.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * 是否接收短信的验证
     */
    private final OnSendMessageHandler onSendMessageHandler = (s, s1) -> {
        //此方法在发送验证短信前被调用，传入参数为接收者号码,返回true表示此号码无须实际接收短信
        return false;
    };

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.iv_delete_phoneNumber) {
            etPhoneNumber.setText("");
        } else if (v.getId() == R.id.btnModify) {
            //校验手机验证码
            confirmCode();
        } else if (v.getId() == R.id.tv_getConfirmCode) {
            //获取短信验证码
            String phoneNumber = etPhoneNumber.getText().toString().trim();
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
        } else if (v.getId() == R.id.tv_noSmsCode) {
            //收不到短信验证码
            openActivity(ContactUsActivity.class);
        }
    };

    /**
     * 设置手机号
     */
    private void modifyPhoneNumber() {
        String key = "PhoneNumber";
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("请输入电话号码");
        } else {
            JsonObject params = new JsonObject();
            params.addProperty("key", key);
            params.addProperty("value", phoneNumber);
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
                        showToast("更新电话成功");
                        ActivityController.finishActivity(SetPhoneNumberActivity.this);
                    } else if (result.getCode() == ErrorCode.FAIL) {
                        showToast("更新电话失败");
                    }
                }
            });
        }
    }

    /**
     * 验证手机验证码
     */
    private void confirmCode() {
        String phoneNumber = etPhoneNumber.getText().toString();
        String smsCode = etSmsCode.getText().toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, smsCode);
    }

    private static class MyHandler extends Handler {
        WeakReference<SetPhoneNumberActivity> mActivity;

        MyHandler(SetPhoneNumberActivity setPhoneNumberActivity) {
            mActivity = new WeakReference<>(setPhoneNumberActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            SetPhoneNumberActivity theActivity = mActivity.get();
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
                        if (currentActivity instanceof SetPhoneNumberActivity) {
                            //验证码验证成功修改手机号
                            theActivity.modifyPhoneNumber();
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
