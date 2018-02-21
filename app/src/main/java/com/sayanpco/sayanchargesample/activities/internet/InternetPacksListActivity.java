package com.sayanpco.sayanchargesample.activities.internet;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sayanpco.charge.library.SayanCharge;
import com.sayanpco.charge.library.models.InternetPackage;
import com.sayanpco.charge.library.utils.SayanUtils;
import com.sayanpco.sayanchargesample.R;
import com.sayanpco.sayanchargesample.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class InternetPacksListActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recList = findViewById(R.id.recList);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recList.setLayoutManager(mLayoutManager);
        recList.setHasFixedSize(true);
        Intent input = getIntent();
        if (input == null || !input.hasExtra("items")) {
            finish();
        }

        ArrayList<InternetPackage> items = input.getParcelableArrayListExtra("items");

        InternetPackAdapter adapter = new InternetPackAdapter(items);
        recList.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position, data) -> displayPurchaseDialog((InternetPackage) data));

    }

    private void displayPurchaseDialog(InternetPackage pack) {


        final Dialog d = new Dialog(this);
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_purchase_package);


        TextView tvTitle = d.findViewById(R.id.tvTitle);
        TextInputLayout til = d.findViewById(R.id.til);
        EditText etPhoneNumber = d.findViewById(R.id.etPhoneNumber);
        Button btnPurchase = d.findViewById(R.id.btnPurchase);

        tvTitle.setText(pack.getDescription());

        btnPurchase.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString();
            if (TextUtils.isEmpty(phoneNumber)) {
                til.setError("شماره تلفن را وارد کنید");
                return;
            }
            if (!SayanUtils.isPhoneNumberValid(phoneNumber)) {
                til.setError("شماره تلفن معتبر نیست");
                return;
            }
            SayanCharge.purchaseInternetPackage(this, pack, 10, phoneNumber, "user_email@domain.com");
            d.dismiss();
        });

        d.show();

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("INP", "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("INP", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
    }

    class InternetPackAdapter extends RecyclerView.Adapter<InternetPackAdapter.ViewHolder> {
        List<InternetPackage> list;
        private OnItemClickListener mOnItemClickListener;

        public InternetPackAdapter(List<InternetPackage> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_internet_pack, parent, false));
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            InternetPackage op = list.get(position);

            holder.title.setText(op.getName());
            holder.price.setText(String.format("قیمت: %s ریال", SayanUtils.getCurrency(op.getPrice())));
            holder.volume.setText(String.format("حجم: %s", readableSize(op.getVolume())));
            holder.duration.setText(String.format("مدت اعتبار: %d روز", op.getDuration()));
            holder.description.setText(op.getDescription());
        }

        public String readableSize(int size) {
            if (size <= 0) return "0";

            if (size >= 1000) {
                return (size / 1000) + " گیگابایت";
            }
            return size + " مگابایت";

        }

        @Override public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView title;
            private TextView price;
            private TextView volume;
            private TextView duration;
            private TextView description;
            private Button btnPurchase;

            public ViewHolder(View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.tvTitle);
                price = itemView.findViewById(R.id.tvPrice);
                volume = itemView.findViewById(R.id.tvVolume);
                duration = itemView.findViewById(R.id.tvDuration);
                description = itemView.findViewById(R.id.tvDescription);
                btnPurchase = itemView.findViewById(R.id.btnPurchase);

                btnPurchase.setOnClickListener(this);
            }

            @Override public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition(), list.get(getAdapterPosition()));
                }
            }
        }
    }
}
