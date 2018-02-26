package com.flynorc.popularmovies_stage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    private Movie movie;

    @BindView(R.id.movie_title_tv)
    TextView titleTv;

    @BindView(R.id.release_date_tv)
    TextView releaseDateTv;

    @BindView(R.id.vote_average_tv)
    TextView voteAverageTv;

    @BindView(R.id.plot_tv)
    TextView plotTv;

    @BindView(R.id.movie_poster)
    ImageView posterIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        /*
         * get the movie object from saved instance state if it exists
         * otherwise get it from the extra data passed along with the intent
         * if it can not be found, show a toast (error) and close the activity
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_movie_details_not_found, Toast.LENGTH_LONG);
                toast.show();
                finish();
                movie= null;
            } else {
                movie = (Movie) extras.getSerializable("movie");
            }
        } else {
            movie= (Movie) savedInstanceState.getSerializable("movie");
        }

        displayMovieInfo();
    }


    private void displayMovieInfo() {
        titleTv.setText(movie.getTitle());
        releaseDateTv.setText(movie.getReleaseDate());
        voteAverageTv.setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(movie.getVoteAverage()));
        plotTv.setText(movie.getPlot());
        Picasso.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_image185)
                .into(posterIv);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movie", movie);
        super.onSaveInstanceState(outState);
    }
}
