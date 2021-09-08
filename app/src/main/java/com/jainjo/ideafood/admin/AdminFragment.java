package com.jainjo.ideafood.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment implements View.OnClickListener {

    private ArrayList<Button> buttons;

    public AdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        buttons = findAllButtons((ViewGroup)view);
        for(Button b : buttons) {
            b.setOnClickListener(this);
        }
    }

    private ArrayList<Button> findAllButtons(ViewGroup root) {
        ArrayList<Button> btns = new ArrayList<>();
        final int childCount = root.getChildCount();
        for(int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if(child instanceof ViewGroup) {
                btns.addAll(findAllButtons((ViewGroup)child));
            }
            if(child instanceof Button) { btns.add((Button)child); }
        }
        return btns;
    }


    @Override
    public void onClick(View v) {
        ManageableEntity entity;
        switch(v.getId()) {
            case R.id.adminTipoAlimentoBtn:
                entity = ManageableEntity.TIPO_ALIMENTO;
                break;
            case R.id.adminTipoProductoBtn:
                entity = ManageableEntity.TIPO_PRODUCTO;
                break;
            case R.id.adminTipoIdeaBtn:
                entity = ManageableEntity.TIPO_IDEA;
                break;
            case R.id.adminUnidadBtn:
                entity = ManageableEntity.UNIDAD;
                break;
            case R.id.adminProductoBtn:
                entity = ManageableEntity.PRODUCTO;
                break;
            default:
                entity = ManageableEntity.ALIMENTO;
                break;
        }
        ((MainActivity)getActivity()).updateFragment(AdminListFragment.newInstance(entity));
    }
}