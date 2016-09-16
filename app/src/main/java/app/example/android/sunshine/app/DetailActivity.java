package app.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

import app.example.android.sunshine.app.ui.ForecastFragment;
import app.example.android.sunshine.app.ui.SettingsActivity;

public class DetailActivity extends ActionBarActivity{

    private ShareActionProvider mShareActionProvider;
    private String mShareString;
    private static Uri mDayForecastUri;
    final private String LOG_TAG = "Detail Activity";
    static final int FORECAST_DETAIL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Add fragment to activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.detail_display, new DetailFragment())
                    .commit();
        }
    }




    /********************* FRAGMENT ***************************************/

    public static class DetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        String mForecastStr;
        TextView mDetailText;

        ShareActionProvider mShareActionProvider;

        public DetailFragment(){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Find views
            mDetailText = (TextView) getActivity().findViewById(R.id.detail_text);
            // Initialize loader
            getLoaderManager().initLoader(FORECAST_DETAIL_LOADER, null, this);

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);

            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem share = menu.findItem(R.id.share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

            // If onLoadFinished happens before this, set share intent
            if (mForecastStr != null){
                mShareActionProvider.setShareIntent(createShareForecastIntent(mForecastStr));
            }
        }

        private Intent createShareForecastIntent(String shareString){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    shareString);
            return shareIntent;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Intent openSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(openSettings);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            // Get URI passed from Intent
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mDayForecastUri = intent.getData();
            }

            return new CursorLoader(getActivity(),
                    mDayForecastUri,
                    ForecastFragment.FORECAST_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Move to first and check if cursor is empty.
            if(!data.moveToFirst()){ return; }

            String date = Utility.formatDate(data.getLong(ForecastFragment.COL_WEATHER_DATE));
            String desc = data.getString(ForecastFragment.COL_WEATHER_DESC);
            String maxTemp = Utility.formatTemperature(
                    data.getInt(ForecastFragment.COL_WEATHER_MAX_TEMP),
                    Utility.isMetric(getActivity())
            );
            String minTemp = Utility.formatTemperature(
                    data.getInt(ForecastFragment.COL_WEATHER_MIN_TEMP),
                    Utility.isMetric(getActivity())
            );

            mForecastStr = String.format("%s - %s - %s/%s", date, desc, maxTemp, minTemp);
            mDetailText.setText(mForecastStr);

            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(createShareForecastIntent(mForecastStr));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mDetailText.setText("No data.");
        }
    }

}
