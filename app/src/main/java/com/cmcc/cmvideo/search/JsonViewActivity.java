package com.cmcc.cmvideo.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cmcc.cmvideo.R;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

public class JsonViewActivity extends AppCompatActivity {
    public static final String KEY_JSON_SOURCE = "json_source";
    private JsonRecyclerView mJsonRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_view);
        mJsonRecyclerView = (JsonRecyclerView)findViewById(R.id.rv_json);
        mJsonRecyclerView.bindJson(getIntent().getStringExtra(KEY_JSON_SOURCE));
        mJsonRecyclerView.setScaleEnable(true);
    }
}
