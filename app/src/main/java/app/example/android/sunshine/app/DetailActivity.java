package app.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

import app.example.android.sunshine.app.ui.SettingsActivity;

public class DetailActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;
    private String mShareString;
    final private String LOG_TAG = "Detail Activity";

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailfragment, menu);

        MenuItem share = menu.findItem(R.id.share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

        if(mShareActionProvider != null){
            Intent shareIntent = createShareForecastIntent(mShareString);
            mShareActionProvider.setShareIntent(shareIntent);
        }
        else{
            Log.d(LOG_TAG, "Share Action Provider is null for some reason.");
        }
        return true;
    }

    private Intent createShareForecastIntent(String shareString){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                shareString);
        return shareIntent;
    }

    public void setShareString(String string){
        mShareString = string;
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

        return super.onOptionsItemSelected(item);
    }




    /********************* FRAGMENT ***************************************/

    public static class DetailFragment extends android.support.v4.app.Fragment{

        public DetailFragment(){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();

            // Find views
            TextView mDetailText = (TextView) getActivity().findViewById(R.id.detail_text);
            // Assign views
            mDetailText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            // Set text to be usable in the activity level
            ((DetailActivity) getActivity()).setShareString(intent.getStringExtra(Intent.EXTRA_TEXT));
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

}
