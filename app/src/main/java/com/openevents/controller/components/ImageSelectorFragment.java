package com.openevents.controller.components;

import android.app.Activity;
import android.content.Context;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;


public class ImageSelectorFragment extends Fragment {
    // UI Components
    private CircleImageView imageSelectorRounded;
    private ImageView imageSelectorSquared;

    // Variables
    private final boolean isRounded;
    private ActivityResultLauncher<Intent> startActivityForResult;

    public ImageSelectorFragment(boolean isRounded) {
        this.isRounded = isRounded;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment depending on the shape
        View view;

        if(this.isRounded) {
            view = inflater.inflate(R.layout.fragment_rounded_image_selector, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_squared_image_selector, container, false);
        }

        // Register an intent to gallery or camera and get the image selected
         this.startActivityForResult = registerForActivityResult(
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
                            if(isRounded) {
                                this.imageSelectorRounded.setImageBitmap(selectedImage);
                            } else {
                                this.imageSelectorSquared.setImageBitmap(selectedImage);
                            }
                        }
                    }
                }
        );

        // Set onClickListener to the ImageView
        if(this.isRounded) {
            this.imageSelectorRounded = view.findViewById(R.id.image_selector);
            this.imageSelectorRounded.setOnClickListener(v -> launchImageSelector(v.getContext()));
        } else {
            this.imageSelectorSquared = view.findViewById(R.id.image_selector);
            this.imageSelectorSquared.setOnClickListener(v -> launchImageSelector(v.getContext()));
        }


        return view;
    }

    private void launchImageSelector(Context context) {
        Intent intent = new Intent(context, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
        intent.putExtra(ImageSelectActivity.FLAG_CROP, true);
        startActivityForResult.launch(intent);
    }
}