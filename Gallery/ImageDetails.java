package com.kaleab.tenaadam.Gallery;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaleab.tenaadam.R;

public class ImageDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_details);

//        Bitmap bitmap = getIntent().getParcelableExtra("image");

        String openedImage = getIntent().getStringExtra("image");

        ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
//        imageView.setImageBitmap(bitmap);
        imageView.setImageURI(Uri.parse(openedImage));
    }
}
