package com.android.qrcodedemo.util;

import android.content.Context;
import android.widget.Toast;

import com.android.qrcodedemo.app.APP;


/**
 * toast优化工具
 */

public class ToastMaster {

    private static Toast sToast = null;

    private ToastMaster() {

    }

    public static void show(String content) {
        showToast(Toast.makeText(APP.get(), content, Toast.LENGTH_LONG));
    }

    public static void show(Context context, String content) {
        showToast(Toast.makeText(context, content, Toast.LENGTH_LONG));
    }

    public static void show(Context context, String content, int duration) {
        showToast(Toast.makeText(context, content, duration));
    }

    public static void showToast(Toast toast) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
        sToast.show();
    }

    public static void cancelToast() {
        if (sToast != null)
            sToast.cancel();
        sToast = null;
    }

}
