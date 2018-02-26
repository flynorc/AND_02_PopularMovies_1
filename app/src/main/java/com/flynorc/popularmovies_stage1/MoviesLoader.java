package com.flynorc.popularmovies_stage1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Flynorc on 24-Feb-18.
 */

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private String url;
    private WeakReference<Context> weakRefContext;

    public MoviesLoader(Context context, String url) {
        super(context);
        this.url = url;
        this.weakRefContext = new WeakReference<>(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if(url.isEmpty()) {
            return null;
        }

        return QueryUtils.getMoviesFromApi(url, weakRefContext.get());
    }
}
