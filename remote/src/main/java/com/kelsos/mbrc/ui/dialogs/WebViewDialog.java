package com.kelsos.mbrc.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.webkit.WebView;
import com.avast.android.dialogs.core.BaseDialogBuilder;
import com.avast.android.dialogs.core.BaseDialogFragment;

public class WebViewDialog extends BaseDialogFragment {

    protected final static String ARG_URL = "url";
    protected final static String ARG_TITLE = "title";

    @Override protected BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        final WebView webView = new WebView(getActivity());
        webView.loadUrl(getUrl());
        builder.setView(webView);
        builder.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
            @Override public void onClick(View view) {
                WebViewDialog.this.dismiss();
            }
        });
        builder.setTitle(getTitle());
        return builder;
    }

    public static WebDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new WebDialogBuilder(context, fragmentManager, WebViewDialog.class);
    }

    protected String getTitle() {
        return getArguments().getString(ARG_TITLE);
    }

    protected String getUrl() {
        return getArguments().getString(ARG_URL);
    }

    public static class WebDialogBuilder extends BaseDialogBuilder<WebDialogBuilder> {
        private String mTitle;
        private String mUrl;

        public WebDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends BaseDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        @Override protected WebDialogBuilder self() {
            return this;
        }

        public WebDialogBuilder setTitle(@StringRes int titleResourceId) {
            mTitle = mContext.getString(titleResourceId);
            return this;
        }

        public WebDialogBuilder setUrl(String url) {
            mUrl = url;
            return this;
        }

        @Override protected Bundle prepareArguments() {
            Bundle args = new Bundle();
            args.putString(WebViewDialog.ARG_URL, mUrl);
            args.putString(WebViewDialog.ARG_TITLE, mTitle);
            return args;
        }
    }
}
