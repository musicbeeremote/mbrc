package kelsos.mbremote;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AppSettings extends PreferenceActivity {

	public static final String REMOTE_SETTINGS = "mbremote_settings";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.application_settings);
	}
}
