package com.android.qrcodedemo.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

/**
 * 二维码生成帮助类
 */
public class QrCodeHelper {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private final String text;
    private final int size;
    private final BarcodeFormat format;
    private final Bitmap logoBitmap;

    private QrCodeHelper(Builder builder) {
        text = builder.text;
        size = builder.size;
        logoBitmap = builder.logoBitmap;
        format = BarcodeFormat.QR_CODE;
    }

    public Bitmap createQrCodeBitmap() {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.QR_VERSION, 5);

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(text, format, size, size, hints);
        } catch (Exception e) {
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if (logoBitmap != null) {
            final Canvas canvas = new Canvas(bitmap);
            final int logoSize = size / 4;
            final int padding = (size - logoSize) / 2;
            final Bitmap logo = setScale(logoBitmap, logoSize);

            final Rect pictureRect = new Rect(0, 0, logoSize, logoSize);
            final RectF dst = new RectF(padding, padding, logoSize + padding, logoSize + padding);
            canvas.drawBitmap(logo, pictureRect, dst, null);
            logo.recycle();
        }

        return bitmap;
    }

    private static Bitmap setScale(Bitmap bitmap, int size) {
        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        if (bitmapWidth != size) {
            float scale = ((float) size) / bitmapWidth;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

            bitmap.recycle();

            return resizedBitmap;
        }

        return bitmap;
    }

    public static class Builder {

        private String text;
        private int size;
        private Bitmap logoBitmap;

        public Builder() {

        }

        public void setText(String text) {
            this.text = text;
        }

        public void setSize(int width, int height) {
            this.size = Math.min(width, height);
        }

        public void setLogoBitmap(Bitmap logoBitmap) {
            this.logoBitmap = logoBitmap;
        }

        public QrCodeHelper bulid() {
            return new QrCodeHelper(this);
        }
    }
}
