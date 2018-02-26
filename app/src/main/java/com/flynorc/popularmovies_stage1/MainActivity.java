package com.flynorc.popularmovies_stage1;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int NR_GRID_COLUMNS = 2;
    private static final int MOVIES_LOADER_ID = 43; //just like the answer to anything, but 1 bigger (better) :)
    private static final String MOVIE_API_BASE_URL = "https://api.themoviedb.org/3/movie";

    @BindView(R.id.no_results)
    TextView noResultsView;

    @BindView(R.id.results_loading)
    ProgressBar loadingSpinner;

    @BindView(R.id.movies_recycler_view)
    RecyclerView moviesRecyclerView;

    private ConnectivityManager connectivityManager;
    private RecyclerAdapter moviesRecyclerAdapter;
    private LoaderManager loaderManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupSharedPreferences();

        //store reference to connectivity manager
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();

        //set up the recycler view
        List<Movie> movies = new ArrayList<>();
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, NR_GRID_COLUMNS));
        moviesRecyclerAdapter = new RecyclerAdapter(movies);
        moviesRecyclerView.setAdapter(moviesRecyclerAdapter);

        fetchMovies();
    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void fetchMovies() {
        if(checkConnectivity()) {
            // show loading spinner and hide the no results text
            loadingSpinner.setVisibility(View.VISIBLE);
            noResultsView.setVisibility(View.GONE);

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIES_LOADER_ID, null, this);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /*
     * helper method to check if user has internet access
     */
    private boolean checkConnectivity() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    /*
     * implementation of LoaderManager.LoaderCallbacks interface
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        //get the url part saved in shared preferences (sorted by popular vs top rated)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortUrlParam = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular_value));

        //build up the URL from MOVIE_API_BASE_URL, the parameters from the shared preferences and api key
        Uri baseUri = Uri.parse(MOVIE_API_BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendPath(sortUrlParam);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

        return new MoviesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        loadingSpinner.setVisibility(View.GONE);

        //pass the results (or an empty ArrayList) to the recyclerAdapter
        if (data == null) {
            moviesRecyclerAdapter.setMovies(new ArrayList<Movie>());
            noResultsView.setVisibility(View.VISIBLE);
        }
        else {
            moviesRecyclerAdapter.setMovies(data);
            noResultsView.setVisibility(View.GONE);
        }

        //notify the adapter to take the changes
        moviesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        moviesRecyclerAdapter.setMovies(new ArrayList<Movie>());
        moviesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            fetchMovies();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
