package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import com.afollestad.materialdialogs.MaterialDialog;
import roboguice.fragment.RoboDialogFragment;

public class WebViewDialog extends RoboDialogFragment {

  public final static String ARG_URL = "url";
  public final static String ARG_TITLE = "title";

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    final WebView webView = new WebView(getActivity());
    webView.loadUrl(getArguments().getString(ARG_URL));
    builder.customView(webView, false);
    builder.title(getArguments().getInt(ARG_TITLE));
    builder.positiveText(android.R.string.ok);

    builder.callback(new MaterialDialog.ButtonCallback() {
      @Override public void onPositive(MaterialDialog dialog) {
        WebViewDialog.this.dismiss();
      }
    });

    return builder.build();
  }
}
