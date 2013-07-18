package com.kelsos.mbrc.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.squareup.otto.Bus;

import static android.app.AlertDialog.Builder;

public class AppPreferenceView extends RoboSherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject Bus bus;
    @Inject RemoteUtils rmUtils;
    private EditTextPreference hostEditTextPreference;
    private EditTextPreference portEditTextPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.main_menu_title_settings);
        addPreferencesFromResource(R.xml.application_settings);
        hostEditTextPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference(getString(R.string.settings_key_hostname));
        portEditTextPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference(getString(R.string.settings_key_port));

        final Context pContext = this;
        portEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (o.toString().equals("")) return false;
                int portNumber = Integer.parseInt(o.toString());
                if (portNumber < 1 || portNumber > 65535) {
                    final Builder alert = new Builder(pContext);
                    alert.setTitle(R.string.alert_invalid_range);
                    alert.setMessage(R.string.alert_invalid_port_number);
                    alert.setPositiveButton(android.R.string.ok, null);
                    alert.show();
                    return false;
                } else {
                    return true;
                }
            }
        });

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
                    startActivity(new Intent(pContext, ConnectionManagerActivity.class));
                    return false;
                }
            });
        }

        if (mVersion != null) {
            try {
                mVersion.setSummary(String.format(getResources().getString(R.string.settings_version_number), rmUtils.getVersion()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
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

    @Override protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        hostEditTextPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.settings_key_hostname), ""));
        portEditTextPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.settings_key_port), ""));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String hostname = sharedPreferences.getString(getString(R.string.settings_key_hostname), "");
        String port = sharedPreferences.getString(getString(R.string.settings_key_port), "");

        if (key.equals(getString(R.string.settings_key_hostname))) {
            hostEditTextPreference.setSummary(hostname);
        } else if (key.equals(getString(R.string.settings_key_port))) {
            portEditTextPreference.setSummary(port);
        }
        if (port.equals("") || hostname.equals("")) return;
        bus.post(new MessageEvent(UserInputEvent.SettingsChanged));
    }
}
