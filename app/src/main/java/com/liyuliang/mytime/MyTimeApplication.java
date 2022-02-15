package com.liyuliang.mytime;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.util.SparseArray;

import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.sqlite.DbHelper;
import com.liyuliang.mytime.utils.CrashHandler;
import com.liyuliang.mytime.utils.LanguageUtil;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.utils.MyLifecycleHandler;
import com.liyuliang.mytime.utils.encrypt.RSAUtils;
import com.tencent.smtt.sdk.QbSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Application类
 * Created by Li Yuliang on 2019/03/26.
 *
 * @author LiYuliang
 * @version 2019/03/26
 */

public class MyTimeApplication extends Application {

    private static MyTimeApplication instance;
    private DbHelper mDbHelper;
    public static String publicKeyString, privateKeyString;

    @Override
    public void onCreate() {
        double time1 = System.currentTimeMillis();
        super.onCreate();
        instance = this;
        SPHelper.init(this);
        //注册Activity生命周期回调
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        // 捕捉异常
        CrashHandler.getInstance().init(this);
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        // 此处注册EventBus是为了修改TextClock采用的语言，因为TextClock直接获取Application的Locale，而不是Activity的
        EventBus.getDefault().register(this);
        init();
        LogUtils.d("TimeRecord", "Application结束时间戳：" + System.currentTimeMillis());
        LogUtils.d("TimeRecord", "Application初始化耗时：" + (System.currentTimeMillis() - time1) / 1000 + "s");
    }

    /**
     * 初始化一些内容
     */
    private void init() {
        new Thread(() -> {
            // 生成RSA密钥对
            SparseArray<String> keyMap = null;
            try {
                keyMap = RSAUtils.genKeyPair(1024);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (keyMap != null) {
                publicKeyString = keyMap.get(0);
                privateKeyString = keyMap.get(1);
            }
            //初始化腾讯X5浏览器内核
            initX5WebView();
            //切换语言
            LanguageUtil.changeAppLanguage(this);
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringEvent(String msg) {
        LanguageUtil.changeAppLanguage(this);
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return application实例
     */
    public static MyTimeApplication getInstance() {
        if (instance == null) {
            instance = new MyTimeApplication();
        }
        return instance;
    }

    /**
     * 初始化腾讯X5内核
     */
    private void initX5WebView() {
        //搜集本地TBS内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.d("X5WebView", "onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public DbHelper getmDbHelper() {
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(instance);
            mDbHelper.getDBHelper();
            mDbHelper.open();
        }
        return mDbHelper;
    }

    public void setmDbHelper(DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }

}
