package com.techupstudio.countus.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techupstudio.countus.Helpers.CountusPreference;
import com.techupstudio.countus.Helpers.Helper;
import com.techupstudio.countus.Models.User;
import com.techupstudio.countus.R;

public class SignInActivity extends AppCompatActivity {

    private EditText email, password;
    private View goto_signup, signin, forgotpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
    }

    private void initializeViews() {

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this).create();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        goto_signup = findViewById(R.id.register);
        signin = findViewById(R.id.proceed);
        remember = findViewById(R.id.remember);
        forgotpassword = findViewById(R.id.forgot_password);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
            }
        });

        goto_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignin();
            }
        });

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, AboutAppActivity.class));
            }
        });
    }

    private void attemptSignin() {
        String _email = email.getText().toString().trim();
        String _password = password.getText().toString().trim();

        if (TextUtils.isEmpty(_email)){
            Toast.makeText(getApplicationContext(), "email field is required!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(_password)){
            Toast.makeText(getApplicationContext(), "password field is required!", Toast.LENGTH_SHORT).show();
        }
        else if (!Helper.isNetworkAvailable(getApplicationContext())){
            alertDialog.setTitle("Network Error");
            alertDialog.setMessage("You do not have any internet connection!. please connect to the internet and try again.");
            alertDialog.show();
        }
        else{
            progressDialog.setTitle("Attempting Signup");
            progressDialog.setMessage("Please wait while we validate your credentials.");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseFirestore.getInstance().collection("Users")
                                .document(mAuth.getCurrentUser().getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()){
                                    User user = task.getResult().toObject(User.class);
                                    ((CountusPreference) getApplicationContext()).setUser(user);
                                    ((CountusPreference) getApplicationContext()).rememberUser(remember.isChecked());
                                    Toast.makeText(getApplicationContext(), "login successful.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                }
                                else {
                                    alertDialog.setTitle(null);
                                    alertDialog.setMessage(task.getException().getMessage());
                                    alertDialog.show();
                                }
                            }
                        });
                    }
                    else{
                        progressDialog.dismiss();
                        alertDialog.setTitle(null);
                        alertDialog.setMessage(task.getException().getMessage());
                        alertDialog.show();
                    }
                }
            });
        }

    }
}
