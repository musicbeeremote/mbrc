package com.kelsos.mbrc.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.webkit.WebView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.squareup.otto.Bus;

public class AppPreferenceView extends RoboSherlockPreferenceActivity {
    @Inject Bus bus;
    @Inject RemoteUtils rmUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.main_menu_title_settings);
        addPreferencesFromResource(R.xml.application_settings);
        final Context mContext = this;

        final Preference mOpenSource = findPreference(getResources().getString(R.string.preferences_open_source));
        final Preference mManager = findPreference(getResources().getString(R.string.preferences_key_connection_manager));
        final Preference mVersion = findPreference(getResources().getString(R.string.settings_version));
        if (mOpenSource != null) {
            mOpenSource.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    showOpenSourceLicenseDialog();
                    return false;
                }
            });
        }

        if (mManager != null) {
            mManager.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(mContext, ConnectionManagerActivity.class));
                    return false;
                }
            });
        }

        if (mVersion != null) {
            try {
                mVersion.setSummary(String.format(getResources().getString(R.string.settings_version_number), rmUtils.getVersion()));
            } catch (PackageManager.NameNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }

        final Preference mShowNotification = findPreference(getResources().
                getString(R.string.settings_key_notification_control));
        mShowNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean value = (Boolean)newValue;
                        if (!value) {
                            bus.post(new MessageEvent(UserInputEvent.CancelNotification));
                        }
                        return true;
                    }
                });
    }

    private void showOpenSourceLicenseDialog() {
        final WebView webView = new WebView(this);
        webView.loadUrl("file:///android_asset/licenses.html");
        new AlertDialog.Builder(this)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle("Open source licenses")
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
