package com.flynorc.popularmovies_stage1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Flynorc on 24-Feb-18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MovieViewHolder> {

    private List<Movie> movies;

    public RecyclerAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }


    @Override
    public RecyclerAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.setMovie(movie);

        Picasso.with(holder.moviePoster.getContext())
                .load(movie.getPosterThumbPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_image185)
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }



    /*
     * implement the view holder pattern using a custom class extending the RecyclerView.ViewHolder
     */
    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView moviePoster;
        private Movie movie;

        public MovieViewHolder(View itemView) {
            super(itemView);
            /*
             * NOTE to reviewer... I was not able to use butterknife library here
             * I used the same approach as with the DetailsActivity
             * and the reference to moviePoster inside onBindViewHolder was null
             */
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);

            itemView.setOnClickListener(this);
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onClick(View view) {
            //start an explicit intent to open DetailsActivity for the clicked movie
            Context context = view.getContext();

            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("movie", movie);
            context.startActivity(intent);
        }
    }
}
