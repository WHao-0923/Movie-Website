package edu.uci.ics.fabflixmobile.ui.singlePage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SingleMovie extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "Fablix_hz_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);
        Intent intent = getIntent();
        if (intent != null) {
            String movie_id = intent.getStringExtra("movie_id"); // 使用相同的键来检索数据
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;
            Log.d("Single-Movie", movie_id);
            final StringRequest singleMovieRequest = new StringRequest(
                    Request.Method.GET,
                    "https://54.183.194.96:8443/Fablix-hz" + "/api/single-movie?movie_id="+movie_id,
                    response -> {
                        Log.d("Single-Movie", "get data success");
                        try {
                            JSONObject movie = new JSONObject(response);
                            displayMovieInfo(movie);
                        } catch (JSONException e) {
                            Log.e("Single-Movie", "Json parsing error: " + e.getMessage());
                        }
                    },
                    error -> {
                        Log.d("Single-Movie", error.toString());
                    }) ;
            // important: queue.add is where the login request is actually sent
            queue.add(singleMovieRequest);
        }
    }

    private void displayMovieInfo(JSONObject movie) throws JSONException {
        TextView titleView = findViewById(R.id.movie_title);
        TextView yearView = findViewById(R.id.movie_year);
        TextView directorView = findViewById(R.id.movie_director);
        TextView genresView = findViewById(R.id.movie_genres);
        TextView starsView = findViewById(R.id.movie_stars);

        titleView.setText(movie.getString("movie_title"));
        yearView.setText(movie.getString("movie_year"));
        directorView.setText(movie.getString("movie_director"));

        // 显示类型
        JSONArray genresArray = movie.getJSONArray("genres");
        StringBuilder genres = new StringBuilder();
        for (int i = 0; i < genresArray.length(); i++) {
            JSONObject genre = genresArray.getJSONObject(i);
            genres.append(genre.getString("genre"));
            if (i < genresArray.length() - 1) {
                genres.append(", ");
            }
        }
        genresView.setText(genres.toString());

        // 显示明星
        JSONArray starsArray = movie.getJSONArray("stars");
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < starsArray.length(); i++) {
            JSONObject star = starsArray.getJSONObject(i);
            stars.append(star.getString("star_name"));
            if (i < starsArray.length() - 1) {
                stars.append(", ");
            }
        }
        starsView.setText(stars.toString());
    }
}