package nu.shout.shout.settings;

import nu.shout.shout.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

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
		
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		Preference clearAccount = (Preference) findPreference("acc_clear");
		clearAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Editor ed = settings.edit();
				ed.putString("nickname", null);
				ed.putString("password", null);
				ed.commit();
				return false;
			}
		});
	}
}
