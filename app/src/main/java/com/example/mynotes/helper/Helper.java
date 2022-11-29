package com.example.mynotes.helper;

import android.icu.text.SimpleDateFormat;

import java.util.Locale;

public class Helper {

    public static boolean isValidEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getDateString(Long time)   {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.ENGLISH);
        return simpleDateFormat.format(time);
    }
}
