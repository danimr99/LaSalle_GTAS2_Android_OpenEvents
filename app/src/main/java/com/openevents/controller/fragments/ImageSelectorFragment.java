package com.openevents.controller.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.openevents.R;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;


public class ImageSelectorFragment extends Fragment {
    private ImageView imageSelector;

    public ImageSelectorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_selector, container, false);

        // Register an intent to gallery or camera and get the image selected
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Get result received from the intent once completed
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        // Get data from result
                        Intent data = result.getData();

                        // Check if exists data
                        if(data != null) {
                            // Get path of the image
                            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);

                            // Get bitmap from path image
                            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

                            // Set bitmap to ImageView
                            this.imageSelector.setImageBitmap(selectedImage);
                        }
                    }
                }
        );

        // Set onClickListener to the ImageView
        this.imageSelector = view.findViewById(R.id.imageSelector);
        this.imageSelector.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
            intent.putExtra(ImageSelectActivity.FLAG_CROP, true);
            startActivityForResult.launch(intent);
        });

        return view;
    }

    public ImageView getImageSelector() {
        return this.imageSelector;
    }
}