package com.example.garudax;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UploadImageActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnCamera, btnGallery, btnContinue;

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_image);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        imgPreview = findViewById(R.id.imgPreview);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnContinue = findViewById(R.id.btnContinue);

        btnCamera.setOnClickListener(v -> {

            Intent cameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
            );

            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        btnGallery.setOnClickListener(v -> {

            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        });

        btnContinue.setOnClickListener(v -> {

            Toast.makeText(
                    UploadImageActivity.this,
                    "Image selected successfully",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == CAMERA_REQUEST) {

                imgPreview.setImageBitmap(
                        (android.graphics.Bitmap) data.getExtras().get("data")
                );

            } else if (requestCode == GALLERY_REQUEST) {

                Uri imageUri = data.getData();
                imgPreview.setImageURI(imageUri);
            }
        }
    }
}