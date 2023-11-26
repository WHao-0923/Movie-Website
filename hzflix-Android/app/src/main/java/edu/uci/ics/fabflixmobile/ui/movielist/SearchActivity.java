package edu.uci.ics.fabflixmobile.ui.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.singlePage.SingleMovie;
import org.json.JSONException;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private Button searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        editText = findViewById(R.id.editText);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = editText.getText().toString();
                String params = "title=&fullText="+searchText+"&pageSize=10&year=&director=&star=&genre=&sortBy=movieId&sortTitle=&page=";
                Log.d("search", params);
                Intent movieList = new Intent(SearchActivity.this, MovieListActivity.class);
                movieList.putExtra("params",params);
                startActivity(movieList);
            }
        });
    }
}