package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.SettingsActivity;

public class UpgradeDialogFragment extends DialogFragment {
  private boolean isNewInstall;

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final WebView webView = new WebView(getActivity());
    webView.loadUrl("file:///android_asset/update.html");
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(webView, false);
    builder.title(R.string.dialog_upgrade_title);
    builder.negativeText(R.string.dialog_upgrade_negative);

    if (isNewInstall) {
      builder.positiveText(R.string.dialog_application_setup_positive);
    }
    builder.onPositive((dialog, which) -> startActivity(new Intent(getActivity(), SettingsActivity.class)));
    builder.onNegative((dialog, which) -> dialog.dismiss());
    return builder.build();
  }

  public void setNewInstall(boolean newInstall) {
    isNewInstall = newInstall;
  }
}
