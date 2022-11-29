package com.example.mynotes.storage;

import android.net.Uri;

import com.example.mynotes.auth.AuthenticationHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageHelper {

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private StorageReference profilePhotoStorage  =
            FirebaseStorage.getInstance().getReference().child("profileImages");

    private StorageReference notesPhotoStorage  =
            FirebaseStorage.getInstance().getReference().child("notesImage");

    public UploadTask uploadImage(Uri imageUri) {
        if(new AuthenticationHelper().isUserLoggedIn()) {
            return profilePhotoStorage.child(new AuthenticationHelper().getCurrentUser().getUid()).putFile(imageUri);
        }
        return null;
    }

    public UploadTask uploadNotesImage(Uri imageUri,String fileName) {
        if(new AuthenticationHelper().isUserLoggedIn()) {
            return notesPhotoStorage.child(new AuthenticationHelper().getCurrentUser().getUid()).child(fileName).putFile(imageUri);
        }
        return null;
    }

    public StorageReference getNotesImageStorageReference(String userId ,String fileName ) {
        return notesPhotoStorage.child(userId).child(fileName);
    }

    public Task<Void> deleteImageWithUrl(String url)  {
        return firebaseStorage.getReferenceFromUrl(url).delete();
    }

    public void deleteImageWithReference(StorageReference storageReference) {
        storageReference.delete();
    }

    public StorageReference getProfileStorageReference(String userId) {
        return profilePhotoStorage.child(userId);
    }

}