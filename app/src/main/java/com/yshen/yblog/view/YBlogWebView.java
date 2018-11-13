package com.yshen.yblog.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 标题栏的滑动渐变需要监听WebView的滑动距离，所以重写后抛出接口
 */
public class YBlogWebView extends WebView {

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public YBlogWebView(final Context context) {
        super(context);
    }

    public YBlogWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public YBlogWebView(final Context context, final AttributeSet attrs,
                             final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl,
                                   final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(t);
        }
    }
    //调用入口
    public void setOnScrollChangedCallback(
            final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    //抛出接口
    public interface OnScrollChangedCallback {
        void onScroll(int y);
    }
}
