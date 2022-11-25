package com.example.foodverse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//https://learntodroid.com/how-to-create-a-login-form-in-android-studio/

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.activity_login_usernameEditText);
        passwordEditText = findViewById(R.id.activity_login_passwordEditText);
        loginButton = findViewById(R.id.activity_login_loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                if (username.length() > 0 && password.length() > 0) {
                    if(checkLogin(username, password)) {
                        //switch activity, pass username?
                    } else {
                        String toastMessage = "Invalid login info";
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String toastMessage = "Username or Password are not populated";
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkLogin(String username, String password){
        boolean isValid = false;
        boolean validUser = false;
        boolean correctPassword = false;

        //check database for username, validUser = result
        if (validUser){
            //check database for matching password, correctPassword = result
            if (correctPassword){
                isValid = true;
            }
        }

        return isValid;
    }
}