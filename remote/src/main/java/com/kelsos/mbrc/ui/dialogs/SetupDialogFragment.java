package com.kelsos.mbrc.ui.dialogs;

import android.content.Intent;
import android.view.View;
import com.avast.android.dialogs.core.BaseDialogFragment;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.SettingsActivity;

public class SetupDialogFragment extends BaseDialogFragment {

    @Override protected Builder build(Builder builder) {
        builder.setMessage(R.string.dialog_application_setup);
        builder.setTitle(R.string.dialog_application_setup_title);
        builder.setPositiveButton(R.string.dialog_application_setup_positive, new View.OnClickListener() {
            @Override public void onClick(View v) {
                SetupDialogFragment.this.dismiss();
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        builder.setNegativeButton(R.string.dialog_application_setup_negative, new View.OnClickListener() {
            @Override public void onClick(View v) {
                SetupDialogFragment.this.getDialog().cancel();
            }
        });
        return builder;
    }
}
