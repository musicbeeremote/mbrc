package com.kelsos.mbrc.ui.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
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
import com.kelsos.mbrc.BuildConfig;
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
import com.kelsos.mbrc.ui.navigation.LibraryActivity;
import com.kelsos.mbrc.ui.navigation.LyricsActivity;
import com.kelsos.mbrc.ui.navigation.MainActivity;
import com.kelsos.mbrc.ui.navigation.NowPlayingActivity;
import com.kelsos.mbrc.ui.navigation.PlaylistListActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.util.Ln;

public class BaseActivity extends RoboAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  public static final int NAVIGATION_DELAY = 250;
  public static final int DEBUG_ORDER = 999;
  public static final int DEBUG_ITEM_ID = 890;
  public static final int DEBUG_ITEM_GROUP = 0;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.drawer_layout) DrawerLayout drawer;
  @Bind(R.id.navigation_view) NavigationView navigationView;
  @Inject private Bus bus;
  @Inject private Handler handler;
  private ActionBarDrawerToggle toggle;
  private DialogFragment mDialog;

  /**
   * This utility method handles Up navigation intents by searching for a parent activity and
   * navigating there if defined. When using this for an activity make sure to define both the
   * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
   * when the activity has a single parent activity. If the activity doesn't have a single parent
   * activity then don't define one and this method will use back button functionality. If "Up"
   * functionality is still desired for activities without parents then use
   * {@code syntheticParentActivity} to define one dynamically.
   *
   * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar
   * in Material Design guidelines.
   *
   * @param currentActivity Activity in use when navigate Up action occurred.
   * @param syntheticParentActivity Parent activity to use when one is not already configured.
   */
  public static void navigateUpOrBack(Activity currentActivity, Class<? extends Activity> syntheticParentActivity) {
    // Retrieve parent activity from AndroidManifest.
    Intent intent = NavUtils.getParentActivityIntent(currentActivity);

    // Synthesize the parent activity when a natural one doesn't exist.
    if (intent == null && syntheticParentActivity != null) {
      try {
        intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (intent == null) {
      // No parent defined in manifest. This indicates the activity may be used by
      // in multiple flows throughout the app and doesn't have a strict parent. In
      // this case the navigation up button should act in the same manner as the
      // back button. This will result in users being forwarded back to other
      // applications if currentActivity was invoked from another application.
      currentActivity.onBackPressed();
    } else {
      if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
        // Need to synthesize a backstack since currentActivity was probably invoked by a
        // different app. The preserves the "Up" functionality within the app according to
        // the activity hierarchy defined in AndroidManifest.xml via parentActivity
        // attributes.
        TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
      } else {
        // Navigate normally to the manifest defined "Up" activity.
        NavUtils.navigateUpTo(currentActivity, intent);
      }
    }
  }

  /**
   * Sends an event object.
   */
  protected void post(Object object) {
    bus.post(object);
  }

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  protected void setCurrentSelection(@IdRes int id) {
    navigationView.getMenu().findItem(id).setChecked(true);
  }

  protected void initialize() {
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    navigationView.setNavigationItemSelectedListener(this);

    if (BuildConfig.DEBUG) {
      navigationView.getMenu().add(DEBUG_ITEM_GROUP, DEBUG_ITEM_ID, DEBUG_ORDER, R.string.debug);
    }

    if (!isMyServiceRunning(Controller.class)) {
      startService(new Intent(this, Controller.class));
    }

    toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  }

  @Override protected void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override protected void onPause() {
    super.onPause();
    bus.unregister(this);
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
    handler.postDelayed(() -> navigate(item.getItemId()), NAVIGATION_DELAY);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void navigate(int id) {
    if (id == R.id.drawer_menu_home) {
      createBackStack(new Intent(this, MainActivity.class));
    } else if (id == R.id.drawer_menu_library) {
      createBackStack(new Intent(this, LibraryActivity.class));
    } else if (id == R.id.drawer_menu_playlist) {
      createBackStack(new Intent(this, PlaylistListActivity.class));
    } else if (id == R.id.drawer_menu_now_playing) {
      createBackStack(new Intent(this, NowPlayingActivity.class));
    } else if (id == R.id.drawer_menu_lyrics) {
      createBackStack(new Intent(this, LyricsActivity.class));
    } else if (id == R.id.drawer_menu_settings) {
      createBackStack(new Intent(this, SettingsActivity.class));
    } else if (id == R.id.drawer_menu_exit) {
      onExitClicked();
    } else if (id == R.id.drawer_menu_connect) {
      onConnectClick();
    } else if (id == R.id.drawer_menu_help) {
      createBackStack(new Intent(this, HelpActivity.class));
    } else if (id == DEBUG_ITEM_ID) {
      createBackStack(new Intent(this, DebugActivity.class));
    }
  }

  private void createBackStack(Intent intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      TaskStackBuilder builder = TaskStackBuilder.create(this);
      builder.addNextIntentWithParentStack(intent);
      builder.startActivities();
    } else {
      startActivity(intent);
      finish();
    }
  }

  private void onConnectClick() {
    bus.post(new MessageEvent(UserInputEventType.ResetConnection));
  }

  private void onHelpClicked() {
    Intent openHelp = new Intent(Intent.ACTION_VIEW);
    openHelp.setData(Uri.parse("http://kelsos.net/musicbeeremote/help/"));
    startActivity(openHelp);
  }

  private void onExitClicked() {
    Ln.v("[Menu] User pressed exit");
    stopService(new Intent(this, Controller.class));
    finish();
  }

  @Subscribe public void handleConnectionStatusChange(final ConnectionStatusChangeEvent change) {

    final TextView view = (TextView) navigationView.findViewById(R.id.drawer_menu_connect);
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
