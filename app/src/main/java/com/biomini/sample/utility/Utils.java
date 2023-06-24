package com.example.bepcom.utility;

import static com.example.bepcom.constant.Constant.sharedPreferences;
import static com.example.bepcom.constant.Constant.sharedPreferencesEditor;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class Utils {


    private static Context context;
    public final static String PREFS_NAME = "com.example.bepcom";


    public static void setInt( String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(key, 0);
    }

    public static void setStr(String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStr(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(key,"DNF");
    }

    public static void setBool(String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBool(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(key,false);
    }

    // SharedPreferences functions
    public static void SavePreference(String Key, String Value) {
        sharedPreferencesEditor.putString(Key, Value).apply();
    }

    public static void SavePreference(String Key, int Value) {
        sharedPreferencesEditor.putInt(Key, Value).apply();
    }


    public static void SavePreference(String Key, float Value) {
        sharedPreferencesEditor.putFloat(Key, Value).apply();
    }

    public static void SavePreference(String Key, boolean Value) {
        sharedPreferencesEditor.putBoolean(Key, Value).apply();
    }


    public static void SavePreference(String Key, Set<String> Value) {
        sharedPreferencesEditor.putStringSet(Key, Value).apply();
    }

    public static String GetPreferenceS(String Key) {
        return sharedPreferences.getString(Key, "");
    }

    public static int GetPreferenceI(String Key) {
        return sharedPreferences.getInt(Key, 0);
    }

    public static float GetPreferenceF(String Key) {
        return sharedPreferences.getFloat(Key, 0.0f);
    }

    public static boolean GetPreferenceB(String Key) {
        return sharedPreferences.getBoolean(Key, false);
    }

    public void ClearPreference() {
        sharedPreferencesEditor.clear().apply();
    }

    public void ClearPreference(String Key) {
        sharedPreferencesEditor.clear().remove(Key).apply();
    }
}
