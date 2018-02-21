package com.sayanpco.sayanchargesample.activities;

import android.accounts.AuthenticatorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sayanpco.charge.library.SayanCharge;
import com.sayanpco.charge.library.SayanUser;
import com.sayanpco.charge.library.utils.SayanUtils;
import com.sayanpco.sayanchargesample.R;

import java.util.Locale;

public class CreditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView tvCredit = (TextView) findViewById(R.id.tvCreditInfo);
        final EditText etAmount = (EditText) findViewById(R.id.etAmount);
        Button btnIncreaseCredit = (Button) findViewById(R.id.btnPurchaseCredit);
        Button btnCreditReport = (Button) findViewById(R.id.btnCreditReport);

        String currentCredit = SayanUtils.convertDigitsToPersian(SayanUtils.getCurrency((int) SayanUser.getUserCredit()));
        tvCredit.setText(String.format(Locale.US, "اعتبار فعلی: %s", currentCredit));

        btnIncreaseCredit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String str = etAmount.getText().toString();
                int amount = TextUtils.isEmpty(str) ? 0 : Integer.parseInt(str);

                if (amount < 10000) {
                    etAmount.setError("حداقل مبلغ ۱۰۰۰ تومان می باشد.");
                    return;
                }
                try {
                    SayanCharge.increaseCredit(CreditActivity.this, amount);
                } catch (AuthenticatorException e) {
                    Toast.makeText(CreditActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCreditReport.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(CreditActivity.this, CreditReportActivity.class));
            }
        });
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
