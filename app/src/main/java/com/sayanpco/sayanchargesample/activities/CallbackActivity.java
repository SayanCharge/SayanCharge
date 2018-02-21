package com.sayanpco.sayanchargesample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.sayanpco.charge.library.SayanCharge;
import com.sayanpco.charge.library.models.CallbackData;
import com.sayanpco.sayanchargesample.R;

public class CallbackActivity extends AppCompatActivity {

    private static final String TAG = "Callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CallbackData data = SayanCharge.parseCallbackData(getIntent());
        if (data == null) {
            finish();
        }

        String text = "وضعیت: " + (data.isSuccessful() ? "موفق" : "ناموفق") + "\n" +
                "پیام: " + data.getMessage() + "\n" +
                "کد تراکنش: " + data.getTransactionId() + "\n" +
                "مبلغ: " + data.getPrice() + "\n" +
                "شماره موبایل: " + data.getPhoneNumber() + "\n" +
                "نوع: " + data.getType() + "\n" +
                "نام محصول: " + data.getProductName() + "\n" +
                "پین کد: " + data.getPinCode() + "\n" +
                "سریال: " + data.getSerial() + "\n" +
                "باقیمانده اعتبار: " + data.getCredit();
        TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.

                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
