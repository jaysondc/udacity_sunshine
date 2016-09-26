package app.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.R;

import app.example.android.sunshine.app.ui.ForecastFragment;
import app.example.android.sunshine.app.ui.SettingsActivity;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{

    private String mLocation;
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.weather_detail_container) != null){
            // This container will only be present on large sw600dp layouts
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity
            // by adding or replacing the detail fragment using a
            // fragment transaction
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        // Tell the forecast fragment, wherever it is, to use the one or two pane layout
        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        // Initialize settings to default values
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        mLocation = Utility.getPreferredLocation(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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


    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        // If the location has changed while we were away
        if (location != null && !location.equals(mLocation)){
            // Refresh forecast fragment
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if ( ff != null) {
                ff.onLocationChanged();
            }

            // Refresh detail fragment
            DetailFragment df = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( df != null){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        // Do stuff when an item is selected
        if (mTwoPane){
            // Launch detail fragment inside the second pane using a fragment transaction
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            // Launch detail activity normally
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
