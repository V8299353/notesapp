package com.example.mynotes.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynotes.R;
import com.example.mynotes.auth.AuthenticationHelper;
import com.example.mynotes.auth.LoginActivity;
import com.example.mynotes.databinding.ActivityProfileBinding;
import com.example.mynotes.helper.ProgressBarHandler;
import com.example.mynotes.storage.StorageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding profileBinding;

    private Uri profileImageUri = null;

    private FirebaseUser currentUser = null;

    private AuthenticationHelper authenticationHelper;

    private StorageHelper storageHelper;

    private ProgressBarHandler progressBarHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());

        setContentView(profileBinding.getRoot());

        authenticationHelper = new AuthenticationHelper();

        currentUser = authenticationHelper.getCurrentUser();

        storageHelper = new StorageHelper();

        progressBarHandler = new ProgressBarHandler(this);

        profileBinding.profileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationHelper.logout();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        profileBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .apply(new RequestOptions().fitCenter())
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .into(profileBinding.profileImage);
        }
        if (currentUser.getDisplayName() != null) {
            profileBinding.profileNameET.setText(currentUser.getDisplayName());
        }

        profileBinding.profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarHandler.show();
                if (profileImageUri != null) {
                    storageHelper.uploadImage(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                storageHelper.getProfileStorageReference(currentUser.getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String profileUri = task.getResult().toString();
                                            authenticationHelper.updateProfile(
                                                    profileBinding.profileNameET.getText().toString(), profileUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(
                                                                ProfileActivity.this,
                                                                "Profile Updated Successfully",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    } else {
                                                        Toast.makeText(
                                                                ProfileActivity.this,
                                                                "Profile Update Failed ",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }
                                                    progressBarHandler.hide();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(
                                                    ProfileActivity.this,
                                                    "Profile Update Failed ",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                            progressBarHandler.hide();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to save profile image", Toast.LENGTH_LONG)
                                        .show();
                                progressBarHandler.hide();
                            }
                        }
                    });
                } else {
                    authenticationHelper.updateProfile(
                            profileBinding.profileNameET.getText().toString(),
                            currentUser.getPhotoUrl().toString()
                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Profile Updated Successfully",
                                        Toast.LENGTH_LONG
                                ).show();
                            } else {
                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Profile Update Failed ",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                            progressBarHandler.hide();
                        }
                    });
                }
            }
        });
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Glide.with(ProfileActivity.this).load(result.getData().getData())
                        .apply(new RequestOptions().fitCenter())
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .into(profileBinding.profileImage);
                profileImageUri = result.getData().getData();
            } else {
                Toast.makeText(ProfileActivity.this, "Error loading Image", Toast.LENGTH_LONG).show();
            }
        }
    });

}
