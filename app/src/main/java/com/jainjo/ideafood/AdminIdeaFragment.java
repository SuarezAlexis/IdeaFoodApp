package com.jainjo.ideafood;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jainjo.ideafood.admin.AdminAlimentoViewHolder;
import com.jainjo.ideafood.log.AlimentoViewModel;
import com.jainjo.ideafood.log.IdeaViewModel;
import com.jainjo.ideafood.model.Alimento;
import com.jainjo.ideafood.model.Idea;
import com.jainjo.ideafood.model.TipoAlimento;
import com.jainjo.ideafood.model.TipoIdea;
import com.jainjo.ideafood.util.AppUtils;
import com.jainjo.ideafood.util.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminIdeaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminIdeaFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    private LayoutInflater inflater;
    private Spinner tipoInput;
    private EditText nombreInput;
    private EditText descripcionInput;
    private AutoCompleteTextView alimentoNombreInput;
    private Button alimentoAddBtn;
    private LinearLayout alimentosLayout;
    private TextView alimentosError;
    private ProgressBar progressBar;
    private Button saveBtn;

    private List<TipoIdea> tiposIdeaArray;
    private List<Alimento> alimentosArray;
    private List<TipoAlimento> tiposAlimentoArray = new ArrayList<>();

    private String url;
    private Date date;
    private Idea model = new Idea();
    private Alimento newAlimento;
    private AlimentoViewModel alimentoViewModel;
    private IdeaViewModel ideaViewModel;

    public AdminIdeaFragment() {
        // Required empty public constructor
    }

    public AdminIdeaFragment(Date date, List<TipoIdea> tiposIdea, Idea model) {
        this.date = date;
        tiposIdeaArray = tiposIdea;
        this.model = model;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewIdeaFragment.
     */
    public static AdminIdeaFragment newInstance() { return new AdminIdeaFragment(); }

    public static AdminIdeaFragment newInstance(Date date, List<TipoIdea> tiposIdea, Idea idea) {
        return new AdminIdeaFragment(date, tiposIdea, idea);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        if( tiposIdeaArray == null ) {
            JsonArrayRequest tipoIdeaRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    getResources().getString(R.string.tipos_idea_url),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            progressBar.setVisibility(View.GONE);
                            tiposIdeaArray = new ArrayList<TipoIdea>();
                            if (response.length() > 0) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        TipoIdea ti = new TipoIdea();
                                        ti.setId((short) ((JSONObject) response.get(i)).getInt("id"));
                                        ti.setNombre(((JSONObject) response.get(i)).getString("nombre"));
                                        tiposIdeaArray.add(ti);
                                    }
                                } catch (JSONException je) {
                                    Log.d("DEBUG", "Ocurrió un error al intentar convertir la respuesta JSON");
                                }
                            }
                            setTipoInputAdapter();
                        }
                    },
                    this) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return MainActivity.utils.getBearerTokenHeader();
                }
            };
            MainActivity.utils.addToRequestQueue(tipoIdeaRequest);
        }

        JsonArrayRequest alimentosRequest = new JsonArrayRequest(
                Request.Method.GET,
                getResources().getString(R.string.alimentos_url),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        alimentosArray = new ArrayList<Alimento>();
                        if (response.length() > 0) {
                            try {
                                for(int i = 0; i < response.length(); i++) {
                                    Alimento a = new Alimento();
                                    a.setId( ((JSONObject)response.get(i)).getLong("id") );
                                    a.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                                    a.setDescripcion( ((JSONObject)response.get(i)).getString("descripcion") );
                                    a.setImagen( ((JSONObject)response.get(i)).getString("imagen") );
                                    a.setUsername( ((JSONObject)response.get(i)).getString("username") );
                                    a.setTipo(new TipoAlimento());
                                    a.getTipo().setId( (short)((JSONObject)response.get(i)).getInt("tipo") );
                                    if( tiposAlimentoArray.contains(a.getTipo()) )
                                    { a.setTipo( tiposAlimentoArray.get(tiposAlimentoArray.indexOf(a.getTipo()))); }
                                    else {
                                        a.getTipo().setNombre(((JSONObject)response.get(i)).getString("tipoNombre"));
                                        tiposAlimentoArray.add(a.getTipo());
                                    }
                                    alimentosArray.add(a);
                                }
                            } catch(JSONException je) {
                                Log.d("DEBUG","Ocurrió un error al intentar convertir la respuesta JSON");
                            }
                        }
                        ArrayAdapter<Alimento> adapter = new ArrayAdapter<Alimento>(
                                getContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                alimentosArray);
                        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        alimentoNombreInput.setAdapter(adapter);
                    }
                },
                this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.utils.getBearerTokenHeader();
            }
        };

        MainActivity.utils.addToRequestQueue(alimentosRequest);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_idea, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        ViewModelProvider vmp = new ViewModelProvider(requireActivity());
        alimentoViewModel = vmp.get(AlimentoViewModel.class);
        alimentoViewModel.getAlimento().observe(getViewLifecycleOwner(), a -> {
            //Update the alimento data, add to alimentos list, etc.
            Log.d("DEBUG", a.toString());
            if(a.getId() != -1) appendAlimento();
        });
        ideaViewModel = vmp.get(IdeaViewModel.class);

        tipoInput = view.findViewById(R.id.adminIdeaFragHorarioInput);
        nombreInput = view.findViewById(R.id.adminIdeaFragNombreInput);
        descripcionInput = view.findViewById(R.id.adminIdeaFragDescripcionInput);
        alimentoNombreInput = view.findViewById(R.id.adminIdeaFragAlimentoNombreInput);
        alimentoAddBtn = view.findViewById(R.id.adminIdeaFragAddAlimentoBtn);
        alimentosLayout = view.findViewById(R.id.adminIdeaFragAlimentosLayout);
        alimentosError = view.findViewById(R.id.adminIdeaFragAlimentosError);
        saveBtn = view.findViewById(R.id.adminIdeaFragAddBtn);
        progressBar = view.findViewById(R.id.adminIdeaFragProgressBar);

        url = getResources().getString(R.string.idea_url);

        fillUIFromModel();
        alimentoNombreInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newAlimento = (Alimento)parent.getItemAtPosition(position);
            }
        });

        alimentoAddBtn.setOnClickListener(v -> {
            if( newAlimento == null) {
                newAlimento = new Alimento();
                newAlimento.setNombre(alimentoNombreInput.getText().toString());
                alimentoViewModel.setAlimento(newAlimento);
                ((MainActivity)getActivity()).updateFragment(AdminAlimentoFragment.newInstance());
            } else {
                appendAlimento();
            }
        });

        saveBtn.setOnClickListener(v -> {
            //Validate fields
            if( model.getAlimentos() == null || model.getAlimentos().size() == 0)
            { alimentosError.setVisibility(View.VISIBLE); return; }
            else { alimentosError.setVisibility(View.GONE); }

            JSONObject params = new JSONObject();
            try {
                StringBuilder sb = new StringBuilder("{ ");
                sb.append( model.getId() < 0? "" : "\"id\": \"" + model.getId() + "\", " );
                sb.append( "\"tipo\": \"" + ((TipoIdea)tipoInput.getSelectedItem()).getId() + "\", ");
                if(nombreInput.getText().length() > 0)
                { sb.append( "\"nombre\": \"" + nombreInput.getText() + "\", "); }
                if(descripcionInput.getText().length() > 0)
                { sb.append( "\"descripcion\": \"" + descripcionInput.getText() + "\", "); }
                if( date != null )
                {
                    SimpleDateFormat format = new SimpleDateFormat("YY.MM.dd");
                    sb.append( "\"fecha\": \"" + format.format(date) + "\", ");
                }
                sb.append("\"alimentos\": [");
                String prefix = "";
                for( Alimento a : model.getAlimentos()) {
                    sb.append(prefix + " { \"id\": \"" + a.getId() + "\" }"); prefix = ",";
                }
                sb.append(" ] ");
                sb.append("}");
                params = new JSONObject(sb.toString());
            } catch(JSONException je) {
                Toast.makeText(getActivity(),"Error al construir JSON",Toast.LENGTH_LONG);
                Log.d("DEBUG", "Error al crear JSON");
                return;
            }
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    params,
                    AdminIdeaFragment.this,
                    AdminIdeaFragment.this) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return MainActivity.utils.getBearerTokenHeader();
                }
            };
            progressBar.setVisibility(View.VISIBLE);
            AppUtils.getInstance(getActivity()).addToRequestQueue(request);
        });
        setTipoInputAdapter();
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        try {
            if( response.getBoolean("success") ) {
                model.setId(response.getLong("id"));
                //viewModel.setIdea(model);
                Toast.makeText(getContext(),"Se guardó correctamente",Toast.LENGTH_LONG);
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
                    .setMessage("Ocurrió un error al procesar la respuesta del servidor.")
                    .setTitle(getResources().getString(R.string.error));
            builder.create().show();
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

    private void setTipoInputAdapter() {
        if( tiposIdeaArray != null ) {
            ArrayAdapter<TipoIdea> adapter = new ArrayAdapter<TipoIdea>(getContext(), android.R.layout.simple_spinner_item, tiposIdeaArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tipoInput.setAdapter(adapter);
            if (model != null) {
                for (int i = 0; i < tipoInput.getCount(); i++) {
                    if (tipoInput.getItemAtPosition(i).equals(model.getTipo())) {
                        tipoInput.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void appendAlimento() {
        model.getAlimentos().add(newAlimento);

        View v = inflater.inflate(R.layout.admin_alimento_view,null);
        AdminAlimentoViewHolder vh = new AdminAlimentoViewHolder(v);
        vh.bindData((MainActivity)getActivity(), newAlimento);
        alimentosLayout.addView(v);
        //TextView tv = new TextView(getActivity());
        //tv.setText(newAlimento.getId() + " " + newAlimento.getNombre());
        //alimentosLayout.addView(tv);
        newAlimento = null;
        alimentoNombreInput.setText("");
    }

    private void fillUIFromModel() {
        for( int i = 0; i < tiposIdeaArray.size(); i++) {
            if( tiposIdeaArray.get(i).equals(model.getTipo()) )
            { tipoInput.setSelection(i); break; }
        }
        nombreInput.setText(model.getNombre());
        descripcionInput.setText(model.getDescripcion());
        for( Alimento a : model.getAlimentos()) {
            TextView tv = new TextView(getActivity());
            tv.setText(a.getId() + " " + a.getNombre());
            alimentosLayout.addView(tv);
        }
    }

}