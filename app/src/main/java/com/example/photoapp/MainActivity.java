package com.example.photoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn_take, btn_list, btn_load;
    TextView tv_message;
    ImageView iv_photo;
    List<Uri> uriList;
    // code used to identify a camera intent
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int SELECT_A_PHOTO = 2;


    // name of file saved by the camera
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_take = findViewById(R.id.btn_take);
        btn_list = findViewById(R.id.btn_list);
        btn_load = findViewById(R.id.btn_load);
        iv_photo = findViewById(R.id.iv_photo);
        tv_message = findViewById(R.id.tv_message);


        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intet to get select photo
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // start intent
                startActivityForResult(i, SELECT_A_PHOTO);
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PhotoList.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // set the image into the iv_photo view
        ImageView iv_photo;
        iv_photo = findViewById(R.id.iv_photo);
        TextView tv_message;
        tv_message = findViewById(R.id.tv_message);
        uriList = ( (MyApplication) this.getApplication()).getUriList();




        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            Glide.with(this).load(currentPhotoPath).into(iv_photo);

            // show the file name in tv_message

            tv_message.setText(currentPhotoPath);

            uriList.add( Uri.fromFile(new File(currentPhotoPath)));
        }
        if (requestCode == SELECT_A_PHOTO && resultCode == RESULT_OK) {
            Uri selectedPhoto = data.getData();


            Glide.with(this).load(selectedPhoto).into(iv_photo);

            // show the file name in tv_message

            tv_message.setText(selectedPhoto.toString());

            uriList.add(selectedPhoto);

        }

        }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Something went wrong. Could not create file.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.photoapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}