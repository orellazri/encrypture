package com.example.photovault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

public class FullScreenActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getSupportActionBar().setTitle("Full Screen Image");

        imageView = findViewById(R.id.fullscreen_imageview);

        // Get image bytes array
        Intent curIntent = getIntent();
        byte[][] imagesArray = new byte[1][1];
        imagesArray[0] = curIntent.getByteArrayExtra("image");

        ImageAdapter imageAdapter = new ImageAdapter(this, imagesArray);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imagesArray[0], 0, imagesArray[0].length));
    }
}
