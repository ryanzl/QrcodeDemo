package com.android.qrcodedemo.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.android.qrcodedemo.app.APP;
import com.android.qrcodedemo.widget.dialog.DefaultAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PermissionsHelper {
    private static final int REQUEST_TO_SETTING = 1000;//跳转到系统设置权限页面

    private static final String APP_NAME = "APP名称";
    private static final String READ_CONTACTS_TIPS = "在设置-应用-%1$s-权限中开启通讯录权限，以便正常使用该功能";
    private static final String CALL_PHONE_TIPS = "在设置-应用-%1$s-权限中开启电话权限，以便正常使用该功能";
    private static final String READ_CALENDAR_TIPS = "在设置-应用-%1$s-权限中开启日历权限，以便正常使用该功能";
    private static final String CAMERA_TIPS = "在设置-应用-%1$s-权限中开启相机权限，以便正常使用该功能";
    private static final String ACCESS_FINE_LOCATION_TIPS = "在设置-应用-%1$s-权限中开启位置信息权限，以便正常使用该功能";
    private static final String WRITE_EXTERNAL_STORAGE_TIPS = "在设置-应用-%1$s-权限中开启存储权限，以便正常使用该功能";
    private static final String RECORD_AUDIO_TIPS = "在设置-应用-%1$s-权限中开启麦克风权限，以便正常使用该功能";
    private static final String READ_SMS_TIPS = "在设置-应用-%1$s-权限中开启短信权限，以便正常使用该功能";
    private static final String BODY_SENSORS_TIPS = "在设置-应用-%1$s-权限中开启身体传感器权限，以便正常使用该功能";
    private static final String DEFAULT_TIPS = "在设置-应用-%1$s-权限中开启相应的权限，以便正常使用该功能";

    private final List<String> permissionList;
    private final List<String> errorTipsList;

    private OnPermissionsResult mPermissionsResult;

    private int mPosition;//当前请求权限位置

    private PermissionsHelper(Builder builder) {
        permissionList = builder.permissionList;
        errorTipsList = builder.errorTipsList;

        for (int i = 0; i < permissionList.size(); i++) {
            final String permission = permissionList.get(i);
            if (hasBeenGranted(permission)) {
                permissionList.remove(i);
                errorTipsList.remove(i);
                i--;
            }
        }

        mPosition = 0;
    }

    private void chekcPermission(Activity activity) {
        if (mPosition < permissionList.size()) {
            final String permission = permissionList.get(mPosition);

            if (hasBeenGranted(permission)) {
                requestNextPermissions(activity);
            } else {
                showTipsDialog(activity);
            }
        }
    }

    private boolean hasBeenGranted(String permission) {
        return ContextCompat.checkSelfPermission(APP.get(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void setPermissionsResult(OnPermissionsResult onPermissionsResult) {
        mPermissionsResult = onPermissionsResult;
    }

    private void requestNextPermissions(Activity activity) {
        mPosition++;

        requestPermissions(activity);
    }

    public void requestPermissions(Activity activity) {
        if (mPosition < permissionList.size()) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionList.get(mPosition)}, mPosition);
        } else {
            if (mPermissionsResult != null) mPermissionsResult.allPermissionGranted();
        }
    }

    private void showTipsDialog(final Activity activity) {
        final DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    AppManager.showInstalledAppDetails(activity, activity.getPackageName(), REQUEST_TO_SETTING);
                } else if (DialogInterface.BUTTON_NEGATIVE == which) {
                    if (mPermissionsResult != null) mPermissionsResult.cancelToSettings();
                }
            }
        };

        final DefaultAlertDialog dialog = new DefaultAlertDialog(activity);
        dialog.setTitle("权限申请");
        dialog.setConfirmButton("去设置", dialogClick);
        dialog.setCancelButton("取消", dialogClick);
        dialog.setMessage(errorTipsList.get(mPosition));
        dialog.showDialog();
    }

    public void activityResult(Activity activity, int requestCode) {
        if (REQUEST_TO_SETTING == requestCode) {
            chekcPermission(activity);
        }
    }

    public void requestPermissionsResult(Activity activity, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showTipsDialog(activity);
            } else {
                requestNextPermissions(activity);
            }
        }
    }

    public static class Builder {

        private final List<String> permissionList;
        private final List<String> errorTipsList;

        public Builder() {
            permissionList = new ArrayList<>();
            errorTipsList = new ArrayList<>();
        }

        //通讯录
        public Builder readContacts() {
            if (!permissionList.contains(Manifest.permission.READ_CONTACTS)) {
                permissionList.add(Manifest.permission.READ_CONTACTS);
                errorTipsList.add(String.format(READ_CONTACTS_TIPS, APP_NAME));
            }
            return this;
        }

        //电话
        public Builder callPhone() {
            if (!permissionList.contains(Manifest.permission.CALL_PHONE)) {
                permissionList.add(Manifest.permission.CALL_PHONE);
                errorTipsList.add(String.format(CALL_PHONE_TIPS, APP_NAME));
            }
            return this;
        }

        //日历
        public Builder readCalendar() {
            if (!permissionList.contains(Manifest.permission.READ_CALENDAR)) {
                permissionList.add(Manifest.permission.READ_CALENDAR);
                errorTipsList.add(String.format(READ_CALENDAR_TIPS, APP_NAME));
            }
            return this;
        }

        //相机
        public Builder camera() {
            if (!permissionList.contains(Manifest.permission.CAMERA)) {
                permissionList.add(Manifest.permission.CAMERA);
                errorTipsList.add(String.format(CAMERA_TIPS, APP_NAME));
            }
            return this;
        }

        //位置信息
        public Builder accessFineLocation() {
            if (!permissionList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                errorTipsList.add(String.format(ACCESS_FINE_LOCATION_TIPS, APP_NAME));
            }
            return this;
        }

        //存储
        public Builder writeExternalStorage() {
            if (!permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                errorTipsList.add(String.format(WRITE_EXTERNAL_STORAGE_TIPS, APP_NAME));
            }
            return this;
        }

        //麦克风
        public Builder recordAudio() {
            if (!permissionList.contains(Manifest.permission.RECORD_AUDIO)) {
                permissionList.add(Manifest.permission.RECORD_AUDIO);
                errorTipsList.add(String.format(RECORD_AUDIO_TIPS, APP_NAME));
            }
            return this;
        }

        //短信
        public Builder readSms() {
            if (!permissionList.contains(Manifest.permission.READ_SMS)) {
                permissionList.add(Manifest.permission.READ_SMS);
                errorTipsList.add(String.format(READ_SMS_TIPS, APP_NAME));
            }
            return this;
        }

        //身体传感器
        public Builder bodySensors() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
                    && !permissionList.contains(Manifest.permission.BODY_SENSORS)) {
                permissionList.add(Manifest.permission.BODY_SENSORS);
                errorTipsList.add(String.format(BODY_SENSORS_TIPS, APP_NAME));
            }
            return this;
        }

        public Builder add(String permissions){
            if (!permissionList.contains(permissions)) {
                permissionList.add(permissions);
                errorTipsList.add(String.format(DEFAULT_TIPS, APP_NAME));
            }
            return this;
        }

        public PermissionsHelper bulid() {
            return new PermissionsHelper(this);
        }
    }

    public interface OnPermissionsResult {
        void allPermissionGranted();

        void cancelToSettings();
    }

    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".file.path.share", file);
        }
        return Uri.fromFile(file);
    }

}
