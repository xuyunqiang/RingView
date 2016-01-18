package com.github.customview.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.customview.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RingViewFragment extends Fragment {

    @Bind(R.id.level_ring_view)
    RingView ringView;
//    @Bind(R.id.level_TV)
//    TextView levelTV;
//    @Bind(R.id.level_V_TV)
//    TextView levelVTV;

    public RingViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ring_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int level = (299 % 300) / 10 + 1;

        ringView.setLevel(level);
//        levelTV.setText("3");
//        levelVTV.setText("V");
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
