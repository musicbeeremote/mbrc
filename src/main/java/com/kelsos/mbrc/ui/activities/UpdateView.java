package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.kelsos.mbrc.R;

public class UpdateView extends RoboSherlockActivity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_update);
        getSupportActionBar().setTitle(R.string.update_view);
    }

    @Override public void onStart() {
        super.onStart();
    }
}
