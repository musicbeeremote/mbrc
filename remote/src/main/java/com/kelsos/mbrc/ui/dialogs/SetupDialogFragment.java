package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.SettingsActivity;
import roboguice.fragment.RoboDialogFragment;

public class SetupDialogFragment extends RoboDialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.content(R.string.dialog_application_setup);
    builder.title(R.string.dialog_application_setup_title);
    builder.positiveText(R.string.dialog_application_setup_positive);
    builder.negativeText(R.string.dialog_application_setup_negative);
    builder.callback(new MaterialDialog.ButtonCallback() {
      @Override public void onPositive(MaterialDialog dialog) {
        SetupDialogFragment.this.dismiss();
        startActivity(new Intent(getActivity(), SettingsActivity.class));
      }

      @Override public void onNegative(MaterialDialog dialog) {
        SetupDialogFragment.this.dismiss();
      }
    });
    return builder.build();
  }
}
