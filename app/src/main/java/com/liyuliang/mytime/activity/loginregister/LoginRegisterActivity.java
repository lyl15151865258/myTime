package com.liyuliang.mytime.activity.loginregister;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseActivity;
import com.liyuliang.mytime.activity.normal.HtmlActivity;
import com.liyuliang.mytime.bean.LoginResult;
import com.liyuliang.mytime.bean.RSAResult;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.constant.Constants;
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
import com.liyuliang.mytime.utils.StatusBarUtil;
import com.liyuliang.mytime.utils.ViewUtils;
import com.liyuliang.mytime.widget.dialog.SelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Objects;
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
 * ??????????????????
 * Created at 2019/2/15 13:08
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class LoginRegisterActivity extends BaseActivity {

    private Context mContext;
    private EditText etUsername, etPassword, etSmsCode;
    private TextView tvGetSmsCode, tvLoginRegister;
    private ImageView ivPasswordVisible;
    private Button btnLoginRegister;
    private LinearLayout llSmsCode;
    private SelectDialog selectDialog;
    private final MyHandler myHandler = new MyHandler(this);
    private EventHandler eventHandler;
    private int i = 30;
    public static ExecutorService executorService;
    private Boolean isLogin = true, isInvisiblePassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        mContext = this;

        // ??????EventHandler??????
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg9 = myHandler.obtainMessage();
                msg9.arg1 = 9;
                msg9.obj = data;
                Bundle bundle = new Bundle();
                bundle.putInt("event", event);
                bundle.putInt("result", result);
                msg9.setData(bundle);
                myHandler.sendMessage(msg9);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
        executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        ivPasswordVisible = findViewById(R.id.ivPasswordVisible);
        etUsername = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        ViewUtils.setCharSequence(etUsername);
        ViewUtils.setCharSequence(etPassword);
        ivPasswordVisible.setOnClickListener(onClickListener);
        tvLoginRegister = findViewById(R.id.tvLoginRegister);
        tvLoginRegister.setOnClickListener(onClickListener);
        findViewById(R.id.tvForgetPassword).setOnClickListener(onClickListener);
        etSmsCode = findViewById(R.id.etSmsCode);
        tvGetSmsCode = findViewById(R.id.tvGetSmsCode);
        tvGetSmsCode.setOnClickListener(onClickListener);
        btnLoginRegister = findViewById(R.id.btnLoginRegister);
        btnLoginRegister.setOnClickListener(onClickListener);
        llSmsCode = findViewById(R.id.llSmsCode);
        llSmsCode.setVisibility(View.GONE);
        tvLoginRegister.setText(R.string.NewUserRegister);
        TextView tvRegistrationProtocol = findViewById(R.id.tvRegistrationProtocol);
        setTextStyle(tvRegistrationProtocol);
        tvRegistrationProtocol.setOnClickListener(onClickListener);
        tvRegistrationProtocol.setOnClickListener(onClickListener);
        // ???????????????
        findViewById(R.id.ivWechat).setOnClickListener(onClickListener);
        findViewById(R.id.ivQQ).setOnClickListener(onClickListener);
        findViewById(R.id.ivWeibo).setOnClickListener(onClickListener);

        String action = getIntent().getAction();
        if (action != null) {
            switch (action) {
                case "RemoteLogin":
                    LogUtils.d(TAG, "Action???" + action + "????????????????????????????????????");
                    showRemoteLoginDialog();
                    break;
                case "SwitchAccount":
                    LogUtils.d(TAG, "Action???" + action + "?????????????????????");
                    // ?????????????????????????????????
                    SPHelper.save("userId", "");
                    SPHelper.save("passWord", "");
                    break;
                default:
                    break;
            }
        }

        // ?????????????????????????????????
        String userId = SPHelper.getString("userId", Constants.EMPTY);
        String passWord = SPHelper.getString("passWord", Constants.EMPTY);
        etUsername.setText(userId);
        etPassword.setText(passWord);

        ViewUtils.setCharSequence(etUsername);
        ViewUtils.setCharSequence(etPassword);
        ViewUtils.setCharSequence(etSmsCode);

    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, findViewById(R.id.myToolbar));
    }

    /**
     * ???????????????????????????
     */
    private final OnSendMessageHandler onSendMessageHandler = (s, s1) -> {
        //???????????????????????????????????????????????????????????????????????????,??????true???????????????????????????????????????
        return false;
    };

    /**
     * ?????????????????????
     */
    private void confirmCode() {
        String phoneNumber = etUsername.getText().toString();
        String smsCode = etSmsCode.getText().toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, smsCode);
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.tvForgetPassword) {
            //????????????
            openActivity(RetrievePasswordActivity.class);
        } else if (v.getId() == R.id.btnLoginRegister) {
            //??????????????????????????????????????????
            String serverPublicKey = SPHelper.getString("serverPublicKey", "");
            if (TextUtils.isEmpty(serverPublicKey)) {
                // ????????????????????????????????????????????????
                getRSAPublicKey();
            } else {
                //???????????????
                if (isLogin) {
                    login();
                } else {
                    registerBtn();
                }
            }
        } else if (v.getId() == R.id.ivPasswordVisible) {
            //????????????
            ViewUtils.changePasswordState(isInvisiblePassword, etPassword, ivPasswordVisible);
            isInvisiblePassword = !isInvisiblePassword;
        } else if (v.getId() == R.id.tvLoginRegister) {
            //???????????????????????????
            if (isLogin) {
                // ?????????????????????????????????
                llSmsCode.setVisibility(View.VISIBLE);
                btnLoginRegister.setText(R.string.Register);
                tvLoginRegister.setText(R.string.HaveAccount);
                etPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                isLogin = false;
            } else {
                // ?????????????????????????????????
                llSmsCode.setVisibility(View.GONE);
                btnLoginRegister.setText(R.string.Login);
                tvLoginRegister.setText(R.string.NewUserRegister);
                etPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
                isLogin = true;
            }
        } else if (v.getId() == R.id.tvGetSmsCode) {
            //?????????????????????
            String phoneNumber = etUsername.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                showToast("??????????????????");
                return;
            }
            if (!RegexUtils.checkMobile(phoneNumber)) {
                showToast("???????????????????????????");
                return;
            }
            SMSSDK.getVerificationCode("86", phoneNumber, onSendMessageHandler);
            tvGetSmsCode.setClickable(false);
            tvGetSmsCode.setText(String.format(getString(R.string.resend_info_time), i));
            Runnable runnable = () -> {
                for (; i > 0; i--) {
                    Message msg7 = myHandler.obtainMessage();
                    msg7.arg1 = 7;
                    myHandler.sendMessage(msg7);
                    if (i <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg8 = myHandler.obtainMessage();
                msg8.arg1 = 8;
                myHandler.sendMessage(msg8);
            };
            executorService.submit(runnable);
        } else if (v.getId() == R.id.tvRegistrationProtocol) {
            //????????????????????????
            Intent intent = new Intent(LoginRegisterActivity.this, HtmlActivity.class);
            String url = NetClient.getBaseUrlProject() + "html/RegisterProtocols/RegisterProtocols.html";
            intent.putExtra("title", getString(R.string.RegistrationProtocol));
            intent.putExtra("URL", url);
            startActivity(intent);
        } else if (v.getId() == R.id.ivWechat) {
            // ????????????
            showToast("??????????????????");
        } else if (v.getId() == R.id.ivQQ) {
            // QQ??????
            showToast("??????????????????");
        } else if (v.getId() == R.id.ivWeibo) {
            // ????????????
            showToast("??????????????????");
        }
    };

    /**
     * ??????????????????RSA??????????????????????????????
     */
    private void getRSAPublicKey() {
        Observable<Result> observable = NetClient.getInstance(NetClient.getBaseUrlProject(), false).getZbsApi().getRSAPublicKey();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //??????????????????????????????????????????
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast(getString(R.string.NetworkUnavailable));
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast("?????????RSA??????????????????");
            }

            @Override
            public void onNext(@NonNull Result result) {
                RSAResult rsaResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), RSAResult.class);
                LogUtils.d(TAG, "????????????RSA????????????" + rsaResult.getRsaPublicKey());
                SPHelper.save("serverPublicKey", rsaResult.getRsaPublicKey());
                if (isLogin) {
                    login();
                } else {
                    registerBtn();
                }
            }
        });
    }

    /**
     * ???????????????????????????
     */
    private void registerBtn() {
        //???????????????????????????
        String userNameRegister = etUsername.getText().toString().trim();
        String passWordRegister = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userNameRegister)) {
            showToast(R.string.EnterPhoneNumber);
            return;
        }
        if (TextUtils.isEmpty(passWordRegister)) {
            showToast(R.string.EnterPassword);
            return;
        }
        int passwordLength = 6;
        if (passWordRegister.length() < passwordLength) {
            showToast(R.string.LessThanSixDigits);
            return;
        }
        // ?????????????????????
        confirmCode();
    }

    /**
     * ??????????????????
     *
     * @param textView ????????????
     */
    private void setTextStyle(TextView textView) {
        String text = textView.getText().toString();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        UnderlineSpan lineSpan = new UnderlineSpan();
        int start = text.indexOf(getString(R.string.RegistrationProtocol));
        int end = start + getString(R.string.RegistrationProtocol).length();
        //?????????
        builder.setSpan(lineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //????????????
        builder.setSpan(blueSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }

    /**
     * ???????????????
     */
    private void login() {
        LogUtils.d("login", "LoginRegisterActivity????????????");
        String userId = etUsername.getText().toString().trim();
        String passWord = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userId)) {
            showToast(getString(R.string.EnterPhoneNumber));
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            showToast(getString(R.string.EnterPassword));
            return;
        }

        JsonObject params = new JsonObject();
        params.addProperty("userId", userId);
        params.addProperty("password", passWord);

        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().login(params);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //??????????????????????????????????????????
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast(getString(R.string.NetworkUnavailable));
                } else {
                    showLoadingDialog(mContext, getString(R.string.LoggingIn), true);
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
                    LoginResult loginResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), LoginResult.class);
                    //?????????????????????
                    String phoneNumber = etUsername.getText().toString();
                    String passWord = etPassword.getText().toString();
                    SPHelper.save("userId", phoneNumber);
                    SPHelper.save("passWord", passWord);
                    //???????????????????????????
