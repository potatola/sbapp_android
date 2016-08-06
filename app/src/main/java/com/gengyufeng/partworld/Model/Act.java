package com.gengyufeng.partworld.Model;


import java.util.Date;

/**
 * Created by gengyufeng on 2016/8/5.
 */
public class Act {
    public int actid;
    public int uid;
    public String username;
    public int aid;
    public int act;
    public double time;
    public double latitude;
    public double longitude;
    public String location;
    public String content;

    public Act(int actid, int uid, String username, int act, int aid, long time, float latitude, float longitude, String location, String content) {
        this.actid = actid;
        this.uid = uid;
        this.username = username;
        this.act = act;
        this.aid = aid;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.content = content;
    }
}
