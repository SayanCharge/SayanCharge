package com.sayanpco.sayanchargesample.activities.antivirus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sayanpco.charge.library.AntivirusUtils;
import com.sayanpco.charge.library.interfaces.AntivirusCallback;
import com.sayanpco.charge.library.models.AntivirusGroup;
import com.sayanpco.sayanchargesample.R;
import com.sayanpco.sayanchargesample.activities.internet.InternetPackGroupActivity;
import com.sayanpco.sayanchargesample.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AntivirusBrandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        RecyclerView recList = findViewById(R.id.recList);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recList.setLayoutManager(mLayoutManager);
        recList.setHasFixedSize(true);

        AntivirusUtils.getPackages(this, new AntivirusCallback() {
            @Override public void onSuccess(ArrayList<AntivirusGroup> list) {
                AntiVirusBrandAdapter adapter = new AntiVirusBrandAdapter(list);
                recList.setAdapter(adapter);
                adapter.setOnItemClickListener((view, position, data) -> {
                    AntivirusGroup ag = (AntivirusGroup) data;

                    Intent intent = new Intent(AntivirusBrandActivity.this, AntiVirusListActivity.class);
                    intent.putParcelableArrayListExtra("items", ag.getList());

                    startActivity(intent);
                });
            }

            @Override public void onError() {

            }
        });
    }

    class AntiVirusBrandAdapter extends RecyclerView.Adapter<AntiVirusBrandAdapter.ViewHolder> {

        List<AntivirusGroup> list;
        Context c;
        private OnItemClickListener mOnItemClickListener;

        public AntiVirusBrandAdapter(List<AntivirusGroup> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            c = parent.getContext();
            return new ViewHolder(LayoutInflater.from(c).inflate(R.layout.row_internet_operator, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AntivirusGroup ag = list.get(position);

            Picasso.with(c).load(ag.getLogoUrl()).fit().centerInside().into(holder.logo);
            holder.title.setText(ag.getTitle());
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
