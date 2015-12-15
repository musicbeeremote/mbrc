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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.dialogs.SetupDialogFragment;
import com.kelsos.mbrc.ui.dialogs.UpgradeDialogFragment;
import com.kelsos.mbrc.ui.fragments.LyricsFragment;
import com.kelsos.mbrc.ui.fragments.MainFragment;
import com.kelsos.mbrc.ui.fragments.NowPlayingFragment;
import com.kelsos.mbrc.ui.fragments.PlaylistListFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MainFragmentActivity extends RoboAppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Inject Bus bus;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.drawer_layout) DrawerLayout drawer;
  @Bind(R.id.navigation_view) NavigationView navigationView;
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

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ui_main_container);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    navigationView.setNavigationItemSelectedListener(this);

    if (!isMyServiceRunning(Controller.class)) {
      startService(new Intent(this, Controller.class));
    }

    toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    if (savedInstanceState != null) {
      return;
    }

    MainFragment mFragment = new MainFragment();
    mFragment.setArguments(getIntent().getExtras());

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, mFragment, "main_fragment");
    fragmentTransaction.commit();
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    toggle.onConfigurationChanged(newConfig);
  }

  private void replaceFragment(Fragment fragment, String tag) {

    FragmentManager fragmentManager = getSupportFragmentManager();
    int bsCount = fragmentManager.getBackStackEntryCount();

    for (int i = 0; i < bsCount; i++) {
      int bsId = fragmentManager.getBackStackEntryAt(i).getId();
      fragmentManager.popBackStack(bsId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, fragment);
    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
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

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
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
    Snackbar.make(toolbar, message, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
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
    int id = item.getItemId();
    if (id == R.id.drawer_menu_home) {
      if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
        onBackPressed();
      }
    } else if (id == R.id.drawer_menu_library) {
      BrowseFragment browseFragment = BrowseFragment.newInstance();
      replaceFragment(browseFragment, "library");
    } else if (id == R.id.drawer_menu_playlist) {
      PlaylistListFragment playlistFragment = PlaylistListFragment.newInstance();
      replaceFragment(playlistFragment, "playlist");
    } else if (id == R.id.drawer_menu_now_playing) {
      NowPlayingFragment npFragment = new NowPlayingFragment();
      replaceFragment(npFragment, "now_playing");
    } else if (id == R.id.drawer_menu_lyrics) {
      LyricsFragment lFragment = new LyricsFragment();
      replaceFragment(lFragment, "lyrics");
    } else if (id == R.id.drawer_menu_settings) {
      onSettingsClicked();
    } else if (id == R.id.drawer_menu_exit) {
      onExitClicked();
    } else if (id == R.id.drawer_menu_connect) {
      onConnectClick();
    } else if (id == R.id.drawer_menu_feedback) {
      onFeedbackClicked();
    } else if (id == R.id.drawer_menu_help) {
      onHelpClicked();
    }

    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void onConnectClick() {
    bus.post(new MessageEvent(UserInputEventType.ResetConnection));
  }

  private void onSettingsClicked() {
    startActivity(new Intent(this, SettingsActivity.class));
  }

  private void onHelpClicked() {
    Intent openHelp = new Intent(Intent.ACTION_VIEW);
    openHelp.setData(Uri.parse("http://kelsos.net/musicbeeremote/help/"));
    startActivity(openHelp);
  }

  private void onExitClicked() {
    stopService(new Intent(this, Controller.class));
    finish();
  }

  private void onFeedbackClicked() {
    startActivity(new Intent(this, FeedbackActivity.class));
  }

  @Subscribe public void handleConnectionStatusChange(final ConnectionStatusChangeEvent change) {

    final TextView view = (TextView)navigationView.findViewById(R.id.drawer_menu_connect);
    if (view == null) {
      return;
    }
    switch (change.getStatus()) {
      case Connection.OFF:
        view.setText(R.string.drawer_connection_status_off);
        break;
      case Connection.ON:
        view.setText(R.string.drawer_connection_status_active);
        break;
      default:
        view.setText(R.string.drawer_connection_status_off);
        break;
    }
  }
}
