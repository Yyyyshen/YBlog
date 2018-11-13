package com.yshen.yblog;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yshen.yblog.constants.Constants;
import com.yshen.yblog.view.YBlogWebView;

public class MainActivity extends BaseActivity {

    YBlogWebView wv_main;
    SmartRefreshLayout srl_main;
    LinearLayout ll_main_title;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //处理标题栏
        initTitle();
        //加载页面
        initWeb();
        //下拉刷新
        doRefresh();
    }

    private void initView() {
        ll_main_title = findViewById(R.id.ll_main_title);
        wv_main = findViewById(R.id.wv_main);
        srl_main = findViewById(R.id.srl_main);
        tv_title = findViewById(R.id.tv_title);
        //约束字长
        tv_title.setMaxEms(10);
        tv_title.setEllipsize(TextUtils.TruncateAt.END);
        tv_title.setSingleLine(true);
    }

    /**
     * 标题栏初始透明，滑动渐变
     */
    private void initTitle() {
        ll_main_title.setAlpha(0);
        wv_main.setOnScrollChangedCallback(new YBlogWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int y) {
                if (y < 50) {
                    ll_main_title.setAlpha(0);
                } else {
                    ll_main_title.setAlpha(y / 256f);
                }
            }
        });
    }

    /**
     * 用WebView加载域名
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        wv_main.getSettings().setJavaScriptEnabled(true);
        wv_main.loadUrl(Constants.BLOG_URL);
        wv_main.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面跳转设置标题为等待
                tv_title.setText(getText(R.string.web_loading).toString());
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                //在手机端隐藏Github丝带，在onPageFinished中执行会有延迟，不美观，所以选择在此方法中进行
                String js = "javascript:(function() {document.getElementsByClassName('ribbon')[0].style.display='none'})()";
                view.loadUrl(js);
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载完显示标题
                tv_title.setText(view.getTitle());
                //页面加载完成结束刷新
                srl_main.finishRefresh(2000);
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     * 刷新处理
     */
    private void doRefresh() {
        srl_main.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                //刷新当前页面
                wv_main.reload();
                //结束刷新写在页面加载完成中
            }
        });
    }

    /**
     * 使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //监听用按键方法，keyCode监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作
        if (keyCode == KeyEvent.KEYCODE_BACK && wv_main.canGoBack()) {
            wv_main.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
