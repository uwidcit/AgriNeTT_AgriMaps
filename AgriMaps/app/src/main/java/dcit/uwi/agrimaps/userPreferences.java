package dcit.uwi.agrimaps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by kiran on 3/23/15.
 */
public class userPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int MAX_VALUE = 4000;
    private int MIN_VALUE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        ListView preferenceList = getListView();
        Button backButton = new Button(this);
        backButton.setText("Back");

        preferenceList.addFooterView(backButton);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Preference button = (Preference)findPreference("welcomeScreen");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent p = new Intent(getBaseContext(), WelcomeScreen.class);
                startActivity(p);
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("radius".equals(key)) {
            String valueString = sharedPreferences.getString(key, "");
            int value = Integer.parseInt(valueString);
            if (value > MAX_VALUE) {
                EditTextPreference p = (EditTextPreference) findPreference(key);
                p.setText("" + MAX_VALUE);
                Toast t = Toast.makeText(this, "Maximum amount limited to " + MAX_VALUE + " meters", Toast.LENGTH_SHORT);
                t.show();
            } else if (value < MIN_VALUE) {
                EditTextPreference p = (EditTextPreference) findPreference(key);
                p.setText("" + MIN_VALUE);
                Toast t = Toast.makeText(this, "Minimum amount limited to " + MIN_VALUE + " meters", Toast.LENGTH_SHORT);
                t.show();
            } else {
                Toast t = Toast.makeText(this, "Radius set to " + value + " meters", Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }
}
