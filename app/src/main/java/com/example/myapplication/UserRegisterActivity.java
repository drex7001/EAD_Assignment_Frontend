package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//https://www.campcodes.com/mobile-app/android-studio/login-and-registration-app-with-sqlite-database-in-android-studio/
//https://www.youtube.com/watch?v=3865rSz9iOs&t=1s

public class UserRegisterActivity extends AppCompatActivity {

    EditText Email, Password, Name;
    Button Register;
    String NameHolder, EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;
    UserDBHelper userDBHelper;
    Cursor cursor;
    String F_Result = "Not_Found";
    TextView LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        Register = (Button) findViewById(R.id.register);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        Name = (EditText) findViewById(R.id.name);
        userDBHelper = new UserDBHelper(this);
        LoginButton = (TextView) findViewById(R.id.login_here);

        // Adding click listener to register button.
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating SQLite database if dose n't exists
                SQLiteDataBaseBuild();
                // Creating SQLite table if dose n't exists.
                SQLiteTableBuild();
                // Checking EditText is empty or Not.
                CheckEditTextStatus();
                // Method to check Email is already exists or not.
                CheckingEmailAlreadyExistsOrNot();
                // Empty EditText After done inserting process.
                EmptyEditTextAfterDataInsert();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_LONG).show();
                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // SQLite database build method.
    public void SQLiteDataBaseBuild() {
        sqLiteDatabaseObj = openOrCreateDatabase(UserDBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    // SQLite table build method.
    public void SQLiteTableBuild() {
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + UserDBHelper.TABLE_NAME + "(" + UserDBHelper.Table_Column_ID + " PRIMARY KEY AUTOINCREMENT NOT NULL, " + UserDBHelper.Table_Column_1_Name + " VARCHAR, " + UserDBHelper.Table_Column_2_Email + " VARCHAR, " + UserDBHelper.Table_Column_3_Password + " VARCHAR, " + UserDBHelper.Table_Column_4_user_type + " VARCHAR );");
    }

    // Insert data into SQLite database method.
    public void InsertDataIntoSQLiteDatabase() {
        // If editText is not empty then this block will executed.
        if (EditTextEmptyHolder == true) {
            // SQLite query to insert data into table.
            String userType = "user" ;
            SQLiteDataBaseQueryHolder = "INSERT INTO " + UserDBHelper.TABLE_NAME + " (name,email,password,userType) VALUES('" + NameHolder + "', '" + EmailHolder + "', '" + PasswordHolder + "', '" + userType + "');";
            // Executing query.
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
            // Closing SQLite database object.
            sqLiteDatabaseObj.close();
            // Printing toast message after done inserting.
            Toast.makeText(UserRegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
            startActivity(intent);
        }
        // This block will execute if any of the registration EditText is empty.
        else {
            // Printing toast message if any of EditText is empty.
            Toast.makeText(UserRegisterActivity.this, "Please Fill All The Required Fields.", Toast.LENGTH_LONG).show();
        }
    }

    // Empty edittext after done inserting process method.
    public void EmptyEditTextAfterDataInsert() {
        Name.getText().clear();
        Email.getText().clear();
        Password.getText().clear();
    }

    // Method to check EditText is empty or Not.
    public void CheckEditTextStatus() {
        // Getting value from All EditText and storing into String Variables.
        NameHolder = Name.getText().toString();
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();
        String userType = "user";
        if (TextUtils.isEmpty(NameHolder) || TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            EditTextEmptyHolder = false;
        } else {
            EditTextEmptyHolder = true;
        }
    }

    // Checking Email is already exists or not.
    public void CheckingEmailAlreadyExistsOrNot() {
        // Opening SQLite database write permission.
        sqLiteDatabaseObj = userDBHelper.getWritableDatabase();
        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj.query(UserDBHelper.TABLE_NAME, null, " " + UserDBHelper.Table_Column_2_Email + "=?", new String[]{EmailHolder}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                // If Email is already exists then Result variable value set as Email Found.
                F_Result = "Email Found";
                // Closing cursor.
                cursor.close();
            }
        }
        // Calling method to check final result and insert data into SQLite database.
        CheckFinalResult();
    }

    // Checking result
    public void CheckFinalResult() {
        // Checking whether email is already exists or not.
        if (F_Result.equalsIgnoreCase("Email Found")) {
            // If email is exists then toast msg will display.
            Toast.makeText(UserRegisterActivity.this, "Email Already Exists", Toast.LENGTH_LONG).show();
        } else {
            // If email already dose n't exists then user registration details will entered to SQLite database.
            InsertDataIntoSQLiteDatabase();
        }
        F_Result = "Not_Found";
    }

}
