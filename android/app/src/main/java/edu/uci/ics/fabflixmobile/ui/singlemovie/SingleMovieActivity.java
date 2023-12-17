package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.ListView;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import android.content.Intent;
import edu.uci.ics.fabflixmobile.databinding.SinglemoviePageBinding;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;



import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    TextView title;
    TextView year;
    TextView director;
    TextView rating;
    TextView genres;
    TextView stars;
    String movieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SinglemoviePageBinding binding = SinglemoviePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Use getRoot() to set the root view of the binding
        title = binding.title;
        year = binding.year;
        director = binding.director;
        rating = binding.rating;
        genres = binding.genres;
        stars = binding.stars;

        final String host = "18.191.77.53";
        final String port = "8443";
        final String domain = "122b-project";
        final String baseURL = "https://" + host + ":" + port + "/" + domain;
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        Intent intent = getIntent(); // get title search from main page
        if (intent != null) {
            movieId = intent.getStringExtra("movieId");
        }

        Log.d("movieId passed from movielist to single movie ", movieId);

        final StringRequest movieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + movieId,
                response -> {
                    try {
                        Log.d("before json", "json");
                        //JSONObject movieJson = new JSONObject(response);
                        JSONArray moviesArray = new JSONArray(response);
                        JSONObject movieJson = moviesArray.getJSONObject(0);

                        Log.d("after json", "json");

                        Log.d("JSON Response", response);

                        // Check for "null" before parsing
                        String yearString = movieJson.getString("year");
                        int yearInt = (yearString != null && !yearString.equals("null")) ? Integer.parseInt(yearString) : 0;

                        // Check for "null" before parsing
                        String ratingString = movieJson.getString("rating");
                        double ratingDouble = (ratingString != null && !ratingString.equals("null")) ? Double.parseDouble(ratingString) : 0.0;


                        Movie movie = new Movie(
                                movieJson.getString("id"), // replace "movie_id" with the correct key
                                movieJson.getString("title"),
                                yearInt,
                                movieJson.getString("director"),
                                ratingDouble,
                                movieJson.getString("genres"),
                                movieJson.getString("star")
                        );
                        // render info
                        title.setText(movie.getTitle());
                        Log.d("getTitle", movie.getTitle());
                        year.setText(movie.getYear() + "");
                        Log.d("getYear", movie.getYear() + "");
                        director.setText(movie.getDirector());
                        rating.setText(movie.getRating() + ""); // double + string
                        genres.setText(movie.getAllGenre());
                        stars.setText(movie.getAllStars());
                        Log.d("getTitle", movie.getTitle());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("single_movie.error", error.toString()));

        queue.add(movieRequest);
    }
}