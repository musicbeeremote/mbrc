package com.kelsos.mbrc.ui.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.base.BaseActivity;
import com.kelsos.mbrc.ui.fragments.SettingsFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.main_menu_title_settings);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, SettingsFragment.newInstance()).commit();
    }
}
