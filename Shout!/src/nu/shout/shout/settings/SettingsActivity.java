package nu.shout.shout.settings;

import nu.shout.shout.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
		
		Preference clearAccount = (Preference) findPreference("acc_clear");
		clearAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SettingsActivity.this.clearAccount();
				return true;
			}
		});
	}
	
	private void clearAccount() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder = builder.setMessage(R.string.pref_acc_clear_conf);
		builder = builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
				
				Editor ed = settings.edit();
				ed.putString("nickname", null);
				ed.putString("password", null);
				ed.commit();
			}
		});
		builder = builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Ignore if no was clicked
			}
		});
		builder.show();
	}
}
