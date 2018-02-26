package com.flynorc.popularmovies_stage1;

import java.io.Serializable;

/**
 * Created by Flynorc on 24-Feb-18.
 */

public class Movie implements Serializable {

    private static final String BASE_POSTER_THUMB_PATH = "https://image.tmdb.org/t/p/w185";
    private static final String BASE_POSTER_PATH = "https://image.tmdb.org/t/p/w500";

    private String title;
    private String releaseDate;
    private String posterPath;
    private Float voteAverage;
    private String plot;

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public String getPlot() {
        return plot;
    }

    public Movie(String title, String releaseDate, String posterPath, Float voteAverage, String plot) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.plot = plot;
    }



    /*
     * helper for debugging
     */
    @Override
    public String toString() {
        return "title: " + title
            + ",\nrelease date: " + releaseDate
            + ",\nposterPath: " + posterPath
            + ",\nvote average: " + voteAverage.toString()
            + ",\nplot: " + plot;
    }

    public String getPosterThumbPath() {
        return BASE_POSTER_THUMB_PATH + posterPath;
    }

    public String getPosterPath() {
        return BASE_POSTER_PATH + posterPath;
    }

}
