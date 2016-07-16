package com.example.android.sunshine.app.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jayson Dela Cruz on 7/15/2016.
 */
public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here

        try{
            // Get entire forecast
            JSONObject weather = new JSONObject(weatherJsonStr);
            // Get per-day weather
            JSONArray weatherArray = weather.getJSONArray("list");
            // Get specified day weather
            JSONObject day = weatherArray.getJSONObject(dayIndex);
            // Get temp data
            JSONObject temp = day.getJSONObject("temp");
            // Get max temp
            Double maxTemp = temp.getDouble("max");

            return maxTemp;


        } catch(JSONException e) {

        } finally{

        }



        return -1;
    }
}
