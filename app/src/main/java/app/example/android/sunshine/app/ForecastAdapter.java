package app.example.android.sunshine.app;

/**
 * Created by Jayson Dela Cruz on 9/15/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

import app.example.android.sunshine.app.ui.ForecastFragment;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    // flag to determine if we want to use a separate view for "today"
    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" +
                Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
                Remember that these views are reused as needed.
             */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the appropriate layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutID = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutID = R.layout.list_item_forecast_today;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutID = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;

    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        String forecast, high, low;
        Long date;

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Draw weather icon
        int weatherCondId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int viewType = getItemViewType(cursor.getPosition());
        int resWeatherIcon;
        if (viewType == VIEW_TYPE_TODAY) {
            resWeatherIcon = Utility.getArtResourceForWeatherCondition(weatherCondId);
        } else {
            resWeatherIcon = Utility.getIconResourceForWeatherCondition(weatherCondId);
        }

        date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        high = Utility.formatTemperature(
                context,
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                Utility.isMetric(context));
        low = Utility.formatTemperature(
                context,
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP),
                Utility.isMetric(context));

        viewHolder.iconView.setImageResource(resWeatherIcon);
        viewHolder.iconView.setContentDescription(forecast);
        if (!mUseTodayLayout && cursor.getPosition() == 0) {
            viewHolder.dateView.setText(context.getString(R.string.today));
        } else {
            viewHolder.dateView.setText(Utility.getFriendlyDayString(context, date));
        }
        viewHolder.descriptionView.setText(forecast);
        viewHolder.highTempView.setText(high);
        viewHolder.lowTempView.setText(low);

    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

}