package com.techupstudio.countus.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techupstudio.countus.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PersonDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setTextView(R.id.person_name, getIntent().getExtras().getString("name", ""));
        setTextView(R.id.person_age, getIntent().getExtras().getString("age", ""));
        setTextView(R.id.person_address, getIntent().getExtras().getString("address", ""));
        setTextView(R.id.person_phone, getIntent().getExtras().getString("phone", ""));
        setTextView(R.id.person_gender, getIntent().getExtras().getString("gender", ""));
        setTextView(R.id.person_notes, getIntent().getExtras().getString("notes", ""));

    }

    public void setTextView(int id, String text){
        ((TextView) findViewById(id)).setText(text);
    }

}
