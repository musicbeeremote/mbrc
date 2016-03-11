package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.SettingsActivity;

public class SetupDialogFragment extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.content(R.string.dialog_application_setup);
    builder.title(R.string.dialog_application_setup_title);
    builder.positiveText(R.string.dialog_application_setup_positive);
    builder.negativeText(R.string.dialog_application_setup_negative);
    builder.onPositive((dialog, which) -> {
      dialog.dismiss();
      startActivity(new Intent(getActivity(), SettingsActivity.class));
    });
    builder.onNegative((dialog, which) -> dialog.dismiss());

    return builder.build();
  }
}
