package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.ui.ForecastFragment;
import com.example.android.sunshine.app.ui.SettingsActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }

        // Initialize settings to default values
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem share = menu.findItem(R.id.share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) share.getActionProvider();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        }
        if (id == R.id.action_open_map){
            // Build map intent
            Intent openMap = new Intent(Intent.ACTION_VIEW);

            // Get user preference location
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String location = sharedPref.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            // Built location uri
            Uri mapInfo = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();

            openMap.setData(mapInfo);

            // Check if a maps app is installed and launch intent
            if(openMap.resolveActivity(getPackageManager()) != null){
                startActivity(openMap);
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
