package com.kelsos.mbrc.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.connection_manager.ConnectionManagerActivity;
import com.kelsos.mbrc.ui.dialogs.WebViewDialog;
import com.kelsos.mbrc.utilities.RemoteUtils;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment {

  private static final int REQUEST_CODE = 15;
  private RxBus bus;
  private Context mContext;

  public static SettingsFragment newInstance(RxBus bus) {
    final SettingsFragment fragment = new SettingsFragment();
    fragment.setBus(bus);
    return fragment;
  }

  @Override public void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    addPreferencesFromResource(R.xml.application_settings);
    mContext = getActivity();

    final Preference reduceOnIncoming = findPreference(getString(R.string.settings_key_incoming_call_action));
    final Preference mOpenSource = findPreference(getString(R.string.preferences_open_source));
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

    if (reduceOnIncoming != null) {
      reduceOnIncoming.setOnPreferenceChangeListener((preference, newValue) -> {
        if (!hasPhonePermission()) {
          requestPhoneStatePermission();
        }
        return true;
      });
    }

    if (mManager != null) {
      mManager.setOnPreferenceClickListener(preference -> {
        startActivity(new Intent(mContext, ConnectionManagerActivity.class));
        return false;
      });
    }

    if (mVersion != null) {
      try {
        mVersion.setSummary(String.format(getResources().getString(R.string.settings_version_number),
            RemoteUtils.getVersion(mContext)));
      } catch (PackageManager.NameNotFoundException e) {
        Timber.d(e, "failed");
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      final Preference mShowNotification = findPreference(getResources().
          getString(R.string.settings_key_notification_control));
      if (mShowNotification != null) {
        mShowNotification.setOnPreferenceChangeListener((preference, newValue) -> {
          boolean value = (Boolean) newValue;
          if (!value) {
            bus.post(new MessageEvent(UserInputEventType.CancelNotification));
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

    if (mBuild != null) {
      mBuild.setSummary(BuildConfig.BUILD_TIME);
    }
    if (mRevision != null) {
      mRevision.setSummary(BuildConfig.GIT_SHA);
    }
  }

  public void requestPhoneStatePermission() {
    ActivityCompat.requestPermissions(getActivity(), new String[] {
        Manifest.permission.READ_PHONE_STATE
    }, REQUEST_CODE);
  }

  public boolean hasPhonePermission() {
    return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE)
        == PackageManager.PERMISSION_GRANTED;
  }

  private void showLicenseDialog() {
    Bundle args = new Bundle();
    args.putString(WebViewDialog.ARG_URL, "file:///android_asset/license.html");
    args.putInt(WebViewDialog.ARG_TITLE, R.string.musicbee_remote_license_title);
    WebViewDialog dialog = new WebViewDialog();
    dialog.setArguments(args);
    dialog.show(getActivity().getSupportFragmentManager(), "license_dialog");
  }

  private void showOpenSourceLicenseDialog() {
    Bundle args = new Bundle();
    args.putString(WebViewDialog.ARG_URL, "file:///android_asset/licenses.html");
    args.putInt(WebViewDialog.ARG_TITLE, R.string.open_source_licenses_title);
    WebViewDialog dialog = new WebViewDialog();
    dialog.setArguments(args);
    dialog.show(getActivity().getSupportFragmentManager(), "licenses_dialogs");
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        getActivity().finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void setBus(RxBus bus) {
    this.bus = bus;
  }
}
