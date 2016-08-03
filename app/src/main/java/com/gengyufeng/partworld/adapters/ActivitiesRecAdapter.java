package com.gengyufeng.partworld.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengyufeng.partworld.Model.MyActivity;
import com.gengyufeng.partworld.R;

import java.util.List;

/**
 * Created by gengyufeng on 2016/8/3.
 */
public class ActivitiesRecAdapter extends RecyclerView.Adapter<ActivitiesRecAdapter.ViewHolder> {
    private List<MyActivity> dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImage;
        public TextView mTitle;
        public TextView mContent;
        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.photo);
            mTitle = (TextView) view.findViewById(R.id.title);
            mContent = (TextView) view.findViewById(R.id.desc);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActivitiesRecAdapter(List<MyActivity> myDataset) {
        dataset = myDataset;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTitle.setText(dataset.get(position).title);
        holder.mContent.setText(dataset.get(position).content);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}