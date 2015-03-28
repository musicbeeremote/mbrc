package com.kelsos.mbrc.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ConnectionSettingsAdapter;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.activity.RoboActionBarActivity;

public class ConnectionManagerActivity extends RoboActionBarActivity
    implements SettingsDialogFragment.SettingsDialogListener {
  @Inject Bus bus;
  private MaterialDialog mProgress;

  private Context mContext;
  @InjectView(R.id.connection_list) RecyclerView mRecyclerView;
  @InjectView(R.id.toolbar) Toolbar mToolbar;

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
    bus.post(new MessageEvent(UserInputEventType.StartDiscovery));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ui_activity_connection_manager);
    ButterKnife.inject(this);
    setSupportActionBar(mToolbar);
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);
  }

  @Override protected void onStart() {
    super.onStart();
    bus.register(this);
    mContext = this;
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(R.string.connection_manager_title);
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

  @Subscribe public void handleConnectionSettingsChange(ConnectionSettingsChanged event) {
    ConnectionSettingsAdapter mAdapter = new ConnectionSettingsAdapter(event.getSettings(), bus);
    mAdapter.setDefaultIndex(event.getDefaultIndex());
    mRecyclerView.setAdapter(mAdapter);
  }

  @Subscribe public void handleDiscoveryStopped(DiscoveryStopped event) {

    if (mProgress != null) {
      mProgress.dismiss();
    }

    String message = "";
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
    }

    new SnackBar.Builder(this).withMessage(message).withStyle(SnackBar.Style.INFO).show();
  }

  @Subscribe public void handleUserNotification(NotifyUser event) {
    final String message =
        event.isFromResource() ? getString(event.getResId()) : event.getMessage();

    new SnackBar.Builder(this).withMessage(message).withStyle(SnackBar.Style.INFO).show();
  }
}
