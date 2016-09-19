package app.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.android.sunshine.app.R;

public class DetailActivity extends ActionBarActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detail);
        // Add fragment to activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, new DetailFragment())
                    .commit();
        }
    }
}
