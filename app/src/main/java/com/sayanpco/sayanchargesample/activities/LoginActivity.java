package com.sayanpco.sayanchargesample.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sayanpco.charge.library.SayanUser;
import com.sayanpco.charge.library.interfaces.NetworkCallback;
import com.sayanpco.charge.library.models.Error;
import com.sayanpco.charge.library.utils.SayanUtils;
import com.sayanpco.sayanchargesample.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnSignup = findViewById(R.id.btnSignup);
        Button btnLogin = findViewById(R.id.btnLogin);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etSignupPassCheck = findViewById(R.id.etSignupPassCheck);
        final EditText etSignupPass = findViewById(R.id.etSignupPass);
        final EditText etSignupPhone = findViewById(R.id.etSignupPhone);
        final EditText etLoginPass = findViewById(R.id.etLoginPass);
        final EditText etLoginPhone = findViewById(R.id.etLoginPhone);

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("لطفاً منتظر بمانید");
        pd.setCancelable(false);
        pd.setIndeterminate(true);


        btnLogin.setOnClickListener(v -> {
            if (!SayanUtils.isNetworkAvailable(LoginActivity.this)) {
                Toast.makeText(LoginActivity.this, "اینترنت در دسترس نیست", Toast.LENGTH_SHORT).show();
                return;
            }
            String phoneNumber = etLoginPhone.getText().toString();
            if (!SayanUtils.isPhoneNumberValid(phoneNumber)) {
                etLoginPhone.setError("شماره وارد شده معتبر نیست");
                return;
            }
            String password = etLoginPass.getText().toString();
            if (TextUtils.isEmpty(password)) {
                etLoginPass.setError("کلمه ی عبور معتبر نیست");
                return;
            }

            pd.show();
            SayanUser.login(phoneNumber, password, new NetworkCallback() {
                @Override public void onSuccess(int status, JSONObject data) {
                    try {
                        pd.dismiss();
                        boolean error = data.getBoolean("error");
                        if (error) {
                            Toast.makeText(LoginActivity.this, "Error Occured: " + data.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject response = data.getJSONObject("data");
                        String email = response.getString("eml");
                        Toast.makeText(LoginActivity.this, "Logged In. Email: " + email, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onFailure(Error error) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "خطایی در ارتباط با سرور رخ داده است", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnSignup.setOnClickListener(v -> {
            if (!SayanUtils.isNetworkAvailable(LoginActivity.this)) {
                Toast.makeText(LoginActivity.this, "اینترنت در دسترس نیست", Toast.LENGTH_SHORT).show();
                return;
            }
            String phoneNumber = etSignupPhone.getText().toString();
            if (!SayanUtils.isPhoneNumberValid(phoneNumber)) {
                etSignupPhone.setError("شماره وارد شده معتبر نیست");
                return;
            }
            String password = etSignupPass.getText().toString();
            if (TextUtils.isEmpty(password)) {
                etSignupPass.setError("کلمه ی عبور معتبر نیست");
                return;
            }
            String passwordCheck = etSignupPassCheck.getText().toString();
            if (!passwordCheck.equals(password)) {
                etSignupPassCheck.setError("رمز وارد شده با کلمه ی عبور همخوانی ندارد");
                return;
            }
            pd.show();
            SayanUser.signUp(phoneNumber, password, etEmail.getText().toString(), new NetworkCallback() {
                @Override public void onSuccess(int status, JSONObject data) {
                    pd.dismiss();
                    try {
                        boolean error = data.getBoolean("error");
                        if (error) {
                            Toast.makeText(LoginActivity.this, "Error Occured: " + data.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Toast.makeText(LoginActivity.this, "Signup successful. UserId: " + SayanUser.getUserId(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onFailure(Error error) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "خطایی در ارتباط با سرور رخ داده است", Toast.LENGTH_SHORT).show();

                }
            });
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
