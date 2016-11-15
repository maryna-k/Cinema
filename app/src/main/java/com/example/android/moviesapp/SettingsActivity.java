package com.example.android.moviesapp;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().
                replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_search_key)));
        }

        private void bindPreferenceSummaryToValue(Preference pref){
            pref.setOnPreferenceChangeListener(this);
            onPreferenceChange(pref, PreferenceManager.getDefaultSharedPreferences(pref.getContext())
                                                        .getString(pref.getKey(),""));
        }

        @Override
        public boolean onPreferenceChange(Preference pref, Object value){
            String stringValue = value.toString();
            ListPreference listPreference = (ListPreference) pref;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0)
                pref.setSummary(listPreference.getEntries()[prefIndex]);
            return true;
        }
    }
}
