package com.kelsos.mbrc.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;
import com.kelsos.mbrc.R;

public class UpgradeDialogFragment extends DialogFragment {
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/update.html");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(webView)
                .setTitle(R.string.dialog_upgrade_title)
                .setNegativeButton(R.string.dialog_upgrade_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UpgradeDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
