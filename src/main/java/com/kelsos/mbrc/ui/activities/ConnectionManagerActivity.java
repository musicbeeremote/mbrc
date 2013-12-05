package com.kelsos.mbrc.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ConnectionSettingsAdapter;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.enums.SettingsAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.base.BaseActivity;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import roboguice.inject.InjectView;

public class ConnectionManagerActivity extends BaseActivity implements SettingsDialogFragment.SettingsDialogListener {
    @Inject Bus bus;
    @InjectView(R.id.connection_scan) Button scanButton;
    @InjectView(R.id.connection_add) Button addButton;
    @InjectView(R.id.connection_list) ListView connectionList;

    private static final int GROUP_ID = 56;
    private static final int DEFAULT = 11;
    private static final int EDIT = 12;
    private static final int DELETE = 13;

    private ProgressDialog mProgress;
    private Context mContext;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_connection_manager);
    }

    @Override protected void onStart() {
        super.onStart();
        bus.register(this);
        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.connection_manager_title);
        scanButton.setOnClickListener(scanListener);
        addButton.setOnClickListener(addListener);
        registerForContextMenu(connectionList);
        connectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bus.post(new ChangeSettings(position, SettingsAction.DEFAULT));
            }
        });
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(GROUP_ID, DEFAULT, 0, getString(R.string.connectivity_manager_default));
        menu.add(GROUP_ID, EDIT, 0, getString(R.string.connectivity_manager_edit));
        menu.add(GROUP_ID, DELETE, 0, getString(R.string.connectivity_manager_delete));
        menu.setHeaderTitle(getString(R.string.connectivity_manager_header));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case DEFAULT:
                bus.post(new ChangeSettings(mi.position, SettingsAction.DEFAULT));
                break;
            case EDIT:
                SettingsDialogFragment settingsDialog = new SettingsDialogFragment();
                ConnectionSettingsAdapter mAdapter = (ConnectionSettingsAdapter) connectionList.getAdapter();
                Bundle args = new Bundle();
                ConnectionSettings mSettings = mAdapter.getItem(mi.position);
                args.putString("address", mSettings.getAddress());
                args.putString("name", mSettings.getName());
                args.putInt("port", mSettings.getPort());
                args.putInt("index", mi.position);
                settingsDialog.setArguments(args);
                settingsDialog.show(getSupportFragmentManager(), "settings_dialog");
                break;
            case DELETE:
                bus.post(new ChangeSettings(mi.position, SettingsAction.DELETE));
                break;
        }
        return true;
    }

    Button.OnClickListener scanListener = new Button.OnClickListener() {
        @Override public void onClick(View view) {
            mProgress = ProgressDialog.show(mContext, getString(R.string.progress_scanning), getString(R.string.progress_scanning_message), true, false);
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

    @Override public void onDialogPositiveClick(DialogFragment dialog, ConnectionSettings settings) {
        bus.post(settings);
    }

    @Subscribe public void handleConnectionSettingsChange(ConnectionSettingsChanged event) {
        ConnectionSettingsAdapter mAdapter = new ConnectionSettingsAdapter(this,
                R.layout.ui_list_connection_settings, event.getmSettings());
        mAdapter.setDefaultIndex(event.getDefaultIndex());
        connectionList.setAdapter(mAdapter);
    }

    @Subscribe public void handleDiscoveryStopped(DiscoveryStopped event) {
        if (mProgress != null) {
            mProgress.hide();
        }
        String message = "";
        de.keyboardsurfer.android.widget.crouton.Style style = Style.INFO;
        switch (event.getReason()) {

            case NO_WIFI:
                message = getString(R.string.con_man_no_wifi);
                break;
            case NOT_FOUND:
                style = Style.ALERT;
                message = getString(R.string.con_man_not_found);
                break;
            case COMPLETE:
                style = Style.CONFIRM;
                message = getString(R.string.con_man_success);
                break;
        }

        Crouton.makeText(this, message, style).show();
    }

    @Subscribe public void handleUserNotification(NotifyUser event) {
        if (event.isFromResource()) {
            Crouton.makeText(this, event.getResId(), Style.INFO).show();
        } else {
            Crouton.makeText(this, event.getMessage(), Style.INFO).show();
        }
    }
}
