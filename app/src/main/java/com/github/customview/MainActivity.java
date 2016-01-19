package com.github.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.ring_view_BT)
    Button ringViewBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(null);
    }

    @OnClick(R.id.ring_view_BT)
    public void clickRingView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_content_FL, new RingViewFragment())
                .commit();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
