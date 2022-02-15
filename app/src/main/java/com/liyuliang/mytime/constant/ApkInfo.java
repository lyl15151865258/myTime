package com.liyuliang.mytime.constant;

import android.os.Environment;

import com.liyuliang.mytime.MyTimeApplication;

/**
 * 软件信息类
 * Created at 2019/9/27 19:04
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ApkInfo {

    /**
     * 软件类型
     */
    public static final String APK_TYPE_ID_MyTime = "1";

    // 文件路径
    public final static String APP_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MyTimeApplication.getInstance().getPackageName();
    public final static String DOWNLOAD_DIR = "/downlaod/";

}
