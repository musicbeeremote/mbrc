package kelsos.mbremote.Views;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import kelsos.mbremote.R;

public class AppPreferenceView extends RoboSherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	private EditTextPreference hostEditTextPreference;
    private EditTextPreference portEditTextPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.main_menu_title_settings);
        addPreferencesFromResource(R.xml.application_settings);
        hostEditTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getApplicationContext().getString(R.string.settings_key_hostname));
        portEditTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getApplicationContext().getString(R.string.settings_key_port));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hostEditTextPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(this).getString(getApplicationContext().getString(R.string.settings_key_hostname), null));
        portEditTextPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(this).getString(getApplicationContext().getString(R.string.settings_key_port), null));
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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
        if (key.equals(getApplicationContext().getString(R.string.settings_key_hostname))) {
            hostEditTextPreference.setSummary(sharedPreferences.getString(getApplicationContext().getString(R.string.settings_key_hostname), null));
        } else if (key.equals(getApplicationContext().getString(R.string.settings_key_port))) {
            portEditTextPreference.setSummary(sharedPreferences.getString(getApplicationContext().getString(R.string.settings_key_port), null));
        }
    }
}
