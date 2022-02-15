package com.liyuliang.mytime.utils;

import android.text.TextUtils;

import com.liyuliang.mytime.network.MyTimeApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 加载文件工具类
 * Created at 2018/9/25 0025 10:30
 *
 * @author LiYuliang
 * @version 1.0
 */

public class LoadFileUtil {

    public static void loadPdfFile(String url, Callback<ResponseBody> callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyTimeApi mLoadFileApi = retrofit.create(MyTimeApi.class);
        if (!TextUtils.isEmpty(url)) {
            Call<ResponseBody> call = mLoadFileApi.downloadFile(url);
            call.enqueue(callback);
        }

    }
}
