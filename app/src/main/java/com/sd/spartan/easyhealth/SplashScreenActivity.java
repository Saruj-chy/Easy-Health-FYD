package com.sd.spartan.easyhealth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private boolean action = false ;
    private int SPLASH_TIME = 3000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        new Handler().postDelayed(() -> {
            if (!action) {
                goActivity();
            }
        }, SPLASH_TIME);



    }

    private void goActivity() {
        Intent mySuperIntent = new Intent(this, MainActivity.class);
        startActivity(mySuperIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPLASH_TIME = 0;
        action = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SPLASH_TIME = 0;
        action = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPLASH_TIME = 0;
        action = true;
        finish();
    }
}