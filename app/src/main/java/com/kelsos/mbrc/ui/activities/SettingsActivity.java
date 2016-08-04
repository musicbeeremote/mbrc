package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.ui.fragments.SettingsFragment;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class SettingsActivity extends FontActivity {

  @Inject RxBus bus;
  private Scope scope;

  @Override protected void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    scope.installModules(new SmoothieActivityModule(this));
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.settings_activity);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(R.string.nav_settings);
    }

    final SettingsFragment fragment = SettingsFragment.newInstance(bus);
    getSupportFragmentManager().beginTransaction().replace(R.id.content_wrapper, fragment).commit();
  }

  @Override protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
