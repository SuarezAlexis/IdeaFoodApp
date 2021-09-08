package com.jainjo.ideafood.log;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.TipoIdea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment implements Response.ErrorListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private ProgressBar progressBar;
    private List<TipoIdea> tiposIdea;
    private Date firstDate;
    private Date lastDate;

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance() {
        LogFragment fragment = new LogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        JsonArrayRequest tipoIdeaRequest = new JsonArrayRequest(
                Request.Method.GET,
                getResources().getString(R.string.tipos_idea_url),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        tiposIdea = new ArrayList<TipoIdea>();
                        if (response.length() > 0) {
                            try {
                                for(int i = 0; i < response.length(); i++) {
                                    TipoIdea ti = new TipoIdea();
                                    ti.setId( (short)((JSONObject)response.get(i)).getInt("id") );
                                    ti.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                                    tiposIdea.add(ti);
                                }

                                Calendar day = Calendar.getInstance();
                                Calendar lastDay = Calendar.getInstance();
                                lastDay.set(lastDay.get(Calendar.YEAR), Calendar.DECEMBER, 25 );

                                toolbar.setTitle( day.get(Calendar.YEAR) + " " + getResources().getString(R.string.week) + " " + day.get(Calendar.WEEK_OF_YEAR) + "/" + lastDay.get(Calendar.WEEK_OF_YEAR));

                                List<LogViewModel> models = new ArrayList<LogViewModel>();
                                day.add(Calendar.DATE, 2-day.get(Calendar.DAY_OF_WEEK));
                                firstDate = new Date(day.getTime().getTime());
                                for(int i = 0; i < 7; i++) {
                                    models.add( new LogViewModel( day.getTime(), new LogDayFoodAdapter(LogFragment.this, tiposIdea) ) );
                                    day.add(Calendar.DATE,1);
                                }
                                lastDate = new Date(day.getTime().getTime());
                                adapter = new LogAdapter(models, (MainActivity)getActivity());
                                recyclerView.setAdapter(adapter);

                            } catch(JSONException je) {
                                Log.d("DEBUG","Ocurrió un error al intentar convertir la respuesta JSON");
                            }
                        }
                    }
                },
                this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.utils.getBearerTokenHeader();
            }
        };

        JSONArray params = new JSONArray();
        try {
            StringBuilder sb = new StringBuilder("[");
            sb.append("\"from\": \"" + firstDate + "\",");
            sb.append("\"to\": \"" + lastDate + "\",");
            sb.append("]");
            params = new JSONArray(sb.toString());
        } catch(JSONException je) {
            Toast.makeText(getActivity(),"Error al construir JSON",Toast.LENGTH_LONG);
            Log.d("DEBUG", "Error al crear JSON");
        }
        JsonArrayRequest logRequest = new JsonArrayRequest(
                Request.Method.GET,
                getResources().getString(R.string.ideas_url),
                params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.length() > 0) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    
                                }
                            } catch (Exception je) {

                            }
                        }
                    }
                },
                LogFragment.this
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.utils.getBearerTokenHeader();
            }
        };

        MainActivity.utils.addToRequestQueue(logRequest);
        MainActivity.utils.addToRequestQueue(tipoIdeaRequest);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.logFragProgressBar);
        toolbar = getView().findViewById(R.id.logToolbar);
        recyclerView = getView().findViewById(R.id.logRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage(message)
                .setTitle(getResources().getString(R.string.error));
        builder.create().show();
    }
}