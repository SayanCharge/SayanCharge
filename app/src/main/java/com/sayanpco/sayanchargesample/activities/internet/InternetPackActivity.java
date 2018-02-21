package com.sayanpco.sayanchargesample.activities.internet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sayanpco.charge.library.InternetPackUtils;
import com.sayanpco.charge.library.interfaces.InternetPackCallback;
import com.sayanpco.charge.library.models.InternetPackageOperator;
import com.sayanpco.sayanchargesample.R;
import com.sayanpco.sayanchargesample.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InternetPackActivity extends AppCompatActivity {
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

        InternetPackUtils.getPackages(this, new InternetPackCallback() {
            @Override public void onSuccess(ArrayList<InternetPackageOperator> list) {
                InternetOperatorsAdapter adapter = new InternetOperatorsAdapter(InternetPackActivity.this, list);
                recList.setAdapter(adapter);
                adapter.setOnItemClickListener((view, position, data) -> {
                    InternetPackageOperator op = (InternetPackageOperator) data;

                    Intent intent = new Intent(InternetPackActivity.this, InternetPackGroupActivity.class);
                    intent.putParcelableArrayListExtra("items", op.getCategories());

                    startActivity(intent);
                });
            }

            @Override public void onError() {

            }
        });


    }

    class InternetOperatorsAdapter extends RecyclerView.Adapter<InternetOperatorsAdapter.ViewHolder> {
        List<InternetPackageOperator> list;
        Context context;

        OnItemClickListener mOnItemClickListener;

        public InternetOperatorsAdapter(Context c, List<InternetPackageOperator> list) {
            this.list = list;
            this.context = c;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_internet_operator, parent, false));
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            InternetPackageOperator op = list.get(position);

            Picasso.with(context).load(op.getLogoUrl()).fit().centerInside().into(holder.logo);
            holder.title.setText(op.getName());
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
