package com.liyuliang.mytime.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查权限
 * Created at 2019/3/3 21:35
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class PermissionUtil {

    // 需要申请的权限
    private static final String[] mPermissionList = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    // 判断是否还需要申请权限
    public static boolean isNeedRequestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Android6.0以下不需要申请权限
            return false;
        } else {
            // Android6.0以上，判断未授权的权限数量是多少
            List<String> mPermissionListDenied = new ArrayList<>();
            for (String permission : mPermissionList) {
                int result = checkPermission(activity, permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    mPermissionListDenied.add(permission);
                }
            }
            return mPermissionListDenied.size() > 0;
        }
    }

    // 判断是否需要申请权限
    private static boolean isNeedRequestPermission(Activity activity, String... permissions) {
        List<String> mPermissionListDenied = new ArrayList<>();
        for (String permission : permissions) {
            int result = checkPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                mPermissionListDenied.add(permission);
            }
        }
        return mPermissionListDenied.size() > 0;
    }

    // 申请权限
    public static void requestPermission(Activity activity) {
        // 遍历取出未授权的权限
        List<String> mPermissionListDenied = new ArrayList<>();
        for (String permission : mPermissionList) {
            int result = checkPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                mPermissionListDenied.add(permission);
            }
        }
        // 申请权限
        String[] pears = new String[mPermissionListDenied.size()];
        pears = mPermissionListDenied.toArray(pears);
        ActivityCompat.requestPermissions(activity, pears, 0);
    }

    private static int checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

}
