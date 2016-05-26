package com.kelsos.mbrc.ui.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.controller.RemoteService;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.activities.nav.LibraryActivity;
import com.kelsos.mbrc.ui.activities.nav.LyricsActivity;
import com.kelsos.mbrc.ui.activities.nav.MainActivity;
import com.kelsos.mbrc.ui.activities.nav.NowPlayingActivity;
import com.kelsos.mbrc.ui.activities.nav.PlaylistActivity;
import com.kelsos.mbrc.ui.dialogs.SetupDialogFragment;
import com.kelsos.mbrc.ui.dialogs.UpgradeDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.RoboGuice;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  @Inject Bus bus;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.drawer_layout) DrawerLayout drawer;
  @BindView(R.id.nav_view) NavigationView navigationView;

  private TextView connectText;
  private ActionBarDrawerToggle toggle;
  private DialogFragment mDialog;

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  protected abstract int active();

  private boolean onConnectLongClick(View view) {
    ifNotRunningStartService();
    bus.post(new MessageEvent(UserInputEventType.ResetConnection));
    return true;
  }

  private void onConnectClick(View view) {
    ifNotRunningStartService();
    bus.post(new MessageEvent(UserInputEventType.StartConnection));
  }

  private void ifNotRunningStartService() {
    if (!isMyServiceRunning(RemoteService.class)) {
      startService(new Intent(this, RemoteService.class));
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    drawer.removeDrawerListener(toggle);
    RoboGuice.destroyInjector(this);
  }

  @Override public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    toggle.onConfigurationChanged(newConfig);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    toggle.syncState();
  }

  @Override public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_UP:
        return true;
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        return true;
      default:
        return super.onKeyUp(keyCode, event);
    }
  }

  @Subscribe public void onConnection(ConnectionStatusChange event) {
    int resId;
    switch (event.getStatus()) {
      case CONNECTION_OFF:
        resId = R.string.drawer_connection_status_off;
        break;
      case CONNECTION_ON:
        resId = R.string.drawer_connection_status_on;
        break;
      case CONNECTION_ACTIVE:
        resId = R.string.drawer_connection_status_active;
        break;
      default:
        resId = R.string.drawer_connection_status_off;
        break;
    }

    connectText.setText(resId);
  }

  @Subscribe public void showSetupDialog(DisplayDialog event) {
    if (mDialog != null) {
      return;
    }
    if (event.getDialogType() == DisplayDialog.SETUP) {
      mDialog = new SetupDialogFragment();
      mDialog.show(getSupportFragmentManager(), "SetupDialogFragment");
    } else if (event.getDialogType() == DisplayDialog.UPGRADE) {
      mDialog = new UpgradeDialogFragment();
      mDialog.show(getSupportFragmentManager(), "UpgradeDialogFragment");
    } else if (event.getDialogType() == DisplayDialog.INSTALL) {
      mDialog = new UpgradeDialogFragment();
      ((UpgradeDialogFragment) mDialog).setNewInstall(true);
      mDialog.show(getSupportFragmentManager(), "UpgradeDialogFragment");
    }
  }

  @Subscribe public void handleUserNotification(NotifyUser event) {
    final String message = event.isFromResource() ? getString(event.getResId()) : event.getMessage();

    View focus = getCurrentFocus();
    if (focus != null) {
      Snackbar.make(focus, message, Snackbar.LENGTH_SHORT).show();
    }
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_UP:
        bus.post(new MessageEvent(UserInputEventType.KeyVolumeUp));
        return true;
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        bus.post(new MessageEvent(UserInputEventType.KeyVolumeDown));
        return true;
      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  @Override public boolean onNavigationItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    drawer.closeDrawer(GravityCompat.START);
    navigate(itemId);
    return true;
  }

  private void navigate(int itemId) {

    if (active() == itemId) {
      return;
    }

    if (itemId == R.id.nav_home) {
      createBackStack(new Intent(this, MainActivity.class));
    } else if (itemId == R.id.nav_library) {
      createBackStack(new Intent(this, LibraryActivity.class));
    } else if (itemId == R.id.nav_now_playing) {
      createBackStack(new Intent(this, NowPlayingActivity.class));
    } else if (itemId == R.id.nav_playlists){
      createBackStack(new Intent(this, PlaylistActivity.class));
    } else if (itemId == R.id.nav_lyrics) {
      createBackStack(new Intent(this, LyricsActivity.class));
    } else if (itemId == R.id.nav_settings) {
      startActivity(new Intent(this, SettingsActivity.class));
    } else if (itemId == R.id.nav_help) {
      Intent openHelp = new Intent(Intent.ACTION_VIEW);
      openHelp.setData(Uri.parse("http://kelsos.net/musicbeeremote/help/"));
      startActivity(openHelp);
    } else if (itemId == R.id.nav_feedback) {
      startActivity(new Intent(this, FeedbackActivity.class));
    } else if (itemId == R.id.nav_exit) {
      stopService(new Intent(this, RemoteService.class));
      finish();
    }
  }

  private void createBackStack(Intent intent) {
    TaskStackBuilder builder = TaskStackBuilder.create(this);
    builder.addNextIntentWithParentStack(intent);
    builder.startActivities();
  }

  /**
   * Should be called after RoboGuice injections and Butterknife bindings.
   */
  public void setup() {
    ifNotRunningStartService();
    setSupportActionBar(toolbar);

    toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    drawer.addDrawerListener(toggle);
    drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    toggle.syncState();
    navigationView.setNavigationItemSelectedListener(this);

    View header = navigationView.getHeaderView(0);
    connectText = ButterKnife.findById(header, R.id.nav_connect_text);

    LinearLayout navConnect = ButterKnife.findById(header, R.id.nav_connect);
    navConnect.setOnClickListener(this::onConnectClick);
    navConnect.setOnLongClickListener(this::onConnectLongClick);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }

    navigationView.setCheckedItem(active());
  }
}

