package com.kelsos.mbrc.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.github.mrengineer13.snackbar.SnackBar;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.enums.DisplayFragment;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.ui.DrawerSelection;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.ui.fragments.DrawerFragment;
import com.kelsos.mbrc.ui.fragments.LyricsFragment;
import com.kelsos.mbrc.ui.fragments.MainFragment;
import com.kelsos.mbrc.ui.fragments.PlaylistFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseFragment;
import com.kelsos.mbrc.ui.fragments.queue.CurrentQueueFragment;
import com.kelsos.mbrc.util.Logger;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainFragmentActivity extends RoboAppCompatActivity {
  private ActionBarDrawerToggle mDrawerToggle;
  private DrawerLayout mDrawerLayout;
  private View mDrawerMenu;
  private DisplayFragment mDisplay;
  private boolean navChanged;
  private MenuItem favoriteItem;
  private DrawerFragment mDrawerFragment;

  @SuppressWarnings("Annotator") @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ui_main_container);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerMenu = findViewById(R.id.drawer_menu);
    mDrawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.drawer_menu);

    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
        R.string.drawer_close) {
      public void onDrawerClosed(View view) {
        invalidateOptionsMenu();
        if (navChanged) {
          navigateToView();
        }
      }

      public void onDrawerOpened(View view) {
        invalidateOptionsMenu();
      }
    };

    mDrawerLayout.setDrawerListener(mDrawerToggle);
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    if (savedInstanceState != null) {
      return;
    }

    navChanged = false;
    mDisplay = DisplayFragment.HOME;

    MainFragment mFragment = new MainFragment();
    mFragment.setArguments(getIntent().getExtras());

    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, mFragment, "main_fragment");
    fragmentTransaction.commit();

    mDrawerFragment.getDrawerSelectionObservable()
        .subscribe(this::handleDrawerEvent, Logger::logThrowable);
    Events.userMessageSub
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::handleUserNotification, Logger::logThrowable);

    Events.trackInfoSub
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleTrackInfoChange, Logger::logThrowable);
  }

  public void handleTrackInfoChange(final TrackInfoChange change) {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setTitle(change.getTitle());
    actionBar.setSubtitle(change.getArtist());
    actionBar.setDisplayShowTitleEnabled(true);
  }

  private void closeDrawer() {
    if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
      mDrawerLayout.closeDrawer(mDrawerMenu);
    }
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    favoriteItem = menu.findItem(R.id.action_bar_favorite);
    return true;
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
      case R.id.actionbar_help:
        Intent openHelp = new Intent(Intent.ACTION_VIEW);
        openHelp.setData(Uri.parse("http://kelsos.net/musicbeeremote/help/"));
        startActivity(openHelp);
        return true;
      case R.id.action_bar_favorite:
        return true;
      case R.id.popup_auto_dj:
        return true;
      case R.id.popup_scrobble:
        return true;
      default:
        return false;
    }
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mDrawerToggle.syncState();
  }

  public void handleDrawerEvent(DrawerSelection event) {
    if (event.isCloseDrawer()) {
      closeDrawer();
    } else {
      navChanged = true;
      mDisplay = event.getNavigate();
      closeDrawer();
    }
  }

  private void replaceFragment(Fragment fragment, String tag) {
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, fragment);
    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  private void navigateToView() {
    switch (mDisplay) {
      case HOME:
        MainFragment mainFragment = new MainFragment();
        replaceFragment(mainFragment, "main_fragment");
        break;
      case SEARCH:
        BrowseFragment slsFragment = new BrowseFragment();
        replaceFragment(slsFragment, "library_search");
        break;
      case NOW_PLAYING_LIST:
        CurrentQueueFragment npFragment = new CurrentQueueFragment();
        replaceFragment(npFragment, "now_playing_list");
        break;
      case LYRICS:
        LyricsFragment lFragment = new LyricsFragment();
        replaceFragment(lFragment, "lyrics");
        break;
      case PLAYLIST:
        PlaylistFragment plFragment = new PlaylistFragment();
        replaceFragment(plFragment, "playlist");
        break;
      case SETTINGS:
        Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(openSettingsIntent);
        break;
      default:
        break;
    }
    navChanged = false;
  }

  public void handleUserNotification(NotifyUser event) {
    String message = event.isFromResource() ? getString(event.getResId()) : event.getMessage();

    new SnackBar.Builder(this).withMessage(message)
        .withStyle(SnackBar.Style.INFO)
        .withDuration(SnackBar.SHORT_SNACK)
        .show();
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_UP:
        Events.messages.onNext(new Message(EventType.KEY_VOLUME_UP));
        return true;
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        Events.messages.onNext(new Message(EventType.KEY_VOLUME_DOWN));
        return true;
      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  @Override public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_UP:
        Events.messages.onNext(new Message(EventType.KEY_VOLUME_UP));
        return true;
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        Events.messages.onNext(new Message(EventType.KEY_VOLUME_DOWN));
        return true;
      default:
        return super.onKeyUp(keyCode, event);
    }
  }

  @SuppressWarnings("UnusedDeclaration") public void handleLfmStatusChange(LfmStatus status) {
    if (favoriteItem == null) {
      return;
    }
    switch (status) {
      case LOVED:
        favoriteItem.setIcon(R.drawable.ic_action_rating_favorite);
        break;
      case BANNED:
        favoriteItem.setIcon(R.drawable.ic_media_lfm_banned);
        break;
      case NORMAL:
        favoriteItem.setIcon(R.drawable.ic_action_rating_favorite_disabled);
        break;
      default:
        favoriteItem.setIcon(R.drawable.ic_action_rating_favorite_disabled);
        break;
    }
  }
}
