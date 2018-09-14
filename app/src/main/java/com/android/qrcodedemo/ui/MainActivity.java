package com.android.qrcodedemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.android.qrcodedemo.R;
import com.android.qrcodedemo.app.BaseActivity;
import com.android.qrcodedemo.util.PermissionsHelper;
import com.android.qrcodedemo.util.ToastMaster;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_QRCODE = 100;

    private PermissionsHelper mPermissionsHelper;
    private TextView tvScan;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();

        mPermissionsHelper = new PermissionsHelper
                .Builder()
                .camera()
                .readContacts()
                .writeExternalStorage()
                .bulid();
        mPermissionsHelper.setPermissionsResult(mPermissionsResult);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        tvScan = findViewById(R.id.tv_scan);

        mPermissionsHelper.requestPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsHelper.requestPermissionsResult(this, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionsHelper.activityResult(this, requestCode);
        if (RESULT_OK == resultCode) {
            if (REQUEST_CODE_QRCODE == requestCode) {
                if (data != null) {
                    final String text = data.getStringExtra(Intents.Scan.RESULT);
                    ToastMaster.show(text);
                }
            }
        } else if (RESULT_CANCELED == resultCode) {
            ToastMaster.show("取消扫描");
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_scan:
                    CaptureActivity.show(MainActivity.this, REQUEST_CODE_QRCODE);
                    break;
            }
        }
    };

    private PermissionsHelper.OnPermissionsResult mPermissionsResult = new PermissionsHelper.OnPermissionsResult() {
        @Override
        public void allPermissionGranted() {
            tvScan.setOnClickListener(mClick);
        }

        @Override
        public void cancelToSettings() {
            finish();
        }
    };
}
