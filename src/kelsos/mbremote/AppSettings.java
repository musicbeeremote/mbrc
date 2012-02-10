package kelsos.mbremote;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class AppSettings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String REMOTE_SETTINGS = "mbremote_settings";

    private EditTextPreference hostEditTextPreference;
    private EditTextPreference portEditTextPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.application_settings);
        hostEditTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getApplicationContext().getString(R.string.settings_server_hostname));
        portEditTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getApplicationContext().getString(R.string.settings_server_port));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hostEditTextPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(this).getString(getApplicationContext().getString(R.string.settings_server_hostname), null));
        portEditTextPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(this).getString(getApplicationContext().getString(R.string.settings_server_port), null));
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getApplicationContext().getString(R.string.settings_server_hostname))) {
            hostEditTextPreference.setSummary(sharedPreferences.getString(getApplicationContext().getString(R.string.settings_server_hostname), null));
        } else if (key.equals(getApplicationContext().getString(R.string.settings_server_port))) {
            portEditTextPreference.setSummary(sharedPreferences.getString(getApplicationContext().getString(R.string.settings_server_port), null));
        }
    }
}
