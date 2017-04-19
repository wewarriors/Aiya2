package com.example.wewarriors.aiya;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GiftMethodActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_method);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id){
            case R.id.tv_back:
                intent = new Intent(GiftMethodActivity.this,MainActivity.class);
                intent.putExtra("Flag","Me");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
