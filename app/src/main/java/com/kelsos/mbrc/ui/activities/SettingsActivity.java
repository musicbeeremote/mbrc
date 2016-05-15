package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.fragments.SettingsFragment;
import com.squareup.otto.Bus;
import roboguice.RoboGuice;

public class SettingsActivity extends AppCompatActivity {

  @Inject private Bus bus;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(this).injectMembers(this);
    setContentView(R.layout.settings_activity);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(R.string.nav_settings);

    final SettingsFragment fragment = SettingsFragment.newInstance(bus);
    getSupportFragmentManager().beginTransaction().replace(R.id.content_wrapper, fragment).commit();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    RoboGuice.destroyInjector(this);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
