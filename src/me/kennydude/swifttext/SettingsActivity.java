package me.kennydude.swifttext;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Settings for app
 */
@SuppressWarnings( "deprecated" )
public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle bis){

		Utils.setupTheme(this);

		super.onCreate(bis);

		addPreferencesFromResource(R.xml.prefs);

		Preference p = findPreference("show_notify");
		p.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				sendBroadcast(new Intent(SettingsActivity.this, ApplySettingsReceiver.class));
				return true;

			}

		});

	}

}
