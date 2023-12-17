package edu.uci.ics.fabflixmobile.ui.mainpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.MainPageBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class MainPageActivity extends AppCompatActivity {

    private EditText searchTitle;
    //private TextView inputSearch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainPageBinding binding = MainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchTitle = binding.searchText;
        final Button searchButton = binding.searchButton;
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search() {
        Log.d("search.success", "search");
        finish();
        Intent MovieListPage = new Intent(MainPageActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("titleSearch", searchTitle.getText().toString()); // what we search
        startActivity(MovieListPage);

    }
}