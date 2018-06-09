package com.kaleab.tenaadam.Gallery;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.kaleab.tenaadam.R;

import java.io.File;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {
    public ArrayList<File> list;
    GridView galleryGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        File pathToGallery = new File(Environment.getExternalStorageDirectory() + "/TenaAdamGallery");

        list = imageReader(pathToGallery);
        Log.i("DIRRR", pathToGallery.toString());

        galleryGrid = findViewById(R.id.galleryGridView);
        galleryGrid.setAdapter(new GridAdapter());

        galleryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent openImageDisplayFullScreen = new Intent(getApplicationContext(), ImageDetails.class);
                openImageDisplayFullScreen.putExtra("image", list.get(i).toString());
                startActivity(openImageDisplayFullScreen);
            }
        });
    }
    class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int index) {
            return list.get(index);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.single_grid, viewGroup, false);
            ImageView iv = (ImageView) view.findViewById(R.id.imageVieww);

//            Log.i("URIIII",getItem(i).toString());

            iv.setImageURI(Uri.parse(getItem(i).toString()));

//            iv.setImageBitmap(BitmapFactory.decodeFile(getItem(i).toString()));
//            Picasso.get().load(getItem(i).toString()).into((ImageView) view.findViewById(R.id.imageVieww));

            return view;
        }
    }

    public ArrayList<File> imageReader(File imagesDirectory){
        ArrayList<File> a = new ArrayList<>();

        File[] files = imagesDirectory.listFiles();
        for (int i = 0; i < files.length; i++){
            if(files[i].isDirectory()){
                a.addAll(imageReader(files[i]));
            }else{
                if(files[i].getName().endsWith(".jpg")){
                    a.add(files[i]);
                }
            }
        }
        return a;
    }
}




