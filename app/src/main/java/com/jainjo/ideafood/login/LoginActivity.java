package com.jainjo.ideafood.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.util.AppPreferences;
import com.jainjo.ideafood.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText usernameInput;
    EditText passwordInput;
    Button loginButton;
    ProgressBar progressBar;

    RequestQueue queue;
    StringRequest stringRequest;
    JsonObjectRequest jsonRequest;

    //String csrfToken;
    //String sessionCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( validateFields() ) {
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String,String> params = new HashMap<String, String>();
                    //params.put("_token", csrfToken);
                    params.put("username", usernameInput.getText().toString());
                    params.put("password", passwordInput.getText().toString());

                    //queue = AppUtils.getInstance(LoginActivity.this).getRequestQueue();
                    //stringRequest = new LoginStringRequest(getResources().getString(R.string.login_url), params,sessionCookie,LoginActivity.this,LoginActivity.this);
                    jsonRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            getResources().getString(R.string.login_url),
                            new JSONObject(params),
                            LoginActivity.this,
                            LoginActivity.this);
                    //queue.add(stringRequest);
                    AppUtils.getInstance(LoginActivity.this).addToRequestQueue(jsonRequest);
                }
            }
        });
/*
        queue = Volley.newRequestQueue(LoginActivity.this);
        stringRequest = new StringRequest(
                Request.Method.GET,
                getResources().getString(R.string.token_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            csrfToken = json.getString("token");
                        } catch (JSONException je) {
                            Log.d("DEBUG", je.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEBUG", "Error al obtener el token CSRF");
                    }
                }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> responseHeaders = response.headers;
                sessionCookie = responseHeaders.get("Set-Cookie");
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(stringRequest);*/
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.GONE);
        Log.d("DEBUG", error.toString());
        String message = getResources().getString(R.string.conn_error);
        if( error.networkResponse != null && error.networkResponse.data != null) {
            try {
                message = new JSONObject(new String(error.networkResponse.data)).getString("message");
            } catch(JSONException je) {
                Log.d("DEBUG", "Error while parsing error network response to JSON Object: " + message);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder
            .setMessage(message)
            .setTitle(getResources().getString(R.string.error));
        builder.create().show();
    }
/*
    @Override
    public void onResponse(String response) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject json = new JSONObject(response);
            if( json.getBoolean("success") ) {
                AppPreferences.setApiToken(json.getString("api_token"));
                AppPreferences.setUsername(json.getString("username"));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder
                        .setMessage(json.getString("message"))
                        .setTitle(getResources().getString(R.string.error));
                builder.create().show();
            }

        } catch(JSONException je) {
            Log.d("DEBUG", je.toString());
        }
    }
*/
    @Override
    public void onResponse(JSONObject json) {
        progressBar.setVisibility(View.GONE);
        try {
            if( json.getBoolean("success") ) {
                AppPreferences.setApiToken(json.getString("api_token"));
                AppPreferences.setUsername(json.getString("username"));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder
                        .setMessage(json.getString("message"))
                        .setTitle(getResources().getString(R.string.error));
                builder.create().show();
            }

        } catch(JSONException je) {
            Log.d("DEBUG", je.toString());
        }
    }

    private boolean validateFields() {
        boolean valid = false;

        if( usernameInput.getText().toString().length() > 0 )
        { usernameInput.setError(null); valid = true; }
        else { usernameInput.setError(getResources().getString(R.string.empty_field)); }

        if( passwordInput.getText().toString().isEmpty() )
        { passwordInput.setError(getResources().getString(R.string.empty_field)); valid = false; }
        else { passwordInput.setError(null); valid &= true;}

        return valid;
    }
}
