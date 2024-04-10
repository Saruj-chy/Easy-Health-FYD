package com.sd.spartan.easyhealth.activity;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import com.sd.spartan.easyhealth.R;

public class DonateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        Toolbar toolbar = findViewById(R.id.appbar_donate) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(DONATION) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView donateTV = findViewById(R.id.text_donate);

        donateTV.setText(Html.fromHtml(donate_msg));

    }
}