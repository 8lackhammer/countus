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

import com.techupstudio.countus.Helpers.CountusPreference;
import com.techupstudio.countus.Helpers.Helper;
import com.techupstudio.countus.Models.User;
import com.techupstudio.countus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    public static final String NORMAL = "--normal--";
    private EditText name, email, phone, password, confirmPassword;
    private View back, signup, goto_login, terms;
    private FirebaseAuth mAuth;
    private CheckBox remember;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        initializeViews();
    }

    private void initializeViews() {
        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        remember = findViewById(R.id.remember);
        signup = findViewById(R.id.proceed);
        goto_login = findViewById(R.id.login);


        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this).create();
        mAuth = FirebaseAuth.getInstance();

        setupViewListeners();
    }

    private void setupViewListeners() {
        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, AboutAppActivity.class));
            }
        });
    }


    private void attemptSignUp(){

        final String _name = name.getText().toString().trim();
        final String _email = email.getText().toString().trim();
        String _password = password.getText().toString().trim();
        String _confirm = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(_name)){
            Toast.makeText(getApplicationContext(), "name field is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(_email)){
            Toast.makeText(getApplicationContext(), "email field is required", Toast.LENGTH_SHORT).show();
        }
        else if (!Helper.isValidEmailId(_email)){
            Toast.makeText(getApplicationContext(), "email provided is not valid", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(_password)){
            Toast.makeText(getApplicationContext(), "password field is required", Toast.LENGTH_SHORT).show();
        }
        else if (_password.length() < 5){
            Toast.makeText(getApplicationContext(), "password is too short", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(_confirm)){
            Toast.makeText(getApplicationContext(), "please confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if (!_password.equals(_confirm)){
            Toast.makeText(getApplicationContext(),
                    "your passwords do not match please check an try again!.", Toast.LENGTH_SHORT).show();
        }
        else if (!Helper.isNetworkAvailable(getApplicationContext())){
            alertDialog.setTitle("Network Error");
            alertDialog.setMessage("You do not have any internet connection!. please connect to the internet and try again.");
            alertDialog.show();
        }
        else {
            progressDialog.setTitle("Attempting Signup");
            progressDialog.setMessage("Please wait while we try to register your account.");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(_email, _password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                final User user = new User();
                                user.setName(_name);
                                user.setEmail(_email);

                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(mAuth.getCurrentUser().getUid())
                                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();

                                        if (task.isSuccessful()){
                                           Toast.makeText(getApplicationContext(), "signup successful. you can login in now.", Toast.LENGTH_SHORT).show();
                                            ((CountusPreference) getApplicationContext()).setUser(user);
                                            ((CountusPreference) getApplicationContext()).rememberUser(remember.isChecked());
                                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                           finish();
                                       }
                                       else{
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

