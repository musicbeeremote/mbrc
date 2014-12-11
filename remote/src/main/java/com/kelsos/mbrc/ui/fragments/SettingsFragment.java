package com.kelsos.mbrc.ui.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.webkit.WebView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.ui.activities.ConnectionManagerActivity;

/**
 * A {@link android.preference.PreferenceFragment} subclass.
 * Used on devices with API > 11;
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SettingsFragment extends PreferenceFragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.application_settings);

        final Preference mOpenSource = findPreference(getResources().getString(R.string.preferences_open_source));
        final Preference mManager = findPreference(getResources().getString(R.string.preferences_key_connection_manager));
        final Preference mVersion = findPreference(getResources().getString(R.string.settings_version));
        final Preference mBuild = findPreference(getResources().getString(R.string.pref_key_build_time));
        final Preference mRevision = findPreference(getResources().getString(R.string.pref_key_revision));
        if (mOpenSource != null) {
            mOpenSource.setOnPreferenceClickListener(preference -> {
                showOpenSourceLicenseDialog();
                return false;
            });
        }

        if (mManager != null) {
            mManager.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), ConnectionManagerActivity.class));
                return false;
            });
        }

        if (mVersion != null) {
            mVersion.setSummary(String.format(getResources().getString(R.string.settings_version_number), BuildConfig.VERSION_NAME));
        }

        if (mBuild != null) {
            mBuild.setSummary(BuildConfig.BUILD_TIME);
        }

        if (mRevision != null) {
            mRevision.setSummary(BuildConfig.GIT_SHA);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final Preference mShowNotification = findPreference(getResources().
                    getString(R.string.settings_key_notification_control));
            if (mShowNotification != null) {
                mShowNotification.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean value = (Boolean) newValue;
                    if (!value) {
                        new MessageEvent(UserInputEventType.CANCEL_NOTIFICATION);
                    }
                    return true;
                });
            }
        }

        final Preference mLicense = findPreference(getResources().getString(R.string.settings_key_license));
        if (mLicense != null) {
            mLicense.setOnPreferenceClickListener(preference -> {
                showLicenseDialog();
                return false;
            });
        }
    }

    private void showLicenseDialog() {
        final WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/license.html");
        new MaterialDialog.Builder(getActivity())
                .customView(webView)
                .positiveText(android.R.string.ok)
                .title("MusicBee Remote license")
                .build()
                .show();
    }

    private void showOpenSourceLicenseDialog() {
        final WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/licenses.html");
        new MaterialDialog.Builder(getActivity())
                .customView(webView)
                .positiveText(android.R.string.ok)
                .title("Open source licenses")
                .build()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
