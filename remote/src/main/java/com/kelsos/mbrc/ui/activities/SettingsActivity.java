package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.fragments.SettingsFragment;
import com.kelsos.mbrc.utilities.RxBus;

public class SettingsActivity extends AppCompatActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Inject private RxBus bus;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(R.string.menu_settings);
    }


    final SettingsFragment fragment = SettingsFragment.newInstance(bus);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.content_wrapper, fragment)
        .commitAllowingStateLoss();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
