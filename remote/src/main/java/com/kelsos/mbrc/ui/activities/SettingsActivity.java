package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.fragments.SettingsFragment;
import com.squareup.otto.Bus;
import roboguice.activity.RoboActionBarActivity;

public class SettingsActivity extends RoboActionBarActivity {

  @Inject private Bus bus;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(R.string.main_menu_settings);

    final SettingsFragment fragment = SettingsFragment.newInstance(bus);
    getSupportFragmentManager().beginTransaction().replace(R.id.content_wrapper, fragment).commit();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
