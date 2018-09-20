package com.android.qrcodedemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.qrcodedemo.R;
import com.android.qrcodedemo.app.BaseActivity;
import com.android.qrcodedemo.util.QrCodeHelper;

public class CreateQrCodeActivity extends BaseActivity {

    private ImageView ivQrCode;
    private EditText etText;
    private TextView tvCreate;

    public static void show(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, CreateQrCodeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_create_qr_code;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        ivQrCode = findViewById(R.id.iv_qr_code);
        etText = findViewById(R.id.et_text);
        tvCreate = findViewById(R.id.tv_create);

        tvCreate.setOnClickListener(mClick);
    }

    private void doCreateQrCode() {
        final String text = etText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            final Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avatar);

            final QrCodeHelper.Builder builder = new QrCodeHelper.Builder();
            builder.setText(text);
            builder.setSize(ivQrCode.getWidth(), ivQrCode.getHeight());
            builder.setLogoBitmap(logo);

            final Bitmap bitmap = builder.bulid().createQrCodeBitmap();
            if (bitmap != null) {
                ivQrCode.setImageBitmap(bitmap);
            }
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_create:
                    doCreateQrCode();
                    break;
            }
        }
    };
}
