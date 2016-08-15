package com.gengyufeng.partworld.Utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by gengyufeng on 2016/8/15.
 */
public class FileUploader {

    public static void upload(String actionUrl, final String fileName, final File uploadFile, final FileUploaderHandler handler) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                uploadFileAndString(Constant.backendUrlBase+"upload_image", fileName, uploadFile, handler);
            }
        }).start();

    }

    private static void uploadFileAndString(String actionUrl, String newName, File uploadFile, FileUploaderHandler handler) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
        /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
        /* 设置传送的method=POST */
            con.setRequestMethod("POST");
        /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
        /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"userfile\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);

        /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
        /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
        /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
            /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);

            // -----
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data;name=\"name\"" + end);
            ds.writeBytes(end + URLEncoder.encode("xiexiezhichi", "UTF-8")
                    + end);
            // -----

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
            fStream.close();
            ds.flush();

        /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            Log.i("gyf", "upload file succeed:"+b.toString());
        /* 关闭DataOutputStream */
            ds.close();
            JSONObject json = new JSONObject(b.toString());
            handler.onSuccess(json.getInt("status"), json.getString("data"));
        } catch (Exception e) {
            Log.i("gyf", "upload file failed:"+e.toString());
            handler.onFail(e.toString());
        }
    }

}
