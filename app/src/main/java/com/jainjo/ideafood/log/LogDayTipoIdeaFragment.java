package com.jainjo.ideafood.log;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jainjo.ideafood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogDayTipoIdeaFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LogDayTipoIdeaFragment extends Fragment {

    private static final String TIPO_IDEA_ID = "com.jainjo.ideafood.log.LogDayTipoIdeaFragment.id";
    private static final String TIPO_IDEA_NAME = "com.jainjo.ideafood.log.LogDayTipoIdeaFragment.name";

    private int mTipoIdeaId;
    private String mTipoIdeaName;

    private TextView tipoIdeaTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tipoIdeaId Parameter 1.
     * @param tipoIdeaName Parameter 2.
     * @return A new instance of fragment LogDayTipoIdeaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogDayTipoIdeaFragment newInstance(int tipoIdeaId, String tipoIdeaName) {
        LogDayTipoIdeaFragment fragment = new LogDayTipoIdeaFragment();
        Bundle args = new Bundle();
        args.putInt(TIPO_IDEA_ID, tipoIdeaId);
        args.putString(TIPO_IDEA_NAME, tipoIdeaName);
        fragment.setArguments(args);
        return fragment;
    }

    public LogDayTipoIdeaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTipoIdeaId = getArguments().getInt(TIPO_IDEA_ID);
            mTipoIdeaName = getArguments().getString(TIPO_IDEA_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_day_tipo_idea, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tipoIdeaTextView = getView().findViewById(R.id.tipoIdeaTextView);
        tipoIdeaTextView.setText(mTipoIdeaName);
    }
}