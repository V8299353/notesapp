package com.example.mynotes.newnote;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynotes.R;
import com.example.mynotes.TaskModel;
import com.example.mynotes.auth.AuthenticationHelper;
import com.example.mynotes.auth.LoginActivity;
import com.example.mynotes.databinding.FragmentNewNoteBinding;
import com.example.mynotes.firestore.FirestoreHelper;
import com.example.mynotes.helper.ProgressBarHandler;
import com.example.mynotes.storage.StorageHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class NewNoteFragment extends Fragment {

    private FragmentNewNoteBinding binding;

    private NewNoteViewModel newNoteViewModel;


    private Uri imageUri;
    private String lastLatLng;
    private TaskModel oldTask;
    ProgressBarHandler processBarHandler;
    private StorageHelper storageHelper;
    private AuthenticationHelper authenticationHelper;
    private FirestoreHelper firestoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewNoteBinding.inflate(inflater, container, false);
        storageHelper = new StorageHelper();
        authenticationHelper = new AuthenticationHelper();
        firestoreHelper = new FirestoreHelper();
        newNoteViewModel = new ViewModelProvider(requireActivity()).get(NewNoteViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        processBarHandler = new ProgressBarHandler(requireContext());

        binding.mapCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack("newNoteFrag");
                fragmentTransaction.add(R.id.newNoteFragContainerView, MapsFragment.newInstance());
                fragmentTransaction.commit();
            }
        });


        binding.newNoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imageLauncher.launch(Intent.createChooser(intent, "Select Image"));

            }
        });

        if (!newNoteViewModel.isNewTask()) {
            oldTask = newNoteViewModel.getEditTaskMode();
            binding.noteTitle.setText(oldTask.getTitle());
            binding.noteDesc.setText(oldTask.getDescription());
            if (oldTask.getImageUrl() != null) {
                Glide.with(requireContext())
                        .load(oldTask.getImageUrl())
                        .apply(new RequestOptions().fitCenter())
                        .placeholder(R.drawable.ic_baseline_add_a_photo_24)
                        .error(R.drawable.ic_baseline_add_a_photo_24)
                        .into(binding.newNoteImage);
            }
            if (oldTask.getMapUrl() != null) {
                binding.mapCta.setText(oldTask.getMapUrl());
                lastLatLng = oldTask.getMapUrl();
            }
            binding.deleteNoteCta.setVisibility(View.VISIBLE);
        } else {
            binding.deleteNoteCta.setVisibility(View.GONE);
        }

        newNoteViewModel.getLatLng().observe(requireActivity(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                lastLatLng = "https://www.google.com/maps/place/" + latLng.latitude;
                binding.mapCta.setText(lastLatLng);
            }
        });

        binding.deleteNoteCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processBarHandler.show();
                if (oldTask.getImageUrl() != null) {
                    storageHelper.deleteImageWithUrl(oldTask.getImageUrl());
                    firestoreHelper.deleteNotes(oldTask.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(requireContext(),"Note deleted Successfully",Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(),"Note delete failed",Toast.LENGTH_LONG).show();
                            }
                            processBarHandler.hide();
                            requireActivity().finish();
                        }
                    });
                }else{
                    firestoreHelper.deleteNotes(oldTask.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(requireContext(),"Note deleted Successfully",Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(),"Note delete failed",Toast.LENGTH_LONG).show();
                            }
                            processBarHandler.hide();
                            requireActivity().finish();

                        }
                    });
                }

            }
        });

        binding.saveNoteCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!authenticationHelper.isUserLoggedIn()){
                    Intent i = new Intent(requireActivity(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else if (binding.noteTitle.getText() == null || binding.noteTitle.getText().toString().isEmpty() ||binding.noteDesc.getText() == null|| binding.noteDesc.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(), "Title or Description Cannot be empty", Toast.LENGTH_LONG).show();
                }else if (newNoteViewModel.isNewTask()){
                    processBarHandler.show();
                    String uuid = UUID.randomUUID().toString();
                    if (imageUri!=null){
                        storageHelper.uploadNotesImage(imageUri,uuid).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){

                                    storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser().getUid(),uuid).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> urlTask) {
                                            if (task.isSuccessful()){
                                                String downloadUri = urlTask.getResult().toString();
                                                TaskModel taskModel = new TaskModel(uuid,binding.noteTitle.getText().toString(),binding.noteDesc.getText().toString(),lastLatLng,downloadUri,System.currentTimeMillis(),System.currentTimeMillis());
                                                firestoreHelper.addNewDocument(taskModel,uuid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(requireContext(),"Note Added Successfully",Toast.LENGTH_LONG).show();
                                                            requireActivity().finish();
                                                        } else {
                                                            storageHelper.deleteImageWithReference(storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser().getUid(),uuid));
                                                            Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show();
                                                        }
                                                        processBarHandler.hide();
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show();
                                                processBarHandler.hide();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(requireContext(),"Note Failed To add",Toast.LENGTH_LONG).show();
                                    processBarHandler.hide();
                                }
                            }
                        });
                    }else{
                        TaskModel taskModel = new TaskModel(uuid,binding.noteTitle.getText().toString(),binding.noteDesc.getText().toString(),lastLatLng,null,System.currentTimeMillis(),System.currentTimeMillis());
                        firestoreHelper.addNewDocument(taskModel,uuid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Note Added Successfully", Toast.LENGTH_LONG).show();
                                    requireActivity().finish();
                                } else {
                                    Toast.makeText(requireContext(), "Note Failed to add", Toast.LENGTH_LONG).show();
                                }
                                processBarHandler.hide();
                            }
                        });
                    }
                }else {
                    processBarHandler.show();
                    if (imageUri!=null){
                        storageHelper.uploadNotesImage(imageUri,oldTask.getUuid()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){

                                    storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser().getUid(),oldTask.getUuid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                String downloadUri = task.getResult().toString();
                                                TaskModel taskModel = new TaskModel(oldTask.getUuid(),binding.noteTitle.getText().toString(),binding.noteDesc.getText().toString(),lastLatLng,downloadUri,oldTask.getCreatedDate(),System.currentTimeMillis());
                                                firestoreHelper.addNewDocument(taskModel,oldTask.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(requireContext(),"Note Added Successfully",Toast.LENGTH_LONG).show();
                                                            requireActivity().finish();
                                                        } else {
                                                            storageHelper.deleteImageWithReference(storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser().getUid(),oldTask.getUuid()));
                                                            Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show();
                                                        }
                                                        processBarHandler.hide();
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show();
                                                processBarHandler.hide();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(requireContext(),"Note Failed To add",Toast.LENGTH_LONG).show();
                                    processBarHandler.hide();
                                }
                            }
                        });
                    }else {
                        TaskModel taskModel = new TaskModel(oldTask.getUuid(),binding.noteTitle.getText().toString(),binding.noteDesc.getText().toString(),lastLatLng,oldTask.getImageUrl(),oldTask.getCreatedDate(),System.currentTimeMillis());
                        firestoreHelper.addNewDocument(taskModel,oldTask.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(requireContext(), "Note Added Successfully", Toast.LENGTH_LONG).show();
                                    requireActivity().finish();
                                }else {
                                    Toast.makeText(requireContext(), "Note Failed to add", Toast.LENGTH_LONG).show();
                                }
                                processBarHandler.hide();
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Glide.with(NewNoteFragment.this).load(result.getData().getData())
                        .apply(new RequestOptions().fitCenter())
                        .placeholder(R.drawable.ic_baseline_add_a_photo_24)
                        .error(R.drawable.ic_baseline_add_a_photo_24)
                        .into(binding.newNoteImage);
                imageUri = result.getData().getData();
            } else {
                Toast.makeText(requireContext(), "Error loading Image", Toast.LENGTH_LONG).show();
            }
        }
    });
}
