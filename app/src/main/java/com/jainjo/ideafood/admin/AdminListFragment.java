package com.jainjo.ideafood.admin;

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
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jainjo.ideafood.AdminAlimentoFragment;
import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Alimento;
import com.jainjo.ideafood.model.Producto;
import com.jainjo.ideafood.model.TipoAlimento;
import com.jainjo.ideafood.model.TipoIdea;
import com.jainjo.ideafood.model.TipoProducto;
import com.jainjo.ideafood.model.Unidad;
import com.jainjo.ideafood.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminListFragment extends Fragment implements Response.Listener<JSONArray>, Response.ErrorListener {

    private Toolbar toolbar;
    private Button addBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ManageableEntity entity;

    public AdminListFragment() {
        // Required empty public constructor
    }

    public AdminListFragment(ManageableEntity entity) { setEntity(entity); }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entity Kind of entities to show.
     * @return A new instance of fragment AdminListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminListFragment newInstance(ManageableEntity entity) {
        return new AdminListFragment(entity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressBar = getView().findViewById(R.id.adminListProgressBar);
        toolbar = getView().findViewById(R.id.adminToolbar);
        addBtn = getView().findViewById(R.id.adminAddBtn);
        recyclerView = getView().findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        int toolbarTitle = -1;
        String url = null;
        switch(entity) {
            case TIPO_ALIMENTO:
                toolbarTitle = R.string.food_types;
                url = getResources().getString(R.string.tipos_alimento_url);
                break;
            case TIPO_IDEA:
                toolbarTitle = R.string.idea_types;
                url = getResources().getString(R.string.tipos_idea_url);
                break;
            case TIPO_PRODUCTO:
                toolbarTitle = R.string.product_types;
                url = getResources().getString(R.string.tipos_producto_url);
                break;
            case UNIDAD:
                toolbarTitle = R.string.units;
                url = getResources().getString(R.string.unidades_url);
                break;
            case PRODUCTO:
                toolbarTitle = R.string.products;
                url = getResources().getString(R.string.productos_url);
                break;
            case ALIMENTO:
                toolbarTitle = R.string.foods;
                url = getResources().getString(R.string.alimentos_url);
                break;

        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                switch(entity) {
                    case UNIDAD:
                        fragment = AdminUnidadFragment.newInstance();
                        break;
                    case PRODUCTO:
                        fragment = AdminProductoFragment.newInstance();
                        break;
                    case TIPO_ALIMENTO:
                        fragment = AdminCatalogoFragment.newInstance(new TipoAlimento());
                        break;
                    case TIPO_IDEA:
                        fragment = AdminCatalogoFragment.newInstance(new TipoIdea());
                        break;
                    case TIPO_PRODUCTO:
                        fragment = AdminCatalogoFragment.newInstance(new TipoProducto());
                        break;
                    case ALIMENTO:
                        fragment = AdminAlimentoFragment.newInstance();
                        break;
                }
                ((MainActivity)getActivity()).updateFragment(fragment);
            }
        });
        toolbar.setTitle(toolbarTitle);
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this,
                this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.utils.getBearerTokenHeader();
            }
        };
        progressBar.setVisibility(View.VISIBLE);
        AppUtils.getInstance(getActivity()).addToRequestQueue(request);
    }

    public ManageableEntity getEntity() { return this.entity; }
    public void setEntity(ManageableEntity entity) { this.entity = entity; }
    public RecyclerView getRecyclerView() { return this.recyclerView; }

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
    public void onResponse(JSONArray response) {
        progressBar.setVisibility(View.GONE);
        if (response.length() > 0) {
            List<Object> items = new ArrayList<Object>();
            try {
                for(int i = 0; i < response.length(); i++) {
                    switch(entity) {
                        case TIPO_ALIMENTO:
                            TipoAlimento ta = new TipoAlimento();
                            ta.setId( (short)((JSONObject)response.get(i)).getInt("id") );
                            ta.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                            items.add(ta);
                            break;
                        case TIPO_IDEA:
                            TipoIdea ti = new TipoIdea();
                            ti.setId( (short)((JSONObject)response.get(i)).getInt("id"));
                            ti.setNombre( ((JSONObject)response.get(i)).getString("nombre"));
                            items.add(ti);
                            break;
                        case TIPO_PRODUCTO:
                            TipoProducto tp = new TipoProducto();
                            tp.setId( (short)((JSONObject)response.get(i)).getInt("id") );
                            tp.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                            items.add(tp);
                            break;
                        case UNIDAD:
                            Unidad u = new Unidad();
                            u.setId( ((JSONObject)response.get(i)).getInt("id") );
                            u.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                            u.setAbr( ((JSONObject)response.get(i)).getString("abr") );
                            u.setMagnitud( ((JSONObject)response.get(i)).getString("magnitud") );
                            items.add(u);
                            break;
                        case PRODUCTO:
                            Producto p = new Producto();
                            p.setId( ((JSONObject)response.get(i)).getInt("id") );
                            p.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                            p.setDescripcion( ((JSONObject)response.get(i)).getString("descripcion") );
                            p.setTipo( new TipoProducto() );
                            p.getTipo().setId( (short)((JSONObject)response.get(i)).getInt("tipo") );
                            p.getTipo().setNombre( ((JSONObject)response.get(i)).getString("tipoNombre"));
                            p.setImagen( ((JSONObject)response.get(i)).getString("imagen") );
                            items.add(p);
                            break;
                        case ALIMENTO:
                            Alimento a = new Alimento();
                            a.setId( ((JSONObject)response.get(i)).getInt("id") );
                            a.setNombre( ((JSONObject)response.get(i)).getString("nombre") );
                            a.setDescripcion( ((JSONObject)response.get(i)).getString("descripcion") );
                            a.setTipo( new TipoAlimento() );
                            a.getTipo().setId( (short)((JSONObject)response.get(i)).getInt("tipo") );
                            a.getTipo().setNombre( ((JSONObject)response.get(i)).getString("tipoNombre"));
                            a.setImagen( ((JSONObject)response.get(i)).getString("imagen") );
                            items.add(a);
                            break;
                    }
                }
            } catch(JSONException je) {
                Log.d("DEBUG","Ocurrió un error al intentar convertir la respuesta JSON");
            }

            recyclerView.setAdapter(new AdminAdapter((MainActivity)getActivity(), items, entity));
        } else {
            recyclerView.setAdapter( new AdminAdapter((MainActivity)getActivity(), new ArrayList<Object>(), entity) );
        }
    }
}