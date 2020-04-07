package com.example.photovault;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;

    public byte[][] imagesArray = null;
    private int width, height;

    public ImageAdapter(Context context, byte[][] imagesArray) {
        this.context = context;
        this.imagesArray = imagesArray;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    public void setImageArray(byte[][] imagesArray) {
        this.imagesArray = imagesArray;
    }

    @Override
    public int getCount() {
        return imagesArray.length;
    }

    @Override
    public Object getItem(int position) {
        return imagesArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imagesArray[position], 0, imagesArray[position].length));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(width / 4, height / 6));

        return imageView;
    }

}
