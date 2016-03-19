package com.kelsos.mbrc.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.kelsos.mbrc.adapters.DeviceManagerAdapter;
import com.kelsos.mbrc.adapters.DeviceManagerAdapter.DeviceActionListener;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.presenters.DeviceManagerPresenter;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment.SettingsDialogListener;
import com.kelsos.mbrc.ui.views.DeviceManagerView;
import com.kelsos.mbrc.utilities.RxBus;
import java.util.List;
import roboguice.RoboGuice;

public class DeviceManagerActivity extends AppCompatActivity
    implements DeviceManagerView, SettingsDialogListener, DeviceActionListener {

  @Bind(R.id.connection_list) RecyclerView mRecyclerView;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Inject private RxBus bus;
  @Inject private DeviceManagerPresenter presenter;
  @Inject private DeviceManagerAdapter adapter;
  private MaterialDialog progressDialog;
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
    progressDialog = mBuilder.show();
    bus.post(MessageEvent.newInstance(UserInputEventType.StartDiscovery));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ui_activity_connection_manager);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    presenter.bind(this);
    adapter.setDeviceActionListener(this);
    setSupportActionBar(mToolbar);
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setAdapter(adapter);
    presenter.loadDevices();
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
    presenter.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.onPause();
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

  @Override public void onDialogPositiveClick(SettingsDialogFragment dialog, DeviceSettings settings) {
    presenter.saveSettings(settings);
  }

  @Override public void showDiscoveryResult(@DiscoveryStopped.Status long reason) {

    String message;
    if (reason == DiscoveryStopped.NO_WIFI) {
      message = getString(R.string.con_man_no_wifi);
    } else if (reason == DiscoveryStopped.NOT_FOUND) {
      message = getString(R.string.con_man_not_found);
    } else if (reason == DiscoveryStopped.SUCCESS) {
      message = getString(R.string.con_man_success);
    } else {
      message = getString(R.string.unknown_reason);
    }

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void dismissLoadingDialog() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  @Override public void showNotification(NotifyUser event) {
    final String message = event.isFromResource() ? getString(event.getResId()) : event.getMessage();

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void updateDevices(List<DeviceSettings> list) {
    adapter.updateDevices(list);
  }

  @Override public void onDelete(DeviceSettings settings) {
    presenter.deleteSettings(settings);
  }

  @Override public void onDefault(DeviceSettings settings) {
    presenter.setDefault(settings);
  }

  @Override public void onEdit(DeviceSettings settings) {
    SettingsDialogFragment settingsDialog = SettingsDialogFragment.newInstance(settings);
    settingsDialog.show(getSupportFragmentManager(), SettingsDialogFragment.TAG);
  }
}
