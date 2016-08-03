package com.gengyufeng.partworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gengyufeng.partworld.Utils.NetClient;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import cz.msebera.android.httpclient.Header;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


/**
 * Created by gengyufeng on 2016/8/2.
 */
public class MainFragment extends Fragment{

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    private Boolean loggedin = false;
    private SharedPreferences sp;

    String username, password, realname, job;
    Integer gender, age;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        loggedin = sp.getBoolean("login", false);

        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

        if (!loggedin) {
            fab1.setVisibility(View.GONE);

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
                                    Toast.makeText(getActivity(), "login:"+username, Toast.LENGTH_SHORT).show();
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
                                                            Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                                                            JSONObject data = response.getJSONObject("data");
                                                            sp.edit().putInt("uid", data.getInt("uid"));
                                                            sp.edit().putString("username", data.getString("username"));
                                                            sp.edit().putString("realname", data.getString("realname"));
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

    private void performLoggedIn() {
        fab1.setVisibility(View.VISIBLE);
        fab2.setLabelText("拍照上传");
        fab2.setLabelText("发送消息");
    }

}