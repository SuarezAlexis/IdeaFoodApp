package com.jainjo.ideafood.admin;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Unidad;
import com.jainjo.ideafood.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUnidadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUnidadFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    private Toolbar toolbar;
    private TextView idLabel;
    private TextView idText;
    private TextView nombreLabel;
    private EditText nombreInput;
    private TextView abrLabel;
    private EditText abrInput;
    private TextView magnitudLabel;
    private EditText magnitudInput;
    private Button saveBtn;
    private ProgressBar progressBar;

    private Unidad model = new Unidad();
    private String url = null;

    private AdminUnidadFragment() {}
    private AdminUnidadFragment(Unidad model) { this.model = model; }

    public static AdminUnidadFragment newInstance() { return new AdminUnidadFragment(); }
    public static AdminUnidadFragment newInstance(Unidad model)
    { return new AdminUnidadFragment(model); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_unidad, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.unidadFragToolbar);
        idLabel = view.findViewById(R.id.unidadFragIdLabel);
        idText = view.findViewById(R.id.unidadFragIdText);
        nombreLabel = view.findViewById(R.id.unidadFragNombreLabel);
        nombreInput = view.findViewById(R.id.unidadFragNombreInput);
        abrLabel = view.findViewById(R.id.unidadFragAbrLabel);
        abrInput = view.findViewById(R.id.unidadFragAbrInput);
        magnitudLabel = view.findViewById(R.id.unidadFragMagnitudLabel);
        magnitudInput = view.findViewById(R.id.unidadFragMagnitudInput);
        saveBtn = view.findViewById(R.id.unidadFragSaveBtn);
        progressBar = view.findViewById(R.id.unidadFragProgressBar);

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        url = getResources().getString(R.string.unidad_url);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( validateFields() ) {
                    JSONObject params = null;
                    try {
                        StringBuilder sb = new StringBuilder("{ ");
                        sb.append( model.getId() < 0? "" : "\"id\": \"" + model.getId() + "\", " );
                        sb.append( "\"nombre\": \"" + nombreInput.getText() + "\"");
                        sb.append( abrInput.getText().length() > 0? ", \"abr\": \"" + abrInput.getText() + "\" " : "" );
                        sb.append("}");
                        params = new JSONObject(sb.toString());
                    } catch(JSONException je) {
                        Toast.makeText(getActivity(),"Error al construir JSON",Toast.LENGTH_LONG);
                        Log.d("DEBUG", "Error al crear JSON");
                        return;
                    }
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.PUT,
                            url,
                            params,
                            AdminUnidadFragment.this,
                            AdminUnidadFragment.this
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return MainActivity.utils.getBearerTokenHeader();
                        }
                    };
                    progressBar.setVisibility(View.VISIBLE);
                    AppUtils.getInstance(getActivity()).addToRequestQueue(request);
                }
            }
        });

        if(model != null) {
            if( model.getId() > 0) {
                toolbar.inflateMenu(R.menu.delete_menu);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                new AlertDialog.Builder(getActivity())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getResources().getString(R.string.delete))
                                        .setMessage(getResources().getString(R.string.delete_confirmation)
                                                + "\n" + model.getClass().getSimpleName()
                                                + " " + model.getNombre())
                                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                                        .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                JsonObjectRequest request = new JsonObjectRequest(
                                                        Request.Method.DELETE,
                                                        url + "?id=" + model.getId(),
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                progressBar.setVisibility(View.GONE);
                                                                try {
                                                                    if( response.getBoolean("success") ) {
                                                                        Toast.makeText(getActivity(),"Se eliminó correctamente",Toast.LENGTH_LONG);
                                                                        getActivity().onBackPressed();
                                                                    } else {
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                        builder
                                                                                .setMessage("Ocurrió un error al intentar eliminar los datos en el servidor: " + response.getString("result"))
                                                                                .setTitle(getResources().getString(R.string.error));
                                                                        builder.create().show();
                                                                    }
                                                                } catch(JSONException e) {
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                    builder
                                                                            .setMessage("Respuesta satisfactoria, ocurrió un error al procesar la respuesta.")
                                                                            .setTitle(getResources().getString(R.string.error));
                                                                    builder.create().show();
                                                                }
                                                            }
                                                        },
                                                        AdminUnidadFragment.this
                                                ) {
                                                    @Override
                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                        return MainActivity.utils.getBearerTokenHeader();
                                                    }
                                                };
                                                progressBar.setVisibility(View.VISIBLE);
                                                AppUtils.getInstance(getActivity()).addToRequestQueue(request);
                                            }
                                        })
                                        .show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                idText.setText("" + model.getId());
            } else {
                idText.setVisibility(View.GONE);
                idLabel.setVisibility(View.GONE);
            }
            nombreInput.setText(model.getNombre());
            abrInput.setText(model.getAbr());
            magnitudInput.setText(model.getMagnitud());
        }
    }

    private boolean validateFields() {
        boolean valid = false;

        if( valid = nombreInput.getText().length() > 0) { nombreInput.setError(null); }
        else { nombreInput.setError(getResources().getString(R.string.empty_field)); }

        if( abrInput.getText().length() > 0) { abrInput.setError(null); }
        else { abrInput.setError(getResources().getString(R.string.empty_field)); valid = false; }

        return valid;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.GONE);
        Log.d("DEBUG", error.toString());
        Toast.makeText(getActivity(),"Error de conexion",Toast.LENGTH_LONG);
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

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        try {
            if( response.getBoolean("success") ) {
                Toast.makeText(getActivity(),"Se guardó correctamente",Toast.LENGTH_LONG);
                getActivity().onBackPressed();
            }
        } catch(JSONException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage("Respuesta satisfactoria, ocurrió un error al procesar la respuesta.")
                    .setTitle(getResources().getString(R.string.error));
            builder.create().show();
        }
    }
}