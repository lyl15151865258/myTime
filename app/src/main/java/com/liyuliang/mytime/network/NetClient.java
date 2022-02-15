package com.liyuliang.mytime.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liyuliang.mytime.BuildConfig;
import com.liyuliang.mytime.MyTimeApplication;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.constant.NetWork;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.utils.encrypt.AESUtils;
import com.liyuliang.mytime.utils.encrypt.GetKey;
import com.liyuliang.mytime.utils.encrypt.RSAUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 请求接口URL
 * Created at 2018/11/28 13:48
 *
 * @author LiYuliang
 * @version 1.0
 */

public class NetClient {

    public static final String TAG = "NetClient";
    // NetClient复用对象（不带加解密、带加解密）
    private static NetClient mNetClient, mEncryptNetClient;
    private MyTimeApi myTimeApi;
    private final Retrofit mRetrofit;
    private static String defaultUrl = "";
    private static final String CACHE_NAME = "NetCache";

    private NetClient(String baseUrl, boolean encrypt) {

        // log用拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // 加解密拦截器
        DataEncryptInterceptor dataEncryptInterceptor = new DataEncryptInterceptor();
        // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }

        //设置缓存目录
        File cacheFile = new File(MyTimeApplication.getInstance().getExternalCacheDir(), CACHE_NAME);
        //生成缓存，50M
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        //缓存拦截器
        Interceptor cacheInterceptor = (chain) -> {
            Request request = chain.request();
            if (NetworkUtil.isNetworkAvailable(MyTimeApplication.getInstance())) {
                //网络可用,强制从网络获取数据
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            } else {
                //网络不可用,在请求头中加入：强制使用缓存，不访问网络
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            //网络可用
            if (NetworkUtil.isNetworkAvailable(MyTimeApplication.getInstance())) {
                int maxAge = 60 * 60;
                // 有网络时 在响应头中加入：设置缓存超时时间1个小时
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("pragma")
                        .build();
            } else {
                // 无网络时，在响应头中加入：设置超时为1周
                int maxStale = 60 * 60 * 24 * 7;
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("pragma")
                        .build();
            }
            return response;
        };

        // OkHttpClient对象
        OkHttpClient okHttpClient;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
//        try {
//            //添加SSL证书验证
//            builder.sslSocketFactory(getSSLSocketFactory(), new MyX509TrustManager());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }

        // 根据是否需要加密确定是否加入DataEncryptInterceptor拦截器
        if (encrypt) {
            LogUtils.d(TAG, "走加密的请求");
            okHttpClient = builder
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(cacheInterceptor)
                    .addInterceptor(dataEncryptInterceptor)
                    .cache(cache)
                    //设置超时时间
                    .connectTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                    .readTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                    .writeTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                    //错误重连
                    .retryOnConnectionFailure(true)
                    .build();
        } else {
            LogUtils.d(TAG, "走不加密的请求");
            okHttpClient = builder
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(cacheInterceptor)
                    .cache(cache)
                    //设置超时时间
                    .connectTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                    .readTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                    .writeTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                    //错误重连
                    .retryOnConnectionFailure(true)
                    .build();
        }

        //设置Gson的非严格模式
        Gson gson = new GsonBuilder().setLenient().create();
        // 初始化Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = {new MyX509TrustManager()};
        context.init(null, trustManagers, new SecureRandom());
        return context.getSocketFactory();
    }

    private class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (chain == null) {
                throw new CertificateException("checkServerTrusted: X509Certificate array is null");
            }
            if (chain.length < 1) {
                throw new CertificateException("checkServerTrusted: X509Certificate is empty");
            }
            if (!(null != authType && authType.equals("ECDHE_RSA"))) {
                throw new CertificateException("checkServerTrusted: AuthType is not ECDHE_RSA");
            }

            //检查所有证书
            try {
                TrustManagerFactory factory = TrustManagerFactory.getInstance("X509");
                factory.init((KeyStore) null);
                for (TrustManager trustManager : factory.getTrustManagers()) {
                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            //获取本地证书中的信息
            String clientEncoded = "";
            String clientSubject = "";
            String clientIssUser = "";
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                InputStream inputStream = MyTimeApplication.getInstance().getAssets().open("zbs.cer");
                X509Certificate clientCertificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
                clientEncoded = new BigInteger(1, clientCertificate.getPublicKey().getEncoded()).toString(16);
                clientSubject = clientCertificate.getSubjectDN().getName();
                clientIssUser = clientCertificate.getIssuerDN().getName();
                LogUtils.d(TAG, "证书详情：clientEncoded：" + clientEncoded + "，clientSubject" + clientSubject + "，clientIssUser" + clientIssUser);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //获取网络中的证书信息
            X509Certificate certificate = chain[0];
            PublicKey publicKey = certificate.getPublicKey();
            String serverEncoded = new BigInteger(1, publicKey.getEncoded()).toString(16);

            if (!clientEncoded.equals(serverEncoded)) {
                throw new CertificateException("server's PublicKey is not equals to client's PublicKey");
            }
            String subject = certificate.getSubjectDN().getName();
            if (!clientSubject.equals(subject)) {
                throw new CertificateException("server's subject is not equals to client's subject");
            }
            String issuser = certificate.getIssuerDN().getName();
            if (!clientIssUser.equals(issuser)) {
                throw new CertificateException("server's issuser is not equals to client's issuser");
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return (Converter<ResponseBody, Object>) body -> {
                if (body.contentLength() == 0) return null;
                return delegate.convert(body);
            };
        }
    }

    /**
     * 数据加解密拦截器
     */
    public class DataEncryptInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            //请求
            Request request = chain.request();
            RequestBody oldRequestBody = request.body();
            Buffer requestBuffer = new Buffer();
            if (oldRequestBody != null) {
                oldRequestBody.writeTo(requestBuffer);
            }
            String oldBodyStr = requestBuffer.readUtf8();
            LogUtils.d(TAG, "原请求体：" + oldBodyStr);
            requestBuffer.close();
            MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
            //生成随机AES密钥并用serverPublicKey进行RSA加密
            String appAESKeyStr = GetKey.generateAESKey(32);
            LogUtils.d(TAG, "生成AES密钥：" + appAESKeyStr);
            //使用保存的服务器公钥加密生成的AES密钥
            String serverPublicKey = SPHelper.getString("serverPublicKey", "");
            LogUtils.d(TAG, "服务器RSA公钥：" + serverPublicKey);
            String appEncryptedKey = null;
            try {
                appEncryptedKey = RSAUtils.encryptByPublicKey(appAESKeyStr, serverPublicKey);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, "APP使用服务器RSA公钥加密AES失败");
            }
            LogUtils.d(TAG, "APP使用服务器RSA公钥加密AES：" + appEncryptedKey);
            //计算body的哈希，并使用app私钥RSA签名，确保数据没有被修改过
            LogUtils.d(TAG, "APP的RSA私钥：" + MyTimeApplication.privateKeyString);
            String appSignature = null;
            try {
                appSignature = RSAUtils.signature(oldBodyStr, MyTimeApplication.privateKeyString);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, "APP使用RSA私钥对请求体进行签名失败");
            }
            LogUtils.d(TAG, "APP使用RSA私钥对请求体进行签名：" + appSignature);
            //随机AES密钥加密oldBodyStr
            String newBodyStr = null;
            try {
                newBodyStr = AESUtils.encrypt(oldBodyStr, appAESKeyStr);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, "AES加密请求体失败");
            }
            LogUtils.d(TAG, "AES加密后的请求体：" + newBodyStr);
            RequestBody newBody = null;
            if (newBodyStr != null) {
                newBody = RequestBody.create(mediaType, newBodyStr);
            }
            //构造新的request，各项参数解释如下
            //appEncryptedKey   使用服务器RSA公钥加密后的AES密钥
            //appSignature      APP使用RSA密钥对请求体的签名
            //appPublicKey      APP的RSA公钥，提供给服务器加密服务端的AES密钥
            //serverPublicKey   服务端的RSA公钥，提供给服务器判断有没有过期
            if (newBody != null && appEncryptedKey != null && appSignature != null) {
                request = request.newBuilder()
                        .addHeader("Content-Type", Objects.requireNonNull(newBody.contentType()).toString())
                        .addHeader("Content-Length", String.valueOf(newBody.contentLength()))
                        .method(request.method(), newBody)
                        .addHeader("appEncryptedKey", appEncryptedKey)
                        .addHeader("appSignature", appSignature)
                        .addHeader("appPublicKey", MyTimeApplication.publicKeyString)
                        .addHeader("serverPublicKey", serverPublicKey)
                        .build();
            }
            //响应
            Response response = chain.proceed(request);
            //只有请求成功的返回码才经过加密，才需要走解密的逻辑
            if (response.code() == 200) {

                //判断头里面的code，如果发生RSA、AES密钥错误或者请求体Hash值不一致的情况，在这里拦截，重新生成response
                int code = Integer.valueOf(Objects.requireNonNull(response.header("code")));
                if (code == ErrorCode.SUCCESS) {
                    LogUtils.d(TAG, "请求成功，走正常流程");
                } else {
                    LogUtils.d(TAG, "Head提示有异常，创建自定义Response并返回");
                    return getErrorResponse(response, code);
                }

                //获取响应头，获取服务器使用APP的RSA公钥加密后的AES密钥
                String serverEncryptedKey = response.header("serverEncryptedKey");
                LogUtils.d(TAG, "服务器加密的AES：" + serverEncryptedKey);
                if (TextUtils.isEmpty(serverEncryptedKey)) {
                    return getErrorResponse(response, ErrorCode.DECRYPT_RESPONSE_BODY_FAILED);
                }
                String appPublicKey = response.header("appPublicKey");
                LogUtils.d(TAG, "APP的RSA公钥：" + appPublicKey);
                String serverPublicKey2 = response.header("serverPublicKey");
                SPHelper.save("serverPublicKey", serverPublicKey2);
                LogUtils.d(TAG, "服务器的RSA公钥：" + serverPublicKey2);

                // 判断APP的RSA公钥，如果一致则解析，不一致则需要APP重新登录
                if (MyTimeApplication.publicKeyString.equals(appPublicKey)) {
                    //用app的RSA私钥解密AES加密密钥
                    String serverDecryptedKey;
                    try {
                        serverDecryptedKey = RSAUtils.decryptByPrivateKey(serverEncryptedKey, MyTimeApplication.privateKeyString);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.d(TAG, "App的RSA私钥解密服务端加密的AES密钥失败");
                        return getErrorResponse(response, ErrorCode.DECRYPT_AES_KEY_FAILED);
                    }
                    LogUtils.d(TAG, "App的RSA私钥解密加密的AES密钥：" + serverEncryptedKey);
                    //用AES密钥解密oldResponseBodyStr
                    ResponseBody oldResponseBody = response.body();
                    String oldResponseBodyStr = null;
                    if (oldResponseBody != null) {
                        oldResponseBodyStr = oldResponseBody.string();
                    }
                    LogUtils.d(TAG, "服务器返回的数据原文：" + oldResponseBodyStr);
                    String newResponseBodyStr;
                    try {
                        newResponseBodyStr = AESUtils.decrypt(oldResponseBodyStr, serverDecryptedKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.d(TAG, "解密服务器返回的数据失败");
                        return getErrorResponse(response, ErrorCode.DECRYPT_RESPONSE_BODY_FAILED);
                    }
                    LogUtils.d(TAG, "解密服务器返回的数据：" + newResponseBodyStr);
                    if (oldResponseBody != null) {
                        oldResponseBody.close();
                    }
                    //构造新的response
                    ResponseBody newResponseBody = ResponseBody.create(mediaType, newResponseBodyStr);
                    response = response.newBuilder().body(newResponseBody).build();
                } else {
                    //构造空的response
                    return getErrorResponse(response, ErrorCode.KEY_RSA_CLIENT_EXPIRED);
                }
            }
            response.close();
            return response;
        }
    }

    private Response getErrorResponse(Response response, int errorCode) {
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        Result result = new Result();
        result.setCode(errorCode);
        result.setData(new Object());
        LogUtils.d(TAG, GsonUtils.convertJSON(result));
        ResponseBody newResponseBody = ResponseBody.create(mediaType, GsonUtils.convertJSON(result));
        response = response.newBuilder().body(newResponseBody).build();
        return response;
    }

    /**
     * 获取单例的NetClient对象
     *
     * @param baseUrl 基础Url
     * @param encrypt 是否需要加密
     * @return NetClient对象
     */
    public static synchronized NetClient getInstance(String baseUrl, boolean encrypt) {
        if (encrypt) {
            if (mEncryptNetClient == null || !defaultUrl.equals(baseUrl)) {
                mEncryptNetClient = new NetClient(baseUrl, true);
                defaultUrl = baseUrl;
            }
            LogUtils.d(TAG, "请求接口：" + baseUrl);
            return mEncryptNetClient;
        } else {
            if (mNetClient == null || !defaultUrl.equals(baseUrl)) {
                mNetClient = new NetClient(baseUrl, false);
                defaultUrl = baseUrl;
            }
            LogUtils.d(TAG, "BaseUrl" + baseUrl);
            return mNetClient;
        }
    }

    public MyTimeApi getZbsApi() {
        if (myTimeApi == null) {
            myTimeApi = mRetrofit.create(MyTimeApi.class);
        }
        return myTimeApi;
    }

    /**
     * 主账号基础Url不带项目名（用于图像链接中）
     */
    public static String getBaseUrl() {
        // 计算主服务器的URL路径
        String ip = SPHelper.getString("PrimaryServerIp", "");
        String port = SPHelper.getString("PrimaryServerPort", "");
        if (TextUtils.isEmpty(ip)) {
            ip = NetWork.SERVER_HOST_MAIN;
        }
        if (TextUtils.isEmpty(port)) {
            port = NetWork.SERVER_PORT_MAIN;
        }
        return ip + ":" + port + "/";
    }

    /**
     * 主账号基础Url带项目名
     */
    public static String getBaseUrlProject() {
        // 计算主服务器的URL路径
        String ip = SPHelper.getString("PrimaryServerIp", "");
        String port = SPHelper.getString("PrimaryServerPort", "");
        if (TextUtils.isEmpty(ip)) {
            ip = NetWork.SERVER_HOST_MAIN;
        }
        if (TextUtils.isEmpty(port)) {
            port = NetWork.SERVER_PORT_MAIN;
        }
        return ip + ":" + port + "/" + NetWork.PROJECT_MAIN + "/";
    }

}
