package com.app.rightbulb.Prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {// PREF FILE WILL STORE ALL DETAILS OF USER
    public static boolean isLogin(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("IS_LOGIN",false);//by default false;
    }
    public static void setLogin(Context context,boolean value){//store login state
        SharedPreferences sharedPreferences= context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("IS_LOGIN",value).apply();
    }
    public static String getUserID(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_ID","0");
    }
    public static void setUserID(Context context,String id){
        SharedPreferences sharedPreferences= context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("USER_ID",id).apply();
    }
    public static String getToken(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_ID","0");
    }
    public static void setToken(Context context,String id){
        SharedPreferences sharedPreferences= context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("USER_ID",id).apply();
    }
}
