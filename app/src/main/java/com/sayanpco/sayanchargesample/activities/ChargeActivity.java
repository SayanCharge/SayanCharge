package com.sayanpco.sayanchargesample.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sayanpco.charge.library.SayanCharge;
import com.sayanpco.charge.library.SayanUser;
import com.sayanpco.charge.library.interfaces.NetworkCallback;
import com.sayanpco.charge.library.models.Error;
import com.sayanpco.charge.library.utils.SayanUtils;
import com.sayanpco.sayanchargesample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChargeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SAYAN_SAMPLE";
    Spinner spOperator;
    Spinner spPrice;
    Spinner spType;
    EditText etPhoneNumber;
    Button btnPurchase;
    Button btnPurchaseOnline;
    Button btnPurchaseCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spOperator = findViewById(R.id.spOperator);
        spPrice = findViewById(R.id.spPrice);
        spType = findViewById(R.id.spType);
        etPhoneNumber = findViewById(R.id.etPhone);

        btnPurchase = findViewById(R.id.btnPurchase);
        btnPurchaseOnline = findViewById(R.id.btnPurchaseOnline);
        btnPurchaseCredit = findViewById(R.id.btnPurchaseCredit);
        btnPurchase.setOnClickListener(this);
        btnPurchaseOnline.setOnClickListener(this);
        btnPurchaseCredit.setOnClickListener(this);
        setUpSpinners();
    }

    private void purchase(int gateway) {
        if (gateway > 1 && !SayanUtils.isNetworkAvailable(this)) {
            Toast.makeText(ChargeActivity.this, "Network is not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gateway == 3 && !SayanUser.isLoggedIn()) {
            Toast.makeText(ChargeActivity.this, "User must be logged in, in order to purchase by credit", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedOperator = spOperator.getSelectedItemPosition();
        if (selectedOperator < 0) {
            Toast.makeText(ChargeActivity.this, "Please select an operator", Toast.LENGTH_SHORT).show();
            return;
        }
        int operatorId = 0;
        switch (selectedOperator) {
            case 0:
                operatorId = SayanCharge.OPERATOR_IRANCELL;
                break;
            case 1:
                operatorId = SayanCharge.OPERATOR_HAMRAH_AVAL;
                break;
            case 2:
                operatorId = SayanCharge.OPERATOR_RIGHTEL;
                break;
            case 3:
                operatorId = SayanCharge.OPERATOR_TALIYA;
                break;
        }
        int selectedPrice = spPrice.getSelectedItemPosition();
        if (selectedPrice < 0) {
            Toast.makeText(ChargeActivity.this, "Please select a Price", Toast.LENGTH_SHORT).show();
            return;
        }
        int price = 0;
        switch (selectedPrice) {
            case 0:
                price = 10000;
                break;
            case 1:
                price = 20000;
                break;
            case 2:
                price = 50000;
                break;
            case 3:
                price = 100000;
                break;
            case 4:
                price = 200000;
                break;
        }
        int selectedType = spType.getSelectedItemPosition();
        if (selectedType < 0) {
            Toast.makeText(ChargeActivity.this, "Please select a Type", Toast.LENGTH_SHORT).show();
            return;
        }
        int type = 0;
        switch (selectedType) {
            case 0:
                type = SayanCharge.TYPE_AUTOCHARGE;
                break;
            case 1:
                type = SayanCharge.TYPE_PIN;
                break;
        }
        String phoneNumber = etPhoneNumber.getText().toString();
        // Check for phone number validation
        if (!SayanUtils.isPhoneNumberValid(phoneNumber)) {
            etPhoneNumber.setError("Phone Number is not valid");
            return;
        }
        // If Charge type is Topup (AutoCharge), phone number needs to match the selected operator
        if (type == SayanCharge.TYPE_AUTOCHARGE && operatorId != SayanUtils.detectOperatorByNumber(phoneNumber)) {
            etPhoneNumber.setError(String.format("شماره ی وارد شده متعلق به %s نیست", SayanUtils.getOperatorName(operatorId)));
            return;
        }
        int userId = TextUtils.isEmpty(SayanUser.getUserId()) ? 0 : Integer.parseInt(SayanUser.getUserId());
        switch (gateway) {
            case 1:
                SayanCharge.purchase(this, price, type, operatorId, false, userId, phoneNumber, "");
                return;
            case 2:
                SayanCharge.purchaseOnline(this, price, type, operatorId, false, userId, phoneNumber, "");
                return;
            case 3:
                promptPassword(price, type, operatorId, phoneNumber, new NetworkCallback() {
                    @Override public void onSuccess(int status, JSONObject response) {
                        if (response == null) {
                            Log.d(TAG, "onSuccess: response = null");
                            return;
                        }
                        try {
                            boolean error = response.getBoolean("error");
                            if (error) {
                                String msg = SayanUtils.getErrorMessage(response.getString("msg"));

                                Toast.makeText(ChargeActivity.this, "Error Occured: " + msg, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject data = response.getJSONObject("data");
                            if (data == null) {
                                Toast.makeText(ChargeActivity.this, "data = null", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String message;

                            int credit = data.getInt("credit");
                            message = String.format("باقیمانده اعتبار: %d ریال\n", credit);

                            JSONArray records = data.getJSONArray("records");
                            for (int i = 0; i < records.length(); i++) {
                                JSONObject o = records.getJSONObject(i);
                                message += "وضعیت: " + (o.getBoolean("status") ? "موفق" : "ناموفق") + "\n";
                                message += "پیام: " + o.getString("message") + "\n";
                                message += "نوع: " + o.getInt("type") + "\n";
                                message += "نام محصول: " + o.getString("name") + "\n";
                                message += "مبلغ: " + o.getString("price") + "\n";
                                message += "شماره موبایل: " + o.getString("phoneNumber") + "\n";
                                message += "کد محصول: " + o.getInt("productId") + "\n";
                                message += "اپراتور: " + SayanUtils.getOperatorName(o.getInt("operator")) + "\n";
                                message += "پین: " + (!o.isNull("pin") ? o.getInt("pin") : "") + "\n";
                                message += "سریال: " + (!o.isNull("serial") ? o.getInt("serial") : "") + "\n";
                                message += "-------------" + "\n";
                            }
                            Log.d(TAG, "onSuccess: " + message);
                            AlertDialog ad = new AlertDialog.Builder(ChargeActivity.this)
                                    .setTitle("نتیجه")
                                    .setMessage(message)
                                    .setPositiveButton("باشه", (dialog, which) -> dialog.dismiss())
                                    .create();
                            ad.show();

                        } catch (JSONException e) {
                            Log.d(TAG, "onSuccess: Exception => " + e.getMessage());
                        }
                    }

                    @Override public void onFailure(Error error) {
                        Toast.makeText(ChargeActivity.this, "Error Occured: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void promptPassword(final int price, final int type, final int operatorId, final String phoneNumber, final NetworkCallback callback) {


        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_password_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> SayanCharge.purchaseByCredit(ChargeActivity.this, price, type, operatorId,
                                false, Integer.parseInt(SayanUser.getUserId()), phoneNumber,
                                "", userInput.getText().toString(), callback))
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void setUpSpinners() {
        final List<String> list = new ArrayList<String>();
        list.add("ایرانسل");
        list.add("همراه اول");
        list.add("رایتل");
        list.add("تالیا");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOperator.setAdapter(dataAdapter);


        final List<String> list1 = new ArrayList<String>();
        list1.add("۱۰۰۰ تومانی");
        list1.add("۲۰۰۰ تومانی");
        list1.add("۵۰۰۰ تومانی");
        list1.add("۱۰۰۰۰ تومانی");
        list1.add("۲۰۰۰۰ تومانی");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter1
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrice.setAdapter(dataAdapter1);

        final List<String> list2 = new ArrayList<String>();
        list2.add("شارژ مستقیم");
        list2.add("پین کد");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(dataAdapter2);
    }

    @Override public void onClick(View v) {
        int gateway = 0;
        switch (v.getId()) {
            case R.id.btnPurchase:
                gateway = 1;
                break;
            case R.id.btnPurchaseOnline:
                gateway = 2;
                break;
            case R.id.btnPurchaseCredit:
                gateway = 3;
                break;
        }
        purchase(gateway);
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
