package com.gengyufeng.partworld.Model;

/**
 * Created by gengyufeng on 2016/8/3.
 */
public class MyActivity {
    public int aid;
    public String title;
    public String content;
    public String cover_url;

    public MyActivity(int aid, String title, String content, String image_url) {
        this.aid = aid;
        this.title = title;
        this.content = content;
        this.cover_url = image_url;
    }
}
