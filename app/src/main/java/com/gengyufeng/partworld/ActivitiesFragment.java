package com.gengyufeng.partworld;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gengyufeng.partworld.Model.MyActivity;
import com.gengyufeng.partworld.adapters.ActivitiesRecAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gengyufeng on 2016/8/2.
 */
public class ActivitiesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ActivitiesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        List<MyActivity> myDataset = new ArrayList<MyActivity>();
        myDataset.add(new MyActivity("青岛市崂山公园门口", "兼职，发传单，一天100", ""));
        myDataset.add(new MyActivity("济南天桥区政府门前", "站街，一天50", ""));
        myDataset.add(new MyActivity("五月的风", "发放小礼品，邀请游客拍照", ""));
        mAdapter = new ActivitiesRecAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}
