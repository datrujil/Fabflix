package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        //TextView subtitle;
        TextView year;
        TextView director;
        TextView rating;
        TextView genres;
        TextView stars;

    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        //layout?
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.year = convertView.findViewById(R.id.year);
            viewHolder.director = convertView.findViewById(R.id.director);
            viewHolder.rating = convertView.findViewById(R.id.rating);
            viewHolder.genres = convertView.findViewById(R.id.genres);
            viewHolder.stars = convertView.findViewById(R.id.stars);

            //viewHolder.subtitle = convertView.findViewById(R.id.subtitle);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        /*viewHolder.title.setText(movie.getTitle());
        viewHolder.director.setText(movie.getDirector());
        double rating = movie.getRating();
        String ratingString = String.valueOf(rating); // Convert the double to a String
        viewHolder.rating.setText(ratingString);

        //3 GENRES, 3 STARS
        // Assuming movie.getGenres() returns an array of strings
        List<String> genresArray = movie.getGenres();
        // Convert the array to a comma-separated string
        String genresString = android.text.TextUtils.join(", ", genresArray);
        // Set the text of the TextView
        viewHolder.genres.setText(genresString);

        // Assuming movie.getGenres() returns an array of strings
        List<String> starsArray = movie.getStars();
        // Convert the array to a comma-separated string
        String starsString = android.text.TextUtils.join(", ", starsArray);
        // Set the text of the TextView
        viewHolder.stars.setText(starsString);
        //viewHolder.subtitle.setText(movie.getYear() + "");
        // Return the completed view to render on screen*/
        viewHolder.title.setText(movie.getTitle());
        //viewHolder.title.setOnClickListener(view -> single_movie_search());
        viewHolder.year.setText(movie.getYear() + "");
        viewHolder.director.setText(movie.getDirector() + "");
        viewHolder.rating.setText(movie.getRating() + "");
        viewHolder.stars.setText(movie.getStarsString() + "");
        viewHolder.genres.setText(movie.getGenresString() + "");
        return convertView;
    }
}