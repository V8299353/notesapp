package com.example.mynotes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotes.MainActivity;
import com.example.mynotes.databinding.ActivitySignUpBinding;
import com.example.mynotes.helper.Helper;
import com.example.mynotes.helper.ProgressBarHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding activitySignUpBinding;
    private ProgressBarHandler progressBarHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(activitySignUpBinding.getRoot());

        progressBarHandler = new ProgressBarHandler(this);


        activitySignUpBinding.loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                activitySignUpBinding.emailLayout.setError(null);
                activitySignUpBinding.passwordLayout.setError(null);
                progressBarHandler.show();

                Editable email = activitySignUpBinding.email.getText();
                if (email == null || email.toString().isEmpty()) {
                    activitySignUpBinding.emailLayout.setError("Enter Email Address");
                    progressBarHandler.hide();
                    return;
                }
                if (!Helper.isValidEmail(email.toString())) {
                    activitySignUpBinding.emailLayout.setError("Enter Valid Email Address");
                    progressBarHandler.hide();
                    return;
                }
                Editable password = activitySignUpBinding.password.getText();
                if (password == null || password.toString().isEmpty()) {
                    activitySignUpBinding.passwordLayout.setError("Enter Password");
                    progressBarHandler.hide();
                    return;
                }
                if (password.toString().length() < 6) {
                    activitySignUpBinding.passwordLayout.setError("Password Should be of length greater than 6");
                    progressBarHandler.hide();
                    return;
                }

                AuthenticationHelper authenticationHelper = new AuthenticationHelper();
                authenticationHelper.signUp(email.toString(),password.toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else {

                            String message = "";
                            if (task.getException() != null && task.getException().getMessage() != null) {
                                message = message + task.getException().getMessage();
                            }
                            Toast.makeText(SignUpActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                            progressBarHandler.hide();

                        }
                    }
                });
            }
        });

    }
}
