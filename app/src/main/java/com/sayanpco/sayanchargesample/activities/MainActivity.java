package com.sayanpco.sayanchargesample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sayanpco.charge.library.SayanCharge;
import com.sayanpco.sayanchargesample.R;
import com.sayanpco.sayanchargesample.activities.antivirus.AntivirusBrandActivity;
import com.sayanpco.sayanchargesample.activities.internet.InternetPackActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SayanLib";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SayanCharge.initialize(this, 243);

        findViewById(R.id.btnCharge).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChargeActivity.class)));
        findViewById(R.id.btnInternet).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InternetPackActivity.class)));
        findViewById(R.id.btnAntiVirus).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AntivirusBrandActivity.class)));
        findViewById(R.id.btnLogin).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        findViewById(R.id.btnCredit).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreditActivity.class)));

    }
}
