package com.gengyufeng.partworld.Utils;

import android.util.Log;

/**
 * Created by gengyufeng on 2016/8/15.
 */
public class FileUploaderHandler {

    public void onSuccess(int statusCode, String file_uri) {
        Log.v("gyf", "FileUpload with code: "+statusCode+", uri: "+file_uri);
    }

    public void onFail(String errorMessage) {
        Log.v("gyf", "FileUpload error: "+errorMessage);
    }

}
