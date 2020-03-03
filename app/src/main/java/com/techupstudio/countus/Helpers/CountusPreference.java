package com.techupstudio.countus.Helpers;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.firebase.FirebaseApp;
import com.techupstudio.countus.Models.User;

public class CountusPreference extends Application {

    private User user;

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(getApplicationContext());
        super.onCreate();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void rememberUser(boolean state) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("remember",0).edit();
        editor.putBoolean("rememberUser", state);
        editor.apply();
    }

    public boolean isRemeberUser(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("remember", 0);
        return preferences.getBoolean("rememberUser", false);
    }
}
