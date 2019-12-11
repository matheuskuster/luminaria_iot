package com.example.applamp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Util {

    // Converte um IS para uma string
    public static String inputStream2String(InputStream is, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                is, charset), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }

    // Converte um IS para um Bitmap
    public static Bitmap inputStream2Bitmap(InputStream is) throws IOException {
        try {
            return BitmapFactory.decodeStream(is);
        } finally {
            is.close();
        }
    }

    public static boolean isLogged(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("myprefs", 0);
        return mPrefs.getBoolean("logged", false);
    }

    public static void setLogin(Context context, boolean value, String token) {
        SharedPreferences mPrefs = context.getSharedPreferences("myprefs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("token", token);
        mEditor.putBoolean("logged", value);
        mEditor.commit();
    }


    public static String getToken(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("myprefs", 0);
        String token = mPrefs.getString("token", null);
        return token;
    }
}
