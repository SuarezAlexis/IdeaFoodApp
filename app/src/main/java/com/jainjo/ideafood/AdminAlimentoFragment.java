package com.jainjo.ideafood;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jainjo.ideafood.admin.AdminProductoFragment;
import com.jainjo.ideafood.log.AlimentoViewModel;
import com.jainjo.ideafood.model.Alimento;
import com.jainjo.ideafood.model.TipoAlimento;
import com.jainjo.ideafood.model.TipoProducto;
import com.jainjo.ideafood.util.AppUtils;
import com.jainjo.ideafood.util.DownloadImageTask;
import com.jainjo.ideafood.util.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAlimentoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAlimentoFragment extends Fragment implements Response.Listener<NetworkResponse>, Response.ErrorListener {
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView idLabel;
    private TextView idTextView;
    private EditText nombreInput;
    private Spinner tipoInput;
    private EditText descripcionInput;
    private Button saveBtn;

    private List<TipoAlimento> tiposSpinnerArray;

    private Bitmap myBitmap;
    private Uri picUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 107;

    private Alimento model;
    private AlimentoViewModel viewModel;
    private String url;

    public AdminAlimentoFragment() { /*Required empty public constructor*/ }
    public AdminAlimentoFragment(Alimento model) { this.model = model; }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewAlimentoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminAlimentoFragment newInstance() { return new AdminAlimentoFragment(); }
    public static AdminAlimentoFragment newInstance(Alimento model)
    { return new AdminAlimentoFragment(model); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        JsonArrayRequest tiposRequest = new JsonArrayRequest(
                Request.Method.GET,
                getResources().getString(R.string.tipos_alimento_url),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        tiposSpinnerArray = new ArrayList<TipoAlimento>();
                        if (response.length() > 0) {
                            try {
                                for(int i = 0; i < response.length(); i++) {
                                    TipoAlimento ta = new TipoAlimento();
                                    ta.setId( (short)((JSONObject)response.get(i)).getInt("id") );
                                    ta.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                                    tiposSpinnerArray.add(ta);
                                }
                            } catch(JSONException je) {
                                Log.d("DEBUG","Ocurrió un error al intentar convertir la respuesta JSON");
                            }
                        }
                        ArrayAdapter<TipoAlimento> adapter = new ArrayAdapter<TipoAlimento>(
                                getContext(),
                                android.R.layout.simple_spinner_item,
                                tiposSpinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        tipoInput.setAdapter(adapter);
                        if(model != null) {
                            for(int i = 0; i < tipoInput.getCount(); i++) {
                                if( tipoInput.getItemAtPosition(i).equals( model.getTipo() ) ) {
                                    tipoInput.setSelection(i);
                                    break;
                                }
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


        MainActivity.utils.addToRequestQueue(tiposRequest);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_alimento, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        viewModel = new ViewModelProvider(requireActivity()).get(AlimentoViewModel.class);

        toolbar = view.findViewById(R.id.adminAlimentoFragToolbar);
        progressBar = view.findViewById(R.id.adminAlimentoFragProgressBar);
        imageView = view.findViewById(R.id.adminAlimentoFragImageView);
        idLabel = view.findViewById(R.id.adminAlimentoFragIdLabel);
        idTextView = view.findViewById(R.id.adminAlimentoFragIdLabel);
        nombreInput = view.findViewById(R.id.adminAlimentoFragNombreInput);
        tipoInput = view.findViewById(R.id.adminAlimentoFragTipoInput);
        descripcionInput = view.findViewById(R.id.adminAlimentoFragDescripcionInput);
        saveBtn = view.findViewById(R.id.adminAlimentoFragSaveBtn);

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        url = getResources().getString(R.string.alimento_url);
        saveBtn.setOnClickListener(v-> {
            //Validate fields
            if( nombreInput.getText().length() > 0) { nombreInput.setError(null); }
            else {
                nombreInput.setError(getResources().getString(R.string.empty_field));
                return;
            }

            VolleyMultipartRequest request = new VolleyMultipartRequest(
                    Request.Method.POST,
                    url,
                    AdminAlimentoFragment.this,
                    AdminAlimentoFragment.this) {

                @Override
                protected  Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    if( model != null && model.getId() > 0 ) { params.put("id", "" + model.getId() ); }
                    params.put("nombre", nombreInput.getText().toString() );
                    params.put("tipo", "" + ((TipoAlimento)tipoInput.getSelectedItem()).getId() );
                    if(descripcionInput.getText().length() > 0 )
                    { params.put("descripcion", descripcionInput.getText().toString()); }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if( myBitmap != null ) {
                        long imageName = System.currentTimeMillis();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                        params.put("imagen", new DataPart(imageName + ".png", byteArrayOutputStream.toByteArray()));
                    }
                    return params;
                }
            };
            request.setHeaders(MainActivity.utils.getBearerTokenHeader());
            progressBar.setVisibility(View.VISIBLE);
            AppUtils.getInstance(getActivity()).addToRequestQueue(request);
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(CAMERA);
                permissionsToRequest = AppUtils.findUnAskedPermissions(getActivity(),permissions);
                //get the permissions we have asked for before but are not granted..
                //we will store this in a global list to access later.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionsToRequest.size() > 0)
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }

                Intent intent = AppUtils.getPickImageChooserIntent(getActivity());
                if(intent == null) {
                    Toast.makeText(getContext(),"No se encontraron orígenes para obtener la imágen", Toast.LENGTH_SHORT);
                } else {
                    startActivityForResult(intent, 200);
                }
            }
        });

        if(model != null && model.getId() > 0) {
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
                                                    AdminAlimentoFragment.this
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
            if( model.getImagen() != null && !model.getImagen().isEmpty() )
            { new DownloadImageTask(imageView).execute(model.getImagen()); }
            idTextView.setText("" + model.getId());
        } else {
            idTextView.setVisibility(View.GONE);
            idLabel.setVisibility(View.GONE);
            model = viewModel.getAlimento().getValue();
        }
        nombreInput.setText(model.getNombre());
        //SELECCIONAR TIPO (Hay que iterar en el arreglo de tipos)
        descripcionInput.setText(model.getDescripcion());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {
            picUri = AppUtils.getPickImageResultUri(data);
            if (picUri != null) {
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    myBitmap = AppUtils.rotateImageIfRequired(getActivity(),myBitmap, picUri);
                    myBitmap = AppUtils.getResizedBitmap(myBitmap, 500);
                    imageView.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap = bitmap;
                imageView.setImageBitmap(myBitmap);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (AppUtils.hasPermission(getActivity(),perms)) {

                    } else {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            AppUtils.showMessageOKCancel(getActivity(),"These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
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



    @Override
    public void onResponse(NetworkResponse response) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject obj = new JSONObject(new String(response.data));
            if( obj.getBoolean("success") ) {
                fillModel();
                model.setId(obj.getLong("id"));
                if(obj.has("imagen")) { model.setImagen(obj.getString("imagen")); }
                viewModel.setAlimento(model);
                Toast.makeText(getContext(),"Se guardó correctamente",Toast.LENGTH_LONG);
                getActivity().onBackPressed();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setMessage("Ocurrió un error al intentar guardar los datos en el servidor: " + obj.getString("result"))
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

    private void fillModel() {
        if( model == null ) model = new Alimento();
        model.setNombre(nombreInput.getText().toString());
        model.setDescripcion(descripcionInput.getText().toString());
        model.setTipo( (TipoAlimento)tipoInput.getSelectedItem() );
    }
}