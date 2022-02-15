package com.liyuliang.mytime.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.contentprovider.SPHelper;

import java.util.Locale;

/**
 * 软件语言设置工具
 * Created at 2018/11/28 13:51
 *
 * @author LiYuliang
 * @version 1.0
 */

public class LanguageUtil {

    public static String getLanguageLocal(Context context) {
        return SPHelper.getString(context.getString(R.string.language), "");
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, getLanguageLocal(context));
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = new Locale(getLanguageLocal(context));

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    /**
     * 获取手机设置的语言国家
     *
     * @param context Context对象
     * @return 国家或地区代码
     */
    public static String getCountry(Context context) {
        String country;
        Resources resources = context.getResources();
        //在7.0以上和7.0一下获取国家的方式有点不一样
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //  大于等于24即为7.0及以上执行内容
            country = resources.getConfiguration().getLocales().get(0).getCountry();
        } else {
            //  低于24即为7.0以下执行内容
            country = resources.getConfiguration().locale.getCountry();
        }
        return country;
    }

    /**
     * 切换语言
     * @param context Context对象
     */
    public static void changeAppLanguage(Context context) {
        String sta = LanguageUtil.getLanguageLocal(context);
        // 本地语言设置
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        switch (sta) {
            case "zh":
                conf.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "zh-rtw":
                conf.locale = Locale.TRADITIONAL_CHINESE;
                break;
            case "en":
                conf.locale = Locale.ENGLISH;
                break;
            case "ja":
                conf.locale = Locale.JAPANESE;
                break;
            default:
                conf.locale = Locale.getDefault();
                break;
        }
        res.updateConfiguration(conf, dm);
    }

}
