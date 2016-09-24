package app.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

import app.example.android.sunshine.app.data.WeatherContract;
import app.example.android.sunshine.app.ui.ForecastFragment;
import app.example.android.sunshine.app.ui.SettingsActivity;

/**
 * Created by Jayson Dela Cruz on 9/19/2016.
 */
public class DetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    String mForecastStr;
    private ShareActionProvider mShareActionProvider;
    private String mShareString;
    private static Uri mDayForecastUri;
    private View mDetailView;
    final private String LOG_TAG = "Detail Activity";
    static final int FORECAST_DETAIL_LOADER = 0;
    private TextView mFriendlyDateView, mDateView, mHumidityView, mWindView, mPressureView, mHighTempView,
        mLowTempView, mDescriptionView;
    private ImageView mIconView;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private static final String[] DETAIL_COLUMNS = {
                    WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                    WeatherContract.WeatherEntry.COLUMN_DATE,
                    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                    WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                    WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                    WeatherContract.WeatherEntry.COLUMN_DEGREES,
                    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                    // This works because the WeatherProvider returns location data joined with
                            // weather data, even though they're stored in two different tables.
                                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
                    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // This was missing from the class but it gets the URI passed from the intent.
        if (getActivity().getIntent().getData() != null){
            mUri = getActivity().getIntent().getData();
        }

        // Restore arguments if they exist
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        // Inflate detail view
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconView = (ImageView) view.findViewById(R.id.detail_icon);
        mDescriptionView = (TextView) view.findViewById(R.id.detail_description);
        mFriendlyDateView = (TextView) view.findViewById(R.id.detail_date_friendly);
        mDateView = (TextView) view.findViewById(R.id.detail_date);
        mHumidityView = (TextView) view.findViewById(R.id.detail_humidity);
        mWindView = (TextView) view.findViewById(R.id.detail_wind);
        mPressureView = (TextView) view.findViewById(R.id.detail_pressure);
        mHighTempView = (TextView) view.findViewById(R.id.detail_temp_high);
        mLowTempView = (TextView) view.findViewById(R.id.detail_temp_low);

        return view;
    }

    void onLocationChanged( String newLocation){
        // replace the uri and restart the loader if the location has changed
        Uri uri = mUri;
        if (uri != null){
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(FORECAST_DETAIL_LOADER, null, this);
        }
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Initialize loader
        getLoaderManager().initLoader(FORECAST_DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
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
        if(mUri != null) {
            // Create the cursor loader using the member URI
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Move to first and check if cursor is empty.
        if(!data.moveToFirst()){ return; }

        // Read and format date
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);

        // Icon
        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Description
        mDescriptionView.setText(data.getString(COL_WEATHER_DESC));

        // High and low temp
        String maxTemp = Utility.formatTemperature(
                getActivity(),
                data.getInt(ForecastFragment.COL_WEATHER_MAX_TEMP),
                Utility.isMetric(getActivity())
        );
        mHighTempView.setText(maxTemp);
        String minTemp = Utility.formatTemperature(
                getActivity(),
                data.getInt(ForecastFragment.COL_WEATHER_MIN_TEMP),
                Utility.isMetric(getActivity())
        );
        mLowTempView.setText(minTemp);

        // Humidity
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Wind speed and direction
        float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Pressure
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));


        if (mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent("Nothing to share yet."));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

