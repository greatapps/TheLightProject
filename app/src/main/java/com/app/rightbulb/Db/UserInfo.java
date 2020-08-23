package com.app.rightbulb.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class UserInfo extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RightLightUserData.db";
    public static final String CONTACTS_TABLE_NAME = "userInfo";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_USERID = "userId";
    public static final String CONTACTS_COLUMN_CITY = "userProfile";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    private HashMap hp;

    public UserInfo(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, userId text,userProfile text, userSID text, userCountryCode text)"
        );
    }

    public boolean insertContact (String name, String phone, String email, String userId,String userProfile,String userSID,String userCountryCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("userId", userId);
        contentValues.put("userProfile", userProfile);
        contentValues.put("userSID", userSID);
        contentValues.put("userCountryCode", userCountryCode);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from userInfo where userId="+id+"", null );
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
