package com.jainjo.ideafood.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jainjo.ideafood.R;

public class AppPreferences {

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private static final String API_TOKEN_KEY = "com.jainjo.ideafood.api_token";
    private static final String UNAME_KEY = "com.jainjo.ideafood.username";

    public static void setPreferences(Context c) {
        prefs = c.getSharedPreferences(c.getResources().getString(R.string.PREFS_NAME), c.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static String getApiToken() { return prefs.getString( API_TOKEN_KEY,null ); }
    public static void setApiToken(String api_token) {
        editor.putString( API_TOKEN_KEY, api_token ); editor.commit(); }

    public static String getUsername() { return prefs.getString( UNAME_KEY,null ); }
    public static void setUsername(String username) {
        editor.putString( UNAME_KEY, username ); editor.commit(); }

}
