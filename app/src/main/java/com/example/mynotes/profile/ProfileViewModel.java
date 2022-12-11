package com.example.mynotes.profile;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.ktx.Firebase;

public class ProfileViewModel  extends ViewModel {

    private MutableLiveData<Boolean> isUpdateSuccess = new MutableLiveData<Boolean>(false);

    void signOutUser() {
        FirebaseAuth.getInstance().signOut();
    }

    void updateUser(String name,String profileUrl){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(profileUrl)).build();
        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    isUpdateSuccess.setValue(true);
                }
            }
        });
    }

    LiveData<Boolean> observeStatus()  {
        return isUpdateSuccess;
    }

}
