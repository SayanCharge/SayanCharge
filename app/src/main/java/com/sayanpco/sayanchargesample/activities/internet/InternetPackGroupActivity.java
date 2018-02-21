package com.sayanpco.sayanchargesample.activities.internet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sayanpco.charge.library.models.InternetPackageGroup;
import com.sayanpco.sayanchargesample.R;
import com.sayanpco.sayanchargesample.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class InternetPackGroupActivity extends AppCompatActivity {

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

        ArrayList<InternetPackageGroup> categories = input.getParcelableArrayListExtra("items");

        InternetPackGroupAdapter adapter = new InternetPackGroupAdapter(categories);
        recList.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position, data) -> {
            InternetPackageGroup group = (InternetPackageGroup) data;

            Intent intent = new Intent(this, InternetPacksListActivity.class);
            intent.putParcelableArrayListExtra("items", group.getPackages());

            startActivity(intent);
        });
    }

    class InternetPackGroupAdapter extends RecyclerView.Adapter<InternetPackGroupAdapter.ViewHolder> {
        List<InternetPackageGroup> list;
        private OnItemClickListener mOnItemClickListener;

        public InternetPackGroupAdapter(List<InternetPackageGroup> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_internet_group, parent, false));
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            InternetPackageGroup op = list.get(position);

            holder.title.setText(op.getTitle());
        }

        @Override public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView title;

            public ViewHolder(View itemView) {
                super(itemView);

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
