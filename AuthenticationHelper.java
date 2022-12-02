package com.example.mynotes.auth;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.annotation.Nullable;

public class AuthenticationHelper {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public Task<AuthResult> signUp(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<Void> updateProfile(@Nullable String name, @Nullable String uri) {
        UserProfileChangeRequest.Builder userProfileChangeRequest =
                new UserProfileChangeRequest.Builder();
        if (name != null) {
            userProfileChangeRequest.setDisplayName(name);
        }
        if (uri != null) {
            userProfileChangeRequest.setPhotoUri(Uri.parse(uri));
        }
        return getCurrentUser().updateProfile(userProfileChangeRequest.build());
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
}
