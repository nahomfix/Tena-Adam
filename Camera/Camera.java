package com.kaleab.tenaadam.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaleab.tenaadam.Gallery.Gallery;
import com.kaleab.tenaadam.HomePage.MainActivity;
import com.kaleab.tenaadam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity{
    int REQUEST_IMAGE_CAPTURE = 1;
    int FILE_SELECT_CODE = 2;
    ImageView imageView;
    Button openCameraBtn;
    TextView saveLabel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        imageView = findViewById(R.id.imagePreview);
//        openCameraBtn = findViewById(R.id.openCam);


        final CharSequence[] items = {"Take Photo", "Select From Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Camera.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Take Photo")){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null ){
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                    }
                }
                else if (items[i].equals("Select From Gallery")){
                    Intent selectFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    selectFromGallery.setType("image/*");
                    startActivityForResult(selectFromGallery.createChooser(selectFromGallery, "Select Photo"), FILE_SELECT_CODE);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent returnHome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(returnHome);
            }
        });
        builder.show();

//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null ){
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//
//        }

//        openCameraBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
//            imageView.setRotation(90);
        }
        else if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
        else{
            Intent returnHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(returnHome);
        }
    }

    public void retakePhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null ){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    public File getSaveDirectory(){
        String saveDirectory = Environment.getExternalStorageDirectory().toString();
//        Log.i ("HERE === ", saveDirectory);
        File folder = new File(saveDirectory + "/TenaAdamGallery");
        folder.mkdirs();
//        Log.i ("HEREToo === ", folder.toString());
        return folder;
    }

    public void savePhoto(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Camera.this);
        dialog.setTitle("Confirm Save");
        final EditText name = new EditText(Camera.this);
        name.setHint("Enter a name");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        name.setLayoutParams(layoutParams);
        dialog.setView(name);

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmSavePhoto(getCurrentFocus(), name.getText().toString());
            }
        });

        dialog.show();
    }

    public void confirmSavePhoto(View view, String nameOfPhoto){

        File savedDirectory = getSaveDirectory();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = nameOfPhoto + date + ".jpg";
//        String fileName = savedDirectory.getAbsolutePath() + "/" + name;
        File newFile = new File(savedDirectory, name);
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(getApplicationContext(), "Image successfully saved", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        refreshGallery(newFile);
//        Toast.makeText(this, nameOfPhoto, Toast.LENGTH_LONG).show();
    }

    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
//        Log.i("SAVED FILE", file.toString());
    }

    public static Bitmap viewToBitmap(View view, int width, int height){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
