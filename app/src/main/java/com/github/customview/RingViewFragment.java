package com.github.customview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.customview.views.RingView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RingViewFragment extends Fragment {

    @Bind(R.id.level_ring_view)
    RingView ringView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ring_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ringView.setOnFinishListener(new RingView.OnFinishListener() {
            @Override
            public void onFinish() {
                Toast.makeText(getActivity(), "绘制完成", Toast.LENGTH_SHORT).show();
            }
        });

        ringView.setLevel(30);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
