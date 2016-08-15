package com.gengyufeng.partworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.gengyufeng.partworld.Adapters.ActsRecAdapter;
import com.gengyufeng.partworld.Model.Act;
import com.gengyufeng.partworld.Model.MyActivity;
import com.gengyufeng.partworld.Utils.Constant;
import com.gengyufeng.partworld.Utils.FileUploader;
import com.gengyufeng.partworld.Utils.FileUploaderHandler;
import com.gengyufeng.partworld.Utils.NetClient;
import com.gengyufeng.partworld.Adapters.ActivitiesRecAdapter;
import com.gengyufeng.partworld.Utils.ViewUnits;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by gengyufeng on 2016/8/2.
 */
public class MainFragment extends Fragment{

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private RecyclerView mRecyclerView;

    private AppBarLayout appbar;
    private TextView tv_username;
    private TextView tv_activity;

    private int uid = -1;
    private int aid = -1;
    private String activity = "";
    private SharedPreferences sp;

    String username, password, realname, job;
    Integer gender, age;

    //Baidu Location
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyBDLocationListener();
    private String bd_location = null;
    private double latitude;
    private double longitude;
    private long last_location_time;
    private static final long LOCATE_SPAN = 120000;
    private Integer Locate_source = 0;// 0:签到，1：照片

    EditText alertText;
    private final Integer CAMERA_PICKER = 0;
    private String imagePath;

    //Loading dialogs
    MaterialDialog uploadImageDialog;
    MaterialDialog checkinDialog;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("setting", 0);

        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        uid = sp.getInt("uid", -1);
        username = sp.getString("username", "");
        realname = sp.getString("realname", "");

        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

