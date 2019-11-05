package com.example.ventus.networksignal.localization;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class PinItem {
    public float x;
    public float y;
    public String text;
    Bitmap bitmap;
    Bitmap bitmapEmpty;

    public PinItem(String text, PointF point, Bitmap bitmap, Bitmap bitmapEmpty) {
        this.x = point.x;
        this.y = point.y;
        this.text = text;
        this.bitmapEmpty = bitmapEmpty;
        this.bitmap = bitmap;
    }

    public String getText() {
        return text;
    }
}
