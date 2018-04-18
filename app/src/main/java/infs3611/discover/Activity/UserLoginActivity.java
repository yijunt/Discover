package infs3611.discover.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import infs3611.discover.R;

public class UserLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpButton = findViewById(R.id.signUpButton);
        emailEditText = findViewById(R.id.email);
        loginButton = findViewById(R.id.loginButton);
        passwordEditText = findViewById(R.id.passwordInput);

        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if (view == signUpButton) {
            Intent intent = new Intent(this, UserRegistrationActivity.class);
            startActivity(intent);
        } else if (view == loginButton) {

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                progressDialog.setMessage("Login User...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent mainIntent = new Intent(getBaseContext(), UserActivity.class);
                            startActivity(mainIntent);
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(UserLoginActivity.this, "Login Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }
}
