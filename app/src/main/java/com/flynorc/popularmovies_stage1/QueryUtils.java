package com.flynorc.popularmovies_stage1;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Flynorc on 25-Feb-18.
 */

public class QueryUtils {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_POSTER = "poster_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_PLOT = "overview";

    public static ArrayList<Movie> getMoviesFromApi(String urlString, final Context context) {
        URL url = createUrl(urlString, context);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e("GET NEWS", "Error closing input stream", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        //extract the news from the string that was returned from the API
        return extractMovies(jsonResponse, context);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl, Context context) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("create url", "Error with creating URL ", e);
            Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
            toast.show();
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * as learned in the course
     */
    private static String makeHttpRequest(URL url, final Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("make request", "Error response code: " + urlConnection.getResponseCode());

                //use a handler to create a toast from the background thread
                Handler handler =  new Handler(context.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        } catch (IOException e) {
            Log.e("make request", "Problem retrieving the news JSON results.", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Movie> extractMovies(String response, final Context context) {
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject data =  new JSONObject(response);
            JSONArray results = data.getJSONArray(KEY_RESULTS);

            //parse every item and if parsing is successful it is added to the list
            for(int i=0; i < results.length(); i++) {
                JSONObject movieJson = results.getJSONObject(i);
                movies.add(parseMovieJson(movieJson));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        // Return the list of movies (that were successfully parsed)
        return movies;
    }

    /*
     * helper function to parse the attributes of the movie from the JSON object
     * and return the Movie object
     */
    private static Movie parseMovieJson(JSONObject movieJson) {
        String title = movieJson.optString(KEY_TITLE, "");
        String releaseDate = movieJson.optString(KEY_RELEASE_DATE, "");
        String posterPath = movieJson.optString(KEY_POSTER, "");
        Float voteAverage = Float.parseFloat(movieJson.optString(KEY_VOTE_AVERAGE, "0"));
        String plot = movieJson.optString(KEY_PLOT, "");

        return new Movie(title, releaseDate, posterPath, voteAverage, plot);
    }


}