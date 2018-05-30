package com.cmcc.cmvideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cmcc.cmvideo.search.SearchByAIActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.turn_to_ai_search)
    public void turnToAISearch() {
        Intent intent = new Intent(MainActivity.this, SearchByAIActivity.class);
        startActivity(intent);
    }

}
