package com.kelsos.mbrc.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import roboguice.inject.InjectView;

public class ConnectionManagerActivity extends RoboActionBarActivity implements SettingsDialogFragment.SettingsDialogListener {
    @Inject Bus bus;
    @InjectView(R.id.connection_scan) Button scanButton;
    @InjectView(R.id.connection_add) Button addButton;

    private MaterialDialog mProgress;
    private Context mContext;
    private SnackBar mSnackBar;
    @InjectView(R.id.connection_list)
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_connection_manager);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mSnackBar = new SnackBar(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override protected void onStart() {
        super.onStart();
        bus.register(this);
        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.connection_manager_title);
        scanButton.setOnClickListener(scanListener);
        addButton.setOnClickListener(addListener);
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

    Button.OnClickListener scanListener = new Button.OnClickListener() {
        @Override public void onClick(View view) {
            MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(mContext);
            mBuilder.title(R.string.progress_scanning);
            mBuilder.content(R.string.progress_scanning_message);
            mBuilder.progress(true, 0);
            mProgress = mBuilder.show();
            bus.post(new MessageEvent(UserInputEventType.StartDiscovery));
        }
    };

    Button.OnClickListener addListener = new Button.OnClickListener() {
        @Override public void onClick(View view) {
            SettingsDialogFragment settingsDialog = new SettingsDialogFragment();
            Bundle args = new Bundle();
            args.putInt("index", -1);
            settingsDialog.setArguments(args);
            settingsDialog.show(getSupportFragmentManager(), "settings_dialog");
        }
    };

    @Override public void onDialogPositiveClick(SettingsDialogFragment dialog, ConnectionSettings settings) {
        bus.post(settings);
    }

    @Subscribe public void handleConnectionSettingsChange(ConnectionSettingsChanged event) {
        ConnectionSettingsAdapter mAdapter = new ConnectionSettingsAdapter(event.getmSettings(), bus);
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

        mSnackBar.show(message);
    }

    @Subscribe public void handleUserNotification(NotifyUser event) {
        final String message = event.isFromResource()
                ? getString(event.getResId())
                : event.getMessage();

        mSnackBar.show(message);
    }


}
