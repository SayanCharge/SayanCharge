package com.sayanpco.sayanchargesample.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sayanpco.charge.library.SayanUser;
import com.sayanpco.charge.library.interfaces.NetworkCallback;
import com.sayanpco.charge.library.models.Credit;
import com.sayanpco.charge.library.models.Error;
import com.sayanpco.charge.library.utils.SayanUtils;
import com.sayanpco.sayanchargesample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreditReportActivity extends AppCompatActivity {
    boolean loading = true;
    int visibleThreshold = 2;
    int previousTotal = 0;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    int count = 20;
    int page = 1;
    int maxPages = 1;
    android.support.v7.widget.RecyclerView mRecyclerView;
    android.support.v4.widget.SwipeRefreshLayout mRefresh;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_report);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new ListAdapter();
        mRecyclerView.setAdapter(adapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
//                String log = "vic = " + visibleItemCount + ", total = " + totalItemCount
//                        + ", first = " + firstVisibleItem + ", max = " + mMaxRecords + ", Offset = " + mOffset;
//                Log.i("SCROLL", log);
                if (maxPages > 0 && page > maxPages) {
                    mRefresh.setRefreshing(false);
                    return;
                }
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    loading = true;
                    performSearch();
                }
                // mRefresh.setRefreshing(loading);
            }
        });

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                maxPages = 1;
                previousTotal = 0;
                page = 1;
                loading = false;
                performSearch();
            }
        });

        performSearch();

    }

    private void performSearch() {
        mRefresh.setRefreshing(true);
        SayanUser.getCreditReport(count, page, new NetworkCallback() {
            @Override public void onSuccess(int status, JSONObject response) {
                mRefresh.setRefreshing(false);

                if (response == null) {
                    return;
                }
                try {
                    boolean error = response.getBoolean("error");
                    if (error) {
                        Toast.makeText(CreditReportActivity.this, "Error Occured: " + response.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject data = response.getJSONObject("data");
                    if (data == null) {
                        return;
                    }
                    if (!data.has("records")) {
                        return;
                    }
                    maxPages = data.getInt("total");
                    JSONArray records = data.getJSONArray("records");
                    List<Credit> list = new ArrayList<>();
                    for (int i = 0; i < records.length(); i++) {
                        JSONObject o = records.getJSONObject(i);
                        Credit credit = new Credit(o);

                        list.add(credit);
                    }
                    page++;

                    adapter.setData(list, true);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(Error error) {
                mRefresh.setRefreshing(false);

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

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private List<Credit> list;
        private Context context;
        OnItemClickListener mItemClickListener;
        private OnItemRemoveListener mItemRemoveListener;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.row_credit, parent, false);
            return new ViewHolder(v);
        }

        public void setData(List<Credit> data, boolean append) {
            if (append && list != null) {
                list.addAll(data);
            } else {
                list = data;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Credit o = list.get(position);

            boolean isIncrease = o.getCredit() > 0;
            int amount = isIncrease ? o.getCredit() : o.getDebit();

            holder.time.setText(o.getTime());
            holder.type.setText(isIncrease ? "افزایش اعتبار" : "کاهش اعتبار");
            holder.amount.setText(String.format("مبلغ: %s ریال", SayanUtils.convertDigitsToPersian(SayanUtils.getCurrency(amount))));
            holder.desc.setText(o.getDescription());
        }

        @Override public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView time;
            TextView type;
            TextView amount;
            TextView desc;

            public ViewHolder(View itemView) {
                super(itemView);

                desc = (TextView) itemView.findViewById(R.id.tvDesc);
                amount = (TextView) itemView.findViewById(R.id.tvAmount);
                type = (TextView) itemView.findViewById(R.id.tvType);
                time = (TextView) itemView.findViewById(R.id.tvTime);

                itemView.setOnClickListener(this);
            }

            @Override public void onClick(View v) {
                if (mItemClickListener != null) {
                    int adapterPosition = getAdapterPosition();
                    mItemClickListener.onItemClick(v, adapterPosition, list.get(adapterPosition));
                }
            }
        }

        public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
            this.mItemClickListener = itemClickListener;
        }

        public void setOnItemRemoveListener(final OnItemRemoveListener itemRemoveListener) {
            this.mItemRemoveListener = itemRemoveListener;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Credit data);
    }

    public interface OnItemRemoveListener {
        void onItemRemove(int id);
    }
}

