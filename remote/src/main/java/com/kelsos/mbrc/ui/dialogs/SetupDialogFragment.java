package com.kelsos.mbrc.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import com.kelsos.mbrc.R;

public class SetupDialogFragment extends DialogFragment {
    final DialogInterface.OnClickListener onPositiveClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    };

    final DialogInterface.OnClickListener onNegativeClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            SetupDialogFragment.this.getDialog().cancel();
        }
    };

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_application_setup);
        builder.setPositiveButton(R.string.dialog_application_setup_positive, onPositiveClick);
        builder.setNegativeButton(R.string.dialog_application_setup_negative, onNegativeClick);
        return builder.create();
    }
}
