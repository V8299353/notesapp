package com.example.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mynotes.auth.AuthenticationHelper;
import com.example.mynotes.auth.LoginActivity;
import com.example.mynotes.databinding.ActivityLandBinding;

public class LandActivity extends AppCompatActivity {

    private ActivityLandBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLandBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        CountDownTimer countDownTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (new AuthenticationHelper().isUserLoggedIn()) {
                    startActivity(new Intent(LandActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(LandActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        countDownTimer.start();
    }
}
