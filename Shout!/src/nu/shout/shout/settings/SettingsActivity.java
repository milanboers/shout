package nu.shout.shout.settings;

import nu.shout.shout.R;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity {
	/**
	 * Uses deprecated methods because this is the only way in ActionBarSherlock. Should be replaced with Fragments when switching to API 11.
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}
}
