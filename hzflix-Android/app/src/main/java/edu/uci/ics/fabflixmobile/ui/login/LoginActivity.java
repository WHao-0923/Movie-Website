package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.SearchActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "Fablix_hz_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        Log.d("onCreate","create");

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> {
            try {
                login();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void login() throws JSONException {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        Log.d("LoginUser", String.valueOf(username.getText()));
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("password",password.getText());
        jsonBody.put("email",username.getText());
        jsonBody.put("gRecaptchaResponse","NotUsingIt");
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    Log.d("login.success", response);
                    //Complete and destroy login activity once successful
                    finish();
                    // initialize the activity(page)/destination
                    Intent searchPage = new Intent(LoginActivity.this, SearchActivity.class);
                    // activate the list page.
                    startActivity(searchPage);
                },
                error -> {
                    // error
                    message.setText("Invalid email or password!");
                    Log.d("login.error", error.toString());
                }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}