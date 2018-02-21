package com.sayanpco.sayanchargesample.activities.antivirus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sayanpco.charge.library.SayanCharge;
import com.sayanpco.charge.library.models.Antivirus;
import com.sayanpco.charge.library.utils.SayanUtils;
import com.sayanpco.sayanchargesample.R;
import com.sayanpco.sayanchargesample.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by nevercom on 2/21/18.
 */

public class AntiVirusListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        RecyclerView recList = findViewById(R.id.recList);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recList.setLayoutManager(mLayoutManager);
        recList.setHasFixedSize(true);

        Intent input = getIntent();
        if (input == null || !input.hasExtra("items")) {
            finish();
        }

        ArrayList<Antivirus> items = input.getParcelableArrayListExtra("items");

        AntiVirusListAdapter adapter = new AntiVirusListAdapter(items);
        recList.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position, data) -> displayPurchaseDialog((Antivirus) data));
    }

    private void displayPurchaseDialog(Antivirus pack) {


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
            SayanCharge.purchaseAntiVirus(this, pack, 10, phoneNumber, "user_email@domain.com");
            d.dismiss();
        });

        d.show();

    }

    class AntiVirusListAdapter extends RecyclerView.Adapter<AntiVirusListAdapter.ViewHolder> {

        List<Antivirus> list;
        Context c;
        private OnItemClickListener mOnItemClickListener;

        public AntiVirusListAdapter(List<Antivirus> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            c = parent.getContext();
            return new ViewHolder(LayoutInflater.from(c).inflate(R.layout.row_internet_operator, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Antivirus av = list.get(position);

            Picasso.with(c).load(av.getLogoUrl()).fit().centerInside().into(holder.logo);
            holder.title.setText(String.format("%s\nقیمت: %s ریال", av.getTitle(), SayanUtils.getCurrency(av.getPrice())));
        }

        @Override public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView logo;
            private TextView title;

            public ViewHolder(View itemView) {
                super(itemView);

                logo = itemView.findViewById(R.id.imgOperator);
                title = itemView.findViewById(R.id.tvTitle);

                itemView.setOnClickListener(this);
            }

            @Override public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition(), list.get(getAdapterPosition()));
                }
            }
        }
    }
}
