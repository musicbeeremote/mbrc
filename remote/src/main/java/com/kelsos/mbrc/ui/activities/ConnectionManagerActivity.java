package com.kelsos.mbrc.ui.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import com.github.mrengineer13.snackbar.SnackBar;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ConnectionSettingsAdapter;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.enums.SettingsAction;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStatus;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.events.ui.SettingsChange;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.kelsos.mbrc.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class ConnectionManagerActivity extends RoboActionBarActivity
		implements SettingsDialogFragment.SettingsDialogListener {

    @InjectView(R.id.connection_scan)
	private Button scanButton;

    @InjectView(R.id.connection_add)
	private Button addButton;

    @InjectView(R.id.connection_list)
	private RecyclerView mRecyclerView;

	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

    private static final int GROUP_ID = 56;
    private static final int DEFAULT = 11;
    private static final int EDIT = 12;
    private static final int DELETE = 13;
    private SnackBar mSnackBar;

    private ProgressDialog mProgress;
    private Context mContext;

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

        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.connection_manager_title);
        scanButton.setOnClickListener(v -> {
			mProgress = ProgressDialog.show(mContext, getString(R.string.progress_scanning),
					getString(R.string.progress_scanning_message), true, false);
			Events.Messages.onNext(new Message(UserInputEventType.START_DISCOVERY));
        });

        addButton.setOnClickListener(view -> {
			SettingsDialogFragment settingsDialog = SettingsDialogFragment.newInstance(-1);
			settingsDialog.show(getFragmentManager(), "settings_dialog");
		});

		AndroidObservable.bindActivity(this, Events.DiscoveryStatusNotification)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::handleDiscoveryStatusChange, Logger::LogThrowable);

		AndroidObservable.bindActivity(this, Events.ConnectionSettingsChangedNotification)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::handleConnectionSettingsChange, Logger::LogThrowable);

		AndroidObservable.bindActivity(this, Events.UserNotification)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::handleUserNotification, Logger::LogThrowable);

		mAdapter = new ConnectionSettingsAdapter(new ArrayList<>());
		mRecyclerView.setAdapter(mAdapter);

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
    public void onDialogPositiveClick(DialogFragment dialog, ConnectionSettings settings) {
		final SettingsChange change = new SettingsChange(settings.getIndex() < 0
				? SettingsAction.NEW
				: SettingsAction.EDIT, settings);

		Events.SettingsChangeNotification.onNext(change);

		dialog.dismiss();
	}

    public void handleConnectionSettingsChange(ConnectionSettingsChanged event) {
        mAdapter = new ConnectionSettingsAdapter(event.getmSettings());
		((ConnectionSettingsAdapter) mAdapter).setDefaultIndex(event.getDefaultIndex());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void handleDiscoveryStatusChange(DiscoveryStatus event) {
        if (mProgress != null) {
            mProgress.hide();
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
                return;
        }
        mSnackBar.show(message);
    }

    public void handleUserNotification(NotifyUser event) {
        String message = event.isFromResource()
				? getString(event.getResId())
				: event.getMessage();

        mSnackBar.show(message);
    }
}
