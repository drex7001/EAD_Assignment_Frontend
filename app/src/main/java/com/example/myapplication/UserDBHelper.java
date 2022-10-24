package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class UserDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "UserTable";
    public static final String Table_Column_ID = "id INTEGER";
    public static final String Table_Column_1_Name = "name";
    public static final String Table_Column_2_Email = "email";
    public static final String Table_Column_3_Password = "password";
    public static final String Table_Column_4_user_type = "userType";
    static String DATABASE_NAME = "EAD";

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+Table_Column_ID+" " +Table_Column_1_Name+" VARCHAR, "+Table_Column_2_Email+" VARCHAR, "+Table_Column_3_Password+" VARCHAR, "+Table_Column_4_user_type+" VARCHAR)";
        database.execSQL(CREATE_TABLE);
//        database.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, email TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}