//                    SPHelper.save("version", GsonUtils.convertJSON(loginResult.getVersion()));
//                    SPHelper.save("User", GsonUtils.convertJSON(loginResult.getUser()));

                    openActivity(MainActivity.class);
                    ActivityController.finishActivity(LoginRegisterActivity.this);
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast("????????????");
                }
            }
        });
    }

    /**
     * ???????????????
     */
    private void register() {
        String userId = etUsername.getText().toString().trim();
        String passWord = etPassword.getText().toString().trim();
        JsonObject params = new JsonObject();
        params.addProperty("phoneNumber", userId);
        params.addProperty("password", passWord);

        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), false).getZbsApi().register(params);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //??????????????????????????????????????????
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast(getString(R.string.NetworkUnavailable));
                } else {
                    showLoadingDialog(mContext, getString(R.string.Registering), true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast("" + responseThrowable.message);
            }

            @Override
            public void onNext(@NonNull Result result) {
                cancelDialog();
                super.onNext(result);
                if (result.getCode() == ErrorCode.SUCCESS) {
                    //?????????????????????
                    String phoneNumber = etUsername.getText().toString();
                    String passWord = etPassword.getText().toString();
                    SPHelper.save("userId", phoneNumber);
                    SPHelper.save("passWord", passWord);
                    showToast(R.string.RegisteredSuccess);
                    //???????????????????????????????????????????????????????????????
                    llSmsCode.setVisibility(View.GONE);
                    btnLoginRegister.setText(R.string.Login);
                    tvLoginRegister.setText(R.string.HaveAccount);
                    isLogin = true;
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast(R.string.RegisterFailed);
                }
            }
        });
    }

    /**
     * ???????????????????????????????????????
     */
    private void showRemoteLoginDialog() {
        if (selectDialog == null) {
            selectDialog = new SelectDialog(mContext, getString(R.string.remote_login));
            selectDialog.setButtonText(getString(R.string.Exit), getString(R.string.LoginAgain));
            selectDialog.setCancelable(false);
            selectDialog.setOnDialogClickListener(new SelectDialog.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    // ????????????
                    btnLoginRegister.performClick();
                }

                @Override
                public void onCancelClick() {
                    // ??????
                    ActivityController.finishActivity(LoginRegisterActivity.this);
                }
            });
        }
        if (!selectDialog.isShowing()) {
            selectDialog.show();
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<LoginRegisterActivity> mActivity;

        MyHandler(LoginRegisterActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginRegisterActivity theActivity = mActivity.get();
            switch (msg.arg1) {
                case 1:
                    theActivity.etSmsCode.setText(msg.obj.toString());
                    break;
                case 7:
                    theActivity.tvGetSmsCode.setText(String.format(theActivity.getString(R.string.resend_info_time), theActivity.i));
                    break;
                case 8:
                    theActivity.tvGetSmsCode.setText("???????????????");
                    theActivity.tvGetSmsCode.setClickable(true);
                    theActivity.i = 30;
                    break;
                case 9:
                    int event = msg.getData().getInt("event");
                    int result9 = msg.getData().getInt("result");
                    LogUtils.d("SMS????????????", String.valueOf(result9));
                    Object data = msg.obj;
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result9 == SMSSDK.RESULT_COMPLETE) {
                            //?????????????????????
                            //???????????????????????????????????????????????????????????????????????????Activity???????????????Activity??????
                            AppCompatActivity currentActivity = (AppCompatActivity) ActivityController.getInstance().getCurrentActivity();
                            if (currentActivity instanceof LoginRegisterActivity) {
                                theActivity.register();
                            }
                        } else if (result9 == SMSSDK.RESULT_ERROR) {
                            theActivity.showToast("?????????????????????");
                        }
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result9 == SMSSDK.RESULT_COMPLETE) {
                            theActivity.showToast("?????????????????????");
                        } else if (result9 == SMSSDK.RESULT_ERROR) {
                            theActivity.showToast("?????????????????????");
                        }
                    } else {
                        ((Throwable) data).printStackTrace();
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        LogUtils.d(theActivity.TAG, throwable.toString());
                        try {
                            JSONObject obj = new JSONObject(Objects.requireNonNull(throwable.getMessage()));
                            final String des = obj.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                theActivity.showToast(des);
                                LogUtils.d(theActivity.TAG, des);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
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