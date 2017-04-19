package com.example.wewarriors.aiya;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private View mMeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("onCreated");
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        System.out.println("onNewIntent();");
    }

    @Override
    protected void onRestart() {
        Intent intent = getIntent();
        if(!TextUtils.isEmpty(intent.getStringExtra("Flag"))){
            String str = intent.getStringExtra("Flag");
            System.out.println(str);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (str){
                case "Me":
                    ft.replace(R.id.main_content,new MeFragment());
                    ft.commit();
                    navigationView.getMenu().getItem(3).setChecked(true);
                    break;

            }
        }
        super.onRestart();

    }

    private void initView() {
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mMeView = findViewById(R.id.navigation_me);
        BottomNavigationViewHelper.disableShiftMode(navigationView);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.navigation_community:

                                return true;
                            case R.id.navigation_message:

                                return true;
                            case R.id.navigation_love:

                                return true;
                            case R.id.navigation_me:
                                ft.replace(R.id.main_content,new MeFragment());
                                ft.commit();
                                return true;
                        }
                        return false;
                    }
                });
    }
}
