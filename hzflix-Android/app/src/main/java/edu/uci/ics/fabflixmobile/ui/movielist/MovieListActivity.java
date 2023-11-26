package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.singlePage.SingleMovie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "Fablix_hz_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    private int page = 1;

    private String params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        Intent intent = getIntent();
        params = intent.getStringExtra("params");
        // 获取按钮的引用
        Button previousButton = findViewById(R.id.previous_page_button);
        Button nextButton = findViewById(R.id.next_page_button);

        displayInfo();

        // 为“Previous”按钮设置监听器
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理“Previous”按钮的点击事件
                page = max(page-1,1);
                displayInfo();
            }
        });

        // 为“Next”按钮设置监听器
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理“Next”按钮的点击事件
                page ++;
                displayInfo();
            }
        });
    }

    private void displayInfo(){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest singleMovieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/main_page?"+params+page,
                response -> {
                    Log.d("Movie-List", "get data success");
                    try {
                        JSONArray movieList = new JSONArray(response);
                        displayMovieInfo(movieList);
                    } catch (JSONException e) {
                        Log.e("Movie-List", "Json parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("Movie-List", error.toString());
                }) ;
        // important: queue.add is where the login request is actually sent
        queue.add(singleMovieRequest);
    }

    private void displayMovieInfo(JSONArray jsonArray) throws JSONException {
        ArrayList<Movie> movies = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String title = jsonObject.getString("title");
            short year = Short.parseShort(jsonObject.getString("year"));
            String director = jsonObject.getString("director");
            String movieId = jsonObject.getString("id");

            List<String> genres = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                String genreKey = "genre" + j + "_name";
                if (jsonObject.has(genreKey) && !jsonObject.getString(genreKey).isEmpty()) {
                    genres.add(jsonObject.getString(genreKey));
                }
            }

            List<String> stars = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                String starKey = "star" + j + "_name";
                if (jsonObject.has(starKey) && !jsonObject.getString(starKey).isEmpty()) {
                    stars.add(jsonObject.getString(starKey));
                }
            }

            movies.add(new Movie(title, year, director, genres, stars, movieId));
        }

        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            Intent singleMoviePage = new Intent(MovieListActivity.this, SingleMovie.class);
            singleMoviePage.putExtra("movie_id",movie.getMovieId());
            startActivity(singleMoviePage);
        });

    }
}