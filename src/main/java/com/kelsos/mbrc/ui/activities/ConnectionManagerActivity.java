package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ConnectionSettingsAdapter;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

public class ConnectionManagerActivity extends RoboSherlockFragmentActivity implements SettingsDialogFragment.SettingsDialogListener {
    @Inject Bus bus;
    @InjectView(R.id.connection_scan) Button scanButton;
    @InjectView(R.id.connection_add) Button addButton;
    @InjectView(R.id.connection_list) ListView connectionList;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_connection_manager);
    }

    @Override protected void onStart() {
        super.onStart();
        bus.register(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.connection_manager_title);
        scanButton.setOnClickListener(scanListener);
        addButton.setOnClickListener(addListener);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    Button.OnClickListener scanListener = new Button.OnClickListener() {
        @Override public void onClick(View view) {

        }
    };

    Button.OnClickListener addListener = new Button.OnClickListener() {
        @Override public void onClick(View view) {
            SettingsDialogFragment settingsDialog = new SettingsDialogFragment();
            settingsDialog.show(getSupportFragmentManager(),"settings_dialog");
        }
    };

    @Override public void onDialogPositiveClick(DialogFragment dialog, ConnectionSettings settings) {
        bus.post(settings);
    }

    @Subscribe public void handleConnectionSettingsChange(ConnectionSettingsChanged event) {
        connectionList.setAdapter(new ConnectionSettingsAdapter(this, R.layout.ui_list_connection_settings, event.getmSettings()));
    }
}
