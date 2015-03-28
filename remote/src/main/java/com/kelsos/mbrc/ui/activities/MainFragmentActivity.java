package com.kelsos.mbrc.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.enums.DisplayFragment;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.DrawerEvent;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.dialogs.SetupDialogFragment;
import com.kelsos.mbrc.ui.dialogs.UpgradeDialogFragment;
import com.kelsos.mbrc.ui.fragments.LyricsFragment;
import com.kelsos.mbrc.ui.fragments.MainFragment;
import com.kelsos.mbrc.ui.fragments.NowPlayingFragment;
import com.kelsos.mbrc.ui.fragments.SearchFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.activity.RoboActionBarActivity;

public class MainFragmentActivity extends RoboActionBarActivity {
  @Inject Bus bus;
  private ActionBarDrawerToggle mDrawerToggle;
  private DrawerLayout mDrawerLayout;
  private View mDrawerMenu;
  private DisplayFragment mDisplay;
  private boolean navChanged;
  private DialogFragment mDialog;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ui_main_container);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerMenu = findViewById(R.id.drawer_menu);

    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
        R.string.drawer_close) {
      public void onDrawerOpened(View view) {
        invalidateOptionsMenu();
      }

      public void onDrawerClosed(View view) {
        invalidateOptionsMenu();
        if (navChanged) {
          navigateToView();
        }
      }
    };

    mDrawerLayout.setDrawerListener(mDrawerToggle);
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, 1);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    if (savedInstanceState != null) {
      return;
    }

    navChanged = false;
    mDisplay = DisplayFragment.HOME;

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
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  private void navigateToView() {
    switch (mDisplay) {
      case HOME:
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
          onBackPressed();
        }
        break;
      case SEARCH:
        SearchFragment slsFragment = new SearchFragment();
        replaceFragment(slsFragment, "library_search");
        break;
      case NOW_PLAYING_LIST:
        NowPlayingFragment npFragment = new NowPlayingFragment();
        replaceFragment(npFragment, "now_playing_list");
        break;
      case LYRICS:
        LyricsFragment lFragment = new LyricsFragment();
        replaceFragment(lFragment, "lyrics");
        break;
    }
    navChanged = false;
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
    mDrawerToggle.syncState();
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
    switch (item.getItemId()) {
      case android.R.id.home:
        if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
          mDrawerLayout.closeDrawer(mDrawerMenu);
        } else {
          mDrawerLayout.openDrawer(mDrawerMenu);
        }
        return true;
      default:
        return false;
    }
  }

  @Subscribe public void ShowSetupDialog(DisplayDialog event) {
    if (mDialog != null) return;
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

  @Subscribe public void handleDrawerEvent(DrawerEvent event) {
    if (event.isCloseDrawer()) {
      closeDrawer();
    } else {
      navChanged = true;
      mDisplay = event.getNavigate();
      closeDrawer();
    }
  }

  private void closeDrawer() {
    if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
      mDrawerLayout.closeDrawer(mDrawerMenu);
    }
  }

  @Subscribe public void handleUserNotification(NotifyUser event) {
    final String message =
        event.isFromResource() ? getString(event.getResId()) : event.getMessage();

    new SnackBar.Builder(this).withMessage(message).withStyle(SnackBar.Style.INFO).show();
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
}
