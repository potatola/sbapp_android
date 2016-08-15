package com.gengyufeng.partworld.Adapters;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gengyufeng.partworld.Model.MyActivity;
import com.gengyufeng.partworld.R;
import com.gengyufeng.partworld.Utils.Constant;
import com.gengyufeng.partworld.Utils.NetClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by gengyufeng on 2016/8/3.
 */
public class ActivitiesRecAdapter extends RecyclerView.Adapter<ActivitiesRecAdapter.ViewHolder> {
    private List<MyActivity> dataset;
    private Integer uid;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImage;
        public TextView mTitle;
        public TextView mContent;
        public Button btn_join;
        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.photo);
            mTitle = (TextView) view.findViewById(R.id.title);
            mContent = (TextView) view.findViewById(R.id.desc);
            btn_join = (Button) view.findViewById(R.id.btn_join);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActivitiesRecAdapter(List<MyActivity> myDataset, Integer uid) {
        dataset = myDataset;
        this.uid = uid;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ActivitiesRecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTitle.setText(dataset.get(position).title);
        holder.mContent.setText(dataset.get(position).content);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
                .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .cacheInMemory(true) // default
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(dataset.get(position).cover_url, holder.mImage, options);
        holder.btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (uid == -1) {
                    Toast.makeText(view.getContext(), "请先登录", Toast.LENGTH_LONG).show();
                    return;
                }
                Integer aid = dataset.get(position).aid;
                RequestParams params = new RequestParams();
                params.put("uid", uid);
                params.put("aid", aid);
                NetClient.post("join_activity", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getInt("status") == 0) {
                                Toast.makeText(view.getContext(), response.getString("data"), Toast.LENGTH_LONG).show();
                                return;
                            }
                            Gson gson = new Gson();
                            MyActivity activity = gson.fromJson(response.getString("data"), MyActivity.class);

                            Toast.makeText(view.getContext(), "加入活动成功", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor edter = view.getContext().getSharedPreferences("setting", 0).edit();
                            edter.putString("activity", activity.title).apply();
                            edter.putInt("aid", activity.aid).apply();
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}