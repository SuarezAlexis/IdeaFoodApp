package com.jainjo.ideafood.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Debug;
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
import com.jainjo.ideafood.model.Catalogo;
import com.jainjo.ideafood.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AdminCatalogoFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    private Toolbar toolbar;
    private TextView idLabel;
    private TextView idText;
    private EditText nombreInput;
    private EditText descripcionInput;
    private Button saveBtn;
    private ProgressBar progressBar;

    private Catalogo model;
    private String url = null;

    public AdminCatalogoFragment() {}
    public AdminCatalogoFragment(Catalogo model) {
        this.model = model;
    }

    public static AdminCatalogoFragment newInstance(Catalogo model)
    { return new AdminCatalogoFragment(model); }

    public String getUrl() {
        switch(model.getClass().getSimpleName()) {
            case "TipoAlimento":
                url = getResources().getString(R.string.tipo_alimento_url);
                break;
            case "TipoIdea":
                url = getResources().getString(R.string.tipo_idea_url);
                break;
            case "TipoProducto":
                url = getResources().getString(R.string.tipo_producto_url);
                break;
            default:
                Log.d("DEBUG","Tipo indefinido de catálogo solicitado: " + model.getClass().getName());
        }
        return url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_catalogo_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.catFragToolbar);
        idLabel = view.findViewById(R.id.catFragIdLabel);
        idText = view.findViewById(R.id.catFragIdText);
        nombreInput = view.findViewById(R.id.catFragNombreInput);
        descripcionInput = view.findViewById(R.id.catFragDescripcionInput);
        saveBtn = view.findViewById(R.id.catFragSaveBtn);
        progressBar = view.findViewById(R.id.catFragProgressBar);

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate fields
                if( nombreInput.getText().length() > 0) { nombreInput.setError(null); }
                else {
                    nombreInput.setError(getResources().getString(R.string.empty_field));
                    return;
                }

                JSONObject params = null;
                try {
                    StringBuilder sb = new StringBuilder("{ ");
                    sb.append( model.getId() < 0? "" : "\"id\": \"" + model.getId() + "\", " );
                    sb.append( "\"nombre\": \"" + nombreInput.getText() + "\" ");
                    sb.append( descripcionInput.getText().length() > 0? "\", \"descripcion\": \"" + descripcionInput.getText() + "\" " : "" );
                    sb.append("}");
                    params = new JSONObject(sb.toString());
                } catch(JSONException je) {
                    Toast.makeText(getActivity(),"Error al construir JSON",Toast.LENGTH_LONG);
                    Log.d("DEBUG", "Error al crear JSON");
                    return;
                }

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.PUT,
                        getUrl(),
                        params,
                        AdminCatalogoFragment.this,
                        AdminCatalogoFragment.this
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return MainActivity.utils.getBearerTokenHeader();
                    }
                };
                progressBar.setVisibility(View.VISIBLE);
                AppUtils.getInstance(getActivity()).addToRequestQueue(request);

            }
        });

        switch(model.getClass().getSimpleName()) {
            case "TipoAlimento":
                toolbar.setTitle(getResources().getString(R.string.food_type));
                break;
            case "TipoIdea":
                toolbar.setTitle(getResources().getString(R.string.idea_type));
                break;
            case "TipoProducto":
                toolbar.setTitle(getResources().getString(R.string.product_type));
                break;
            default:
                toolbar.setTitle(getResources().getString(R.string.catalog));
                break;
        }

        if( model.getId() > 0 ) {
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
                                                    getUrl() + "?id=" + model.getId(),
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
                                                    AdminCatalogoFragment.this
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
            nombreInput.setText(model.getNombre());
            descripcionInput.setText(model.getDescripcion());
        } else {
            idText.setVisibility(View.GONE);
            idLabel.setVisibility(View.GONE);
        }
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
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setMessage("Ocurrió un error al intentar guardar los datos en el servidor: " + response.getString("result"))
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
}