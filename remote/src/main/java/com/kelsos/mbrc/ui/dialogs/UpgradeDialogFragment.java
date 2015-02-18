package com.kelsos.mbrc.ui.dialogs;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.SettingsActivity;

public class UpgradeDialogFragment extends SimpleDialogFragment {
    private boolean isNewInstall;

    @Override protected Builder build(Builder builder) {
        final WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/update.html");
        builder.setView(webView)
                .setTitle(R.string.dialog_upgrade_title)
                .setNegativeButton(R.string.dialog_upgrade_negative, new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        UpgradeDialogFragment.this.getDialog().cancel();
                    }
                });

        if (isNewInstall) {
            builder.setPositiveButton(R.string.dialog_application_setup_positive, new View.OnClickListener() {
                @Override public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                }
            });
        }

        return builder;
    }

    public void setNewInstall(boolean newInstall) {
        isNewInstall = newInstall;
    }
}
