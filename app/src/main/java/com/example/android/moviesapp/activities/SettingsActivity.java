package com.example.android.moviesapp.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.moviesapp.R;

import butterknife.BindView;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);

        getFragmentManager().beginTransaction().
                replace(R.id.settings_content_frame, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
