package com.kelsos.mbrc.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.DisplaySelection;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.DrawerEvent;
import com.kelsos.mbrc.ui.activities.FeedbackActivity;
import com.kelsos.mbrc.ui.activities.SettingsActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.fragment.RoboFragment;

public class DrawerFragment extends RoboFragment
    implements FragmentManager.OnBackStackChangedListener {

  @Inject private Bus bus;
  @Bind(R.id.navigation_view) NavigationView navigationView;

  private DrawerLayout drawerLayout;
  private DisplaySelection selection;
  private boolean backStackChanging;
  private SparseArray<DisplaySelection> displayArray;
  private MenuItem home;
  private MenuItem connectionStatus;

  public DrawerFragment() {
    displayArray = new SparseArray<>();
    displayArray.put(R.id.drawer_menu_home, DisplaySelection.HOME);
    displayArray.put(R.id.drawer_menu_library, DisplaySelection.LIBRARY);
    displayArray.put(R.id.drawer_menu_playlist, DisplaySelection.PLAYLISTS);
    displayArray.put(R.id.drawer_menu_now_playing, DisplaySelection.NOW_PLAYING);
    displayArray.put(R.id.drawer_menu_lyrics, DisplaySelection.LYRICS);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    selection = DisplaySelection.HOME;
    backStackChanging = false;

    if (savedInstanceState != null) {
      final int current = savedInstanceState.getInt("selection");
      this.selection = DisplaySelection.values()[current];
    }
    getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_drawer, container, false);
    ButterKnife.bind(this, view);

    navigationView.setNavigationItemSelectedListener(menuItem -> {
      final int itemId = menuItem.getItemId();
      final boolean mainHandled = handleMainMenu(itemId);

      if (mainHandled) {
        menuItem.setChecked(true);
      }

      return mainHandled || handleSubmenu(itemId);
    });
    connectionStatus = navigationView.getMenu().findItem(R.id.drawer_menu_connect);
    home = navigationView.getMenu().getItem(0);
    home.setChecked(true);

    return view;
  }

  private boolean handleMainMenu(int itemId) {
    boolean handled = false;
    final DisplaySelection display = displayArray.get(itemId);
    if (display != null) {
      handled = true;
      navigate(display);
    }
    return handled;
  }

  private void navigate(@Nullable DisplaySelection selection) {
    backStackChanging = true;
    DrawerEvent drawerEvent;
    if (this.selection != selection) {
      this.selection = selection;
      drawerEvent = new DrawerEvent(selection);
    } else {
      drawerEvent = new DrawerEvent();
    }

    bus.post(drawerEvent);
  }

  private boolean handleSubmenu(int itemId) {
    boolean handled = true;
    switch (itemId) {
      case R.id.drawer_menu_settings:
        onSettingsClicked();
        break;
      case R.id.drawer_menu_exit:
        onExitClicked();
        break;
      case R.id.drawer_menu_connect:
        onConnectClick();
        break;
      case R.id.drawer_menu_feedback:
        onFeedbackClicked();
        break;
      case R.id.drawer_menu_help:
        onHelpClicked();
        break;
      default:
        handled = false;
        break;
    }
    return handled;
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);

    if (drawerLayout == null) {
      drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
    }
  }

  private void onConnectClick() {
    bus.post(new MessageEvent(UserInputEventType.ResetConnection));
  }

  private void onSettingsClicked() {
    bus.post(new DrawerEvent());
    startActivity(new Intent(getActivity(), SettingsActivity.class));
  }

  private void onHelpClicked() {
    bus.post(new DrawerEvent());
    Intent openHelp = new Intent(Intent.ACTION_VIEW);
    openHelp.setData(Uri.parse("http://kelsos.net/musicbeeremote/help/"));
    startActivity(openHelp);
  }

  private void onExitClicked() {
    final Activity activity = getActivity();
    activity.stopService(new Intent(activity, Controller.class));
    activity.finish();
  }

  private void onFeedbackClicked() {
    bus.post(new DrawerEvent());
    final Activity activity = getActivity();
    activity.startActivity(new Intent(activity, FeedbackActivity.class));
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Subscribe public void handleConnectionStatusChange(final ConnectionStatusChangeEvent change) {
    if (connectionStatus == null) {
      return;
    }
    switch (change.getStatus()) {
      case Connection.OFF:
        connectionStatus.setTitle(R.string.drawer_connection_status_off);
        break;
      case Connection.ON:
        connectionStatus.setTitle(R.string.drawer_connection_status_active);
        break;
      default:
        connectionStatus.setTitle(R.string.drawer_connection_status_off);
        break;
    }
  }

  @Override public void onBackStackChanged() {
    if (!backStackChanging
        && getActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
      selection = DisplaySelection.HOME;
      home.setChecked(true);
    }
    backStackChanging = false;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putInt("selection", selection.ordinal());
    super.onSaveInstanceState(outState);
  }
}
