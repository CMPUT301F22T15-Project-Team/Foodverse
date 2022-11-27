package com.example.foodverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//https://learntodroid.com/how-to-create-a-login-form-in-android-studio/

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseAuth auth;
    private Button registerButton;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.activity_login_usernameEditText);
        passwordEditText = findViewById(R.id.activity_login_passwordEditText);
        loginButton = findViewById(R.id.activity_login_loginButton);
        registerButton = findViewById(R.id.activity_login_registerButton);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // User already signed in so go to app
        if (user != null) {
            String name = " ";
            if (user.getEmail() != null) {
                name += user.getEmail().split("@")[0];
            }
            String toast = "Welcome back" + name + "!";
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            goToApp();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                if (username.length() > 0 && password.length() > 0) {
                    checkLogin(username, password);
                } else {
                    String toastMessage = "Username or Password are not populated.";
                    Toast.makeText(getApplicationContext(), toastMessage,
                                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                if (password.length() > 0 && password.length() < 6) {
                    String toastMessage = "Password needs to be at least 6 characters.";
                    Toast.makeText(getApplicationContext(), toastMessage,
                            Toast.LENGTH_LONG).show();
                } else if (username.length() > 0 && password.length() > 0) {
                    username += "@email.com";
                    // https://firebase.google.com/docs/auth/android/password-auth
                    auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String toastMessage = "User has been registered.";
                                    Toast.makeText(getApplicationContext(), toastMessage,
                                        Toast.LENGTH_LONG).show();
                                    goToApp();
                                } else {
                                    Log.d(TAG, task.getException().toString());
                                    String toastMessage = "Failed to register.";
                                    Toast.makeText(getApplicationContext(), toastMessage,
                                        Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                } else {
                    String toastMessage = "Username or Password are not populated";
                    Toast.makeText(getApplicationContext(), toastMessage,
                                    Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkLogin(String username, String password) {
        boolean isValid = false;
        username += "@email.com";
        // https://firebase.google.com/docs/auth/android/password-auth
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String toastMessage = "Sign in successful.";
                            Toast.makeText(getApplicationContext(), toastMessage,
                                    Toast.LENGTH_LONG).show();
                            goToApp();
                        } else {
                            String toastMessage = "Invalid login info.";
                            Toast.makeText(getApplicationContext(), toastMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToApp() {
        Intent intent = new Intent(this, StoredIngredientActivity.class);
        startActivity(intent);
    }
}