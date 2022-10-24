package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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


public class OwnerLoginActivity extends AppCompatActivity {

    public static final String UserEmail = "";
    Button LogInButton;
    EditText Email, Password;
    String EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    UserDBHelper userDBHelper;
    Cursor cursor;
    String TempPassword = "NOT_FOUND";
    TextView RegisterButton,UserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);
        LogInButton = (Button) findViewById(R.id.login);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        userDBHelper = new UserDBHelper(this);
        RegisterButton = (TextView) findViewById(R.id.register_here);
        UserLogin = (TextView) findViewById(R.id.user_login);


        //Adding click listener to log in button.
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calling EditText is empty or no method.
                CheckEditTextStatus();
                // Calling login method.
                LoginFunction();
            }
        });
        // Adding click listener to register button.
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(OwnerLoginActivity.this, OwnerRegisterActivity.class);
                startActivity(intent);
            }
        });

        UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(OwnerLoginActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Login function starts from here.
    @SuppressLint("Range")
    public void LoginFunction() {
        if (EditTextEmptyHolder) {
            // Opening SQLite database write permission.
            sqLiteDatabaseObj = userDBHelper.getWritableDatabase();
            // Adding search email query to cursor.
            cursor = sqLiteDatabaseObj.query(UserDBHelper.TABLE_NAME, null, " " + UserDBHelper.Table_Column_2_Email + "=?", new String[]{EmailHolder}, null, null, null);
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    cursor.moveToFirst();
                    // Storing Password associated with entered email.
                    TempPassword = cursor.getString(cursor.getColumnIndex(UserDBHelper.Table_Column_3_Password));
                    // Closing cursor.
                    cursor.close();
                }
            }
            // Calling method to check final result ..
            CheckFinalResult();
        } else {
            //If any of login EditText empty then this block will be executed.
            Toast.makeText(OwnerLoginActivity.this, "Please Enter UserName or Password.", Toast.LENGTH_LONG).show();
        }
    }

    // Checking EditText is empty or not.
    public void CheckEditTextStatus() {
        // Getting value from All EditText and storing into String Variables.
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();
        // Checking EditText is empty or no using TextUtils.
        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            EditTextEmptyHolder = false;
        } else {
            EditTextEmptyHolder = true;
        }
    }

    // Checking entered password from SQLite database email associated password.
    public void CheckFinalResult() {
        if (TempPassword.equalsIgnoreCase(PasswordHolder)) {
            Toast.makeText(OwnerLoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
            // Going to Dashboard activity after login success message.
            Intent intent = new Intent(OwnerLoginActivity.this, UserDashboardActivity.class);
            // Sending Email to Dashboard Activity using intent.
            intent.putExtra(UserEmail, EmailHolder);
            startActivity(intent);
        } else {
            Toast.makeText(OwnerLoginActivity.this, "UserName or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
        }
        TempPassword = "NOT_FOUND";
    }
}