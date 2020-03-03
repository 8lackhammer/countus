package com.techupstudio.countus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techupstudio.countus.Helpers.CountusPreference;
import com.techupstudio.countus.Models.User;
import com.techupstudio.countus.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (((CountusPreference) getApplicationContext()).isRemeberUser() &&
                FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ((CountusPreference) getApplicationContext()).setUser(user);
                            Toast.makeText(getApplicationContext(), "login successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                        else{
                            gotoLoginActivity();
                        }
                    }
                });
        }
        else{
            gotoLoginActivity();
        }
    }

    private void gotoLoginActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }



}
