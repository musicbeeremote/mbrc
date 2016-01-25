package com.kelsos.mbrc.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ConnectionSettingsAdapter;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.kelsos.mbrc.utilities.RxBus;

public class ConnectionManagerActivity extends RoboAppCompatActivity
    implements SettingsDialogFragment.SettingsDialogListener {

  @Inject private RxBus bus;
  @Bind(R.id.connection_list) RecyclerView mRecyclerView;
  @Bind(R.id.toolbar) Toolbar mToolbar;

  private MaterialDialog mProgress;
  private Context mContext;

  @OnClick(R.id.connection_add) public void onAddButtonClick(View v) {
    SettingsDialogFragment settingsDialog = new SettingsDialogFragment();
    Bundle args = new Bundle();
    args.putInt("index", -1);
    settingsDialog.setArguments(args);
    settingsDialog.show(getSupportFragmentManager(), "settings_dialog");
  }

  @OnClick(R.id.connection_scan) public void onScanButtonClick(View v) {
    MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(mContext);
    mBuilder.title(R.string.progress_scanning);
    mBuilder.content(R.string.progress_scanning_message);
    mBuilder.progress(true, 0);
    mProgress = mBuilder.show();
    bus.post(MessageEvent.newInstance(UserInputEventType.StartDiscovery));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ui_activity_connection_manager);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);
  }

  @Override protected void onStart() {
    super.onStart();
    mContext = this;
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(R.string.connection_manager_title);
    }

  }

  @Override protected void onResume() {
    super.onResume();
    bus.register(ConnectionSettingsChanged.class, this::handleConnectionSettingsChange, false);
    bus.register(DiscoveryStopped.class, this::handleDiscoveryStopped, false);
    bus.register(NotifyUser.class, this::handleUserNotification, false);
  }

  @Override protected void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;
      default:
        return false;
    }
    return true;
  }

  @Override
  public void onDialogPositiveClick(SettingsDialogFragment dialog, ConnectionSettings settings) {
    bus.post(settings);
  }

  public void handleConnectionSettingsChange(ConnectionSettingsChanged event) {
    ConnectionSettingsAdapter mAdapter = new ConnectionSettingsAdapter(event.getSettings(), bus);
    mAdapter.setDefaultIndex(event.getDefaultIndex());
    mRecyclerView.setAdapter(mAdapter);
  }

  public void handleDiscoveryStopped(DiscoveryStopped event) {

    if (mProgress != null) {
      mProgress.dismiss();
    }

    String message;
    switch (event.getReason()) {
      case NO_WIFI:
        message = getString(R.string.con_man_no_wifi);
        break;
      case NOT_FOUND:
        message = getString(R.string.con_man_not_found);
        break;
      case COMPLETE:
        message = getString(R.string.con_man_success);
        break;
      default:
        message = getString(R.string.unknown_reason);
        break;
    }

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
  }

  public void handleUserNotification(NotifyUser event) {
    final String message =
        event.isFromResource() ? getString(event.getResId()) : event.getMessage();

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
  }
}
