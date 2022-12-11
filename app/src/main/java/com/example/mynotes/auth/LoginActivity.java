package com.example.mynotes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotes.MainActivity;
import com.example.mynotes.databinding.ActivityLoginBinding;
import com.example.mynotes.helper.Helper;
import com.example.mynotes.helper.ProgressBarHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding activityLoginBinding;
    private ProgressBarHandler progressBarHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());
        setSupportActionBar(activityLoginBinding.toolbar);

        progressBarHandler = new ProgressBarHandler(this);

        activityLoginBinding.signInButton.setOnClickListener(v -> {
            if (activityLoginBinding.email.getText() != null && activityLoginBinding.password.getText()!=null && activityLoginBinding.password.getText().toString().length() >= 6 && Helper.isValidEmail(activityLoginBinding.email.getText().toString())) {
                progressBarHandler.show();
                AuthenticationHelper repo = new AuthenticationHelper();
                repo.login(activityLoginBinding.email.getText().toString(), activityLoginBinding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {

                            String message = "";
                            if (task.getException() != null && task.getException().getMessage() != null) {
                                message = message + task.getException().getMessage();
                            }
                            Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                        }
                        progressBarHandler.hide();
                    }
                });
            } else if (activityLoginBinding.email.getText() == null) {
                activityLoginBinding.email.setError("Enter Email Address");
            } else if (activityLoginBinding.password.getText() == null || activityLoginBinding.password.getText().toString().isEmpty()) {
                activityLoginBinding.password.setError("Enter Password");
            } else if (activityLoginBinding.password.getText().toString().length() < 6) {
                activityLoginBinding.password.setError("Password Should be of length greater than 6");
            } else if (!Helper.isValidEmail(activityLoginBinding.email.getText().toString())) {
                activityLoginBinding.email.setError("Enter Valid Email Address");
            }
        });

        activityLoginBinding.signInLoginCTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });

    }
}
