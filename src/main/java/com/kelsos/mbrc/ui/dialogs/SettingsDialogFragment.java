package com.kelsos.mbrc.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.AppPreferenceView;

public class SettingsDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.ui_dialog_settings, null))
                .setTitle(R.string.dialog_application_setup_title)
                .setPositiveButton(R.string.dialog_application_setup_positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .setNegativeButton(R.string.dialog_application_setup_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SettingsDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
