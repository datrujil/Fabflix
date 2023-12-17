package edu.uci.ics.fabflixmobile.ui.movielist;
import android.view.View;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.ListView;
import android.widget.Button;

import android.widget.Toast;
import android.text.TextUtils;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;



import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {
    ListView movieList;
    ArrayList<Movie> movies = new ArrayList<>();
    Button prevBtn;
    Button nextBtn;
    String titleSearch;
    int itemPerPage = 10;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Use getRoot() to set the root view of the binding
        movieList = binding.movielist;
        prevBtn = binding.prev;
        nextBtn = binding.next;
        getMovie(); // process movies, 10 item, page 1 at beginning
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                page--;
                if(page <= 0) {
                    page = 1;
                    @SuppressLint("DefaultLocale") String message = "First page";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                getMovie();
                Log.d("Page", String.valueOf(page));
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movies != null && movies.size() < itemPerPage){
                    @SuppressLint("DefaultLocale") String message = "End of page";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    //Log.d("end page", String.valueOf(page));

                } else {
                    page++;
                    getMovie();
                }

            }
        });


    }


    private void getMovie() {
        movies.clear();
        final String host = "18.191.77.53";
        final String port = "8443";
        final String domain = "122b-project";
        final String baseURL = "https://" + host + ":" + port + "/" + domain;
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;


        Intent intent = getIntent(); // get title search from main page
        if (intent != null) {
            titleSearch = intent.getStringExtra("titleSearch");
        }
         //request type is POST
        final StringRequest movieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-genre?byTitle=" + titleSearch +
                        "&page=" + page + "&pageSize=" + itemPerPage,
                //baseURL: "/api/single-genre?genreId=${genresId}&startWith=${startWith}&byTitle=${byTitle}&byYear=${byYear}&byDirector=${byDirector}&byStar=${byStar}&firstChoice=${firstChoice}&firstPriorityType=${firstPriorityType}&secondChoice=${secondChoice}&secondPriorityType=${secondPriorityType}&page=${currentPage}&pageSize=${currentItemsPerPage}`,
        response -> {
            // TODO: should parse the json response to redirect to appropriate functions
            //  upon different response value.
            //
            Log.d("response in movieList before try", response);
            Log.d("search title from mainpage:", titleSearch);
            Log.d("the response is", response);
            try {
                JSONArray moviesArray = new JSONArray(response);
                //Log.d("movieArr length = ", String.valueOf(moviesArray.length()));
                if (moviesArray.length() > 0)
                {
                    // log to check
                // process movie here. add to arr?
                    for (int i = 0; i <moviesArray.length(); i++)
                    {
                        JSONObject movieJson = moviesArray.getJSONObject(i);

                        // Check for "null" before parsing
                        String yearString = movieJson.getString("year");
                        int year = (yearString != null && !yearString.equals("null")) ? Integer.parseInt(yearString) : 0;

                        // Check for "null" before parsing
                        String ratingString = movieJson.getString("rating");
                        double rating = (ratingString != null && !ratingString.equals("null")) ? Double.parseDouble(ratingString) : 0.0;



                        movies.add(new Movie(
                                        movieJson.getString("id"), // replace "movie_id" with the correct key
                                        movieJson.getString("title"),
                                        year,
                                        movieJson.getString("director"),
                                        rating,
                                        movieJson.getString("genres"),
                                        movieJson.getString("star")
                                ));
                        //Movie movie = new Movie(movieJson);
                        //movies.add(movie);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("MovieListStatus", "calladapter");
            // adapter
            //MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);


            movieList.setAdapter(adapter); //
            movieList.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                Intent singlemovieIntent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                singlemovieIntent.putExtra("movieId", movie.getId()); // pass the id to single page
                // Start the new activity
                startActivity(singlemovieIntent);

            });

        }, error -> {
            // error
            Log.d("login.error", error.toString());
        });
        queue.add(movieRequest);
          }
}