package com.liyuliang.mytime.activity.normal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.utils.RegexUtils;
import com.liyuliang.mytime.widget.MyToolbar;

/**
 * 通用HTML页面
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class HtmlActivity extends SwipeBackActivity {

    private Context mContext;
    private MyToolbar toolbar;
    private ProgressBar progressBarWebView;
    private WebView webViewProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        mContext = this;
        String url = getIntent().getStringExtra("URL");
        toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.WebPage, R.drawable.back_white, -1, -1, -1, onClickListener);
        webViewProtocol = findViewById(R.id.webView_protocol);
        progressBarWebView = findViewById(R.id.progress_bar_webView);
        initSettings();
        loadProtocol(RegexUtils.formatUrl(url));
    }

    /**
     * 加载页面配置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings() {
        com.tencent.smtt.sdk.WebSettings settings = webViewProtocol.getSettings();
        settings.setJavaScriptEnabled(true);
        // 设置缓存模式
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            settings.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // 提高渲染的优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 支持多窗口
        settings.setSupportMultipleWindows(true);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        settings.setAppCacheEnabled(true);
    }

    private void loadProtocol(String url) {
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webViewProtocol.loadUrl(url);
        webViewProtocol.setWebViewClient(new WebViewClient() {

            private String startUrl;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                startUrl = url;
            }

            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                // return true;  webview处理url是根据程序来执行的
                // return false; webview处理url是在webview内部执行
                // 如果是重定向的呢，我们就return false,不是重定向就return true
                if (startUrl != null && startUrl.equals(url)) {
                    view.loadUrl(url);
                } else {
                    //交给系统处理
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return true;
            }

            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
//                    view.loadUrl(url);
                    return null;
                }
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                String title = webView.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    toolbar.setTitle(title);
                }
            }
        });

        //监听网页加载
        webViewProtocol.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    cancelDialog();
                    progressBarWebView.setVisibility(View.GONE);
                } else {
                    // 加载中
                    showLoadingDialog(mContext, true);
                    progressBarWebView.setProgress(newProgress);
                    // 如果获取到了网页标题，则显示在toolbar上
                    String title = view.getTitle();
                    if (!TextUtils.isEmpty(title)) {
                        toolbar.setTitle(title);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webViewProtocol.canGoBack()) {
            webViewProtocol.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}