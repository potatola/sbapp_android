package com.gengyufeng.partworld.Utils;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by gengyufeng on 2016/8/7.
 */
public class ViewUnits {

    public static MaterialDialog getLoadingDialog(Activity activity, String title, String content) {
        MaterialDialog.Builder builder =  new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .progress(true, 0)
                .autoDismiss(false);
        builder.cancelable(false);
        return builder.show();
    }

}