        appbar = (AppBarLayout) view.findViewById(R.id.appbar);
        tv_username = (TextView) view.findViewById(R.id.realname);
        tv_activity = (TextView) view.findViewById(R.id.activity);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view);

        if (uid == -1) {
            fab1.setVisibility(View.GONE);
            appbar.setVisibility(View.GONE);

            initPopWindow(view.findViewById(R.id.menu_yellow));

            fab2.setLabelText("登录");
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View dview = inflater.inflate(R.layout.dialog_login, null);
                    builder.setView(dview)
                            // Add action buttons
                            .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String username = ((EditText)dview.findViewById(R.id.username)).getText().toString();
                                    String password = ((EditText)dview.findViewById(R.id.password)).getText().toString();
                                    RequestParams params = new RequestParams();
                                    params.put("username", username);
                                    params.put("password", password);
                                    NetClient.post("login", params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            try {
                                                if (response.getInt("status") == 0) {
                                                    Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                JSONObject data = response.getJSONObject("data");
                                                Log.i("gyf", response.toString());
                                                sp.edit().putInt("uid", data.getInt("uid")).apply();
                                                sp.edit().putString("username", data.getString("username")).apply();
                                                sp.edit().putString("realname", data.getString("realname")).apply();
                                                if (data.has("aid")) {
                                                    sp.edit().putInt("aid", data.getInt("aid")).apply();
                                                    sp.edit().putString("activity", data.getString("title")).apply();
                                                    aid = data.getInt("aid");
                                                    activity = data.getString("title");
                                                }
                                                performLoggedIn();
                                            } catch (Exception e) {
                                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();
                }
            });

            fab3.setLabelText("注册新用户");
            fab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View dview = inflater.inflate(R.layout.dialog_register, null);
                    builder.setView(dview)
                            // Add action buttons
                            .setPositiveButton("注册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    try {
                                        username = ((EditText) dview.findViewById(R.id.username)).getText().toString();
                                        password = ((EditText) dview.findViewById(R.id.password)).getText().toString();
                                        realname = ((EditText) dview.findViewById(R.id.realname)).getText().toString();
                                        job = ((EditText) dview.findViewById(R.id.job)).getText().toString();
                                        age = Integer.parseInt(((EditText) dview.findViewById(R.id.age)).getText().toString());
                                        if (((RadioButton) dview.findViewById(R.id.man)).isChecked())
                                            gender = 1;
                                        else if (((RadioButton) dview.findViewById(R.id.woman)).isChecked())
                                            gender = 0;
                                        else {
                                            Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    //TODO: verify

                                    //打开注册页面
                                    RegisterPage registerPage = new RegisterPage();
                                    registerPage.setRegisterCallback(new EventHandler() {
                                        public void afterEvent(int event, final int result, Object data) {
                                            // 解析注册结果
                                            if (result == SMSSDK.RESULT_COMPLETE) {
                                                @SuppressWarnings("unchecked")
                                                HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                                                String country = (String) phoneMap.get("country");
                                                String phone = (String) phoneMap.get("phone");
                                                Toast.makeText(getActivity(), "Succeed with phone:"+phone+",username:"+username+",password:"+password, Toast.LENGTH_SHORT).show();
                                                RequestParams params = new RequestParams();
                                                params.put("username", username);
                                                params.put("password", password);
                                                params.put("realname", realname);
                                                params.put("job", job);
                                                params.put("age", age);
                                                params.put("gender", gender);
                                                params.put("phone", phone);
                                                NetClient.post("add_account", params, new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        try {
                                                            Log.i("gyf", response.toString());
                                                            JSONObject data = response.getJSONObject("data");
                                                            sp.edit().putInt("uid", data.getInt("uid")).apply();
                                                            sp.edit().putString("username", data.getString("username")).apply();
                                                            sp.edit().putString("realname", data.getString("realname")).apply();
                                                            performLoggedIn();
                                                        } catch (Exception e) {
                                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    registerPage.show(getActivity());
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();
                }
            });
        }
        else {
            performLoggedIn();
        }

        return view;
    }

    private void initPopWindow(View v) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_login, null, false);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.pop_login);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 0, -370);
    }

    private void performLoggedIn() {
        initLocation();
        appbar.setVisibility(View.VISIBLE);
        tv_username.setText(""+sp.getString("realname", ""));
        activity = sp.getString("activity", null);
        aid = sp.getInt("aid", -1);
        if(activity != null) {
            tv_activity.setText(activity);
            update_acts();
        }
        else {
            tv_activity.setText("在'活动列表'中选择活动加入");
            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);
            fab3.setVisibility(View.GONE);
            return;
        }
        fab1.setVisibility(View.VISIBLE);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locate_source = 0;
                mLocationClient.start();
                checkinDialog = ViewUnits.getLoadingDialog(getActivity(), "签到", "正在签到...");
            }
        });

        fab2.setLabelText("拍照上传");
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locate_source = 1;
                //mLocationClient.start();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Log.v("gyf", "folder:"+ Environment.getExternalStorageDirectory()+", "+ getActivity().getApplicationInfo().dataDir);
                startActivityForResult(cameraIntent, CAMERA_PICKER);
            }
        });

        fab3.setLabelText("发送消息");
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertText = new EditText(getActivity());
                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setTitle("发送消息")
                        .setView(alertText)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String content = alertText.getText().toString();
                                RequestParams params = new RequestParams();
                                params.put("act", 2);
                                params.put("content", content);
                                params.put("aid", sp.getInt("aid", -1));
                                params.put("uid", uid);
                                params.put("username", realname);
                                params.put("location", "");
                                params.put("latitude", 0);
                                params.put("longitude", 0);
                                NetClient.post("act_activity", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            if (response.getInt("status") == 0) {
                                                Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Log.e("gyf", e.toString());
                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyBDLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            last_location_time = (new Date()).getTime();
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                bd_location = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                bd_location = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                bd_location = "("+latitude+","+latitude+")";
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            Log.i("gyf", sb.toString());
            mLocationClient.stop();

            if (location.getLocType() == BDLocation.TypeGpsLocation||
                    location.getLocType() == BDLocation.TypeNetWorkLocation||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {
                Integer aid = sp.getInt("aid", -1);
                RequestParams params = new RequestParams();
                params.put("aid", aid);
                params.put("uid", uid);
                params.put("location", bd_location);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("username", realname);
                if (Locate_source == 0) {
                    // 签到
                    params.put("act", 0);
                    params.put("content", "");
                    NetClient.post("act_activity", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            checkinDialog.dismiss();
                            try {
                                if (response.getInt("status") == 0) {
                                    Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Toast.makeText(getActivity(), "签到成功", Toast.LENGTH_LONG).show();
                                update_acts();
                            } catch (Exception e) {
                                Log.e("gyf", e.toString());
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
            }
            else {
                Log.e("gyf", sb.toString());
                switch (Locate_source) {
                    case 0:
                        checkinDialog.dismiss();
                        break;
                    case 1:
                        uploadImageDialog.dismiss();
                        break;
                }
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("gyf", ""+requestCode);
        if (CAMERA_PICKER == requestCode) {
            if (resultCode == Activity.RESULT_CANCELED) return;
            Log.i("gyf", "photo");
            onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        uploadImageDialog = ViewUnits.getLoadingDialog(getActivity(), "拍照上传", "正在上传照片");
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(getActivity().getApplicationInfo().dataDir,
                System.currentTimeMillis() + ".jpg");
        final String fpath = destination.getAbsolutePath();
        imagePath = fpath;
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            final JsonHttpResponseHandler actActivityHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    uploadImageDialog.dismiss();
                    try {
                        if (response.getInt("status") == 0) {
                            Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                            uploadImageDialog.dismiss();
                            return;
                        }
                        Toast.makeText(getActivity(), "拍照上传成功", Toast.LENGTH_SHORT).show();
                        update_acts();
                    } catch (Exception e) {
                        Log.e("gyf", e.toString());
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            };
            Date date = new Date();
            FileUploader.upload(Constant.backendUrlBase+"upload_image", "image_"+uid+"_"+date.getTime()+".jpg", new File(fpath), new FileUploaderHandler() {
                @Override
                public void onSuccess(int statusCode, String file_uri) {
                    RequestParams params = new RequestParams();
                    params.put("uid", uid);
                    params.put("username", realname);
                    params.put("act", 1);
                    params.put("aid", aid);
                    params.put("location", "not now");
                    params.put("content", file_uri);
                    params.put("latitude", 0);
                    params.put("longitude", 0);
                    NetClient.post("act_activity", params, actActivityHandler);
                }

                @Override
                public void onFail(String errorMessage) {
                    Toast.makeText(getActivity(), "照片上传失败", Toast.LENGTH_SHORT).show();
                    uploadImageDialog.dismiss();
                }
            });

        }
        catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            uploadImageDialog.dismiss();
        }
    }

    private void update_acts() {
        RequestParams params = new RequestParams();
        params.put("aid", aid);
        NetClient.post("acts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("status") == 0) {
                        Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Gson gson = new Gson();
                    List<Act> acts = new ArrayList<Act>();
                    JSONArray data = response.getJSONArray("data");
                    Log.i("gyf", response.getString("data"));
                    //Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < data.length(); i++) {
                        Act act = gson.fromJson(data.getString(i), Act.class);
                        acts.add(act);
                    }
                    ActsRecAdapter adapter = new ActsRecAdapter(acts, uid);

                    mRecyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager mLayoutManager;

                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    Log.e("gyf", e.toString());
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

}