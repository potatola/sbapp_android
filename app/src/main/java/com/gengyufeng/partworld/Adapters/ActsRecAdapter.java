package com.gengyufeng.partworld.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gengyufeng.partworld.Model.Act;
import com.gengyufeng.partworld.R;
import com.gengyufeng.partworld.Utils.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gengyufeng on 2016/8/5.
 */
public class ActsRecAdapter extends RecyclerView.Adapter<ActsRecAdapter.ActViewHolder> {

    private int uid;
    private List<Act> mActs;
    DisplayImageOptions options;

    public ActsRecAdapter(List<Act> mActs, int uid) {
        this.mActs = mActs;
        this.uid = uid;

        options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
            .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
            .showImageOnFail(R.drawable.ic_error) // resource or drawable
            .cacheInMemory(true) // default
            .build();
    }

    @Override
    public ActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_act, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ActViewHolder vh = new ActViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ActViewHolder holder, int position) {
        Act act = mActs.get(position);
        //TODO this function may cause blocking
        holder.setIsRecyclable(false);
        holder.username.setText(act.username);
        holder.content.setText(act.content);
        holder.location.setText(act.location);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        String sttime = ft.format(new Date((long)(act.time*1000)));
        holder.time.setText(sttime);
        switch (act.act) {
            case 0:
                holder.content.setText("进行了签到");
                holder.photo.setVisibility(View.GONE);
                break;
            case 1:
                Log.i("gyf", "image:"+Constant.imageUrlBase+act.content);
                holder.photo.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(Constant.imageUrlBase+act.content, holder.photo, options);
                holder.content.setVisibility(View.GONE);
                holder.location.setVisibility(View.GONE);
                break;
            case 2:
                holder.content.setText(act.content);
                holder.photo.setVisibility(View.GONE);
                holder.location.setVisibility(View.GONE);
                break;
        }
        if (this.uid == act.uid) {
            holder.rl.setBackgroundColor(Color.GREEN);
            holder.vright.setVisibility(View.GONE);
        }
        else {
            holder.vleft.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mActs.size();
    }

    public class ActViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl;
        public View vleft;
        public View vright;
        public ImageView photo;
        public TextView username;
        public TextView content;
        public TextView location;
        public TextView time;
        public ActViewHolder(View view) {
            super(view);
            this.vleft = (View) view.findViewById(R.id.vleft);
            this.vright = (View) view.findViewById(R.id.vright);
            this.rl = (RelativeLayout) view.findViewById(R.id.rl);
            this.photo = (ImageView) view.findViewById(R.id.photo);
            this.username = (TextView) view.findViewById(R.id.username);
            this.content = (TextView) view.findViewById(R.id.content);
            this.location = (TextView) view.findViewById(R.id.location);
            this.time = (TextView) view.findViewById(R.id.time);
        }
    }
}
