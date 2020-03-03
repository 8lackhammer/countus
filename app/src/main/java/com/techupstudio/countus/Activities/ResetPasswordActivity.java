package com.techupstudio.countus.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.techupstudio.countus.R;


public class ResetPasswordActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this).create();
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email);
        findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetEmail();
            }
        });
    }


    public void sendResetEmail() {

        progressDialog.setTitle("Sending Email");
        progressDialog.setMessage("Please wait while we try to send you a reset link.");
        progressDialog.show();
        String email = mEmail.getText().toString().trim();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    //TODO: alert email sent
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("your reset password email has been sent successfully.");
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                else{
                    //TODO: show error
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(task.getException().getMessage());
                    alertDialog.show();
                }
            }
        });
    }

}
