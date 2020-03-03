package com.techupstudio.countus.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techupstudio.countus.Helpers.CountusPreference;
import com.techupstudio.countus.Helpers.Helper;
import com.techupstudio.countus.Models.Person;
import com.techupstudio.countus.Models.User;
import com.techupstudio.countus.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NewPersonActivity extends AppCompatActivity {


    private static final int CAMERA_PICTURE = 80;
    private static final int CAMERA_PERMISSIONS = 120;
    private boolean isProfileImageChanged;

    private User user;
    private Person person;
    private Uri profileImageURI;
    private ImageView profile;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFireStore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);

        initializeViews();
    }

    private void initializeViews() {

//        progressBar = findViewById(R.id.progressBar);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        isProfileImageChanged = false;
        profileImageURI = new Uri.Builder().build();

        user = ((CountusPreference) getApplicationContext()).getUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(NewPersonActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(NewPersonActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(NewPersonActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS);
                    }
                    else {
                        openCamera();
                    }
                }
            }
        });

        findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToAddNewPerson();
            }
        });
    }

    private void tryToAddNewPerson() {

        person = new Person();

        String lastname, firstname, middlename, age, gender, phone, address, notes;
        lastname = getTextFromEditTextView(R.id.person_l_name);
        firstname = getTextFromEditTextView(R.id.person_f_name);
        middlename = getTextFromEditTextView(R.id.person_m_name);
        age = getTextFromEditTextView(R.id.person_age);
        RadioButton genderButton = ((RadioButton) (findViewById(((RadioGroup) findViewById(R.id.gender)).getCheckedRadioButtonId())));
        gender = (genderButton != null) ? genderButton.getTag().toString(): "";
        notes = getTextFromEditTextView(R.id.notes);
        address = getTextFromEditTextView(R.id.address);
        phone = getTextFromEditTextView(R.id.phone);
        //TODO: validate lastname, firstname, age, gender

        if (lastname.isEmpty()){
            Helper.shortToast(getApplicationContext(), "please lastname field is required!.");
        }
        else if (firstname.isEmpty()){
            Helper.shortToast(getApplicationContext(), "please firstname field is required!.");
        }
        else if (age.isEmpty()){
            Helper.shortToast(getApplicationContext(), "please age field is required!.");
        }
        else if (gender.isEmpty()){
            Helper.shortToast(getApplicationContext(), "please select a gender!.");
        }
        else {

            person.setFirstName(firstname);
            person.setLastName(lastname);
            person.setMiddleName(middlename);
            person.setGender(gender);
            person.setAge(Integer.valueOf(age));
            person.setAddress(address);
            person.setPhone(phone);
            person.setDisplayImageUri(profileImageURI.toString());
            person.getProperties().put("notes", notes);
            person.getProperties().put("timestamp", new Date());


            AlertDialog dialog = new AlertDialog.Builder(NewPersonActivity.this).setTitle("Confirm")
                    .setMessage("Are you sure you want to add this person?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            addUserToDatabase();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();

            dialog.show();
        }

    }

    private void addUserToDatabase() {

        if (!Helper.isNetworkAvailable(getApplicationContext())){
            new AlertDialog.Builder(NewPersonActivity.this).setTitle("Problem")
                    .setMessage("You have no internet connection!.").create().show();
        }
        else {

            final StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("PeoplesProfileImages");

            progressDialog = new ProgressDialog(NewPersonActivity.this);
            progressDialog.setTitle("Adding User");
            progressDialog.setMessage("we are trying to add the new record. please wait.");
            progressDialog.show();

            if (!profileImageURI.toString().isEmpty()) {
                storageReference.child(mCurrentUser.getUid() + person.getLastName() + person.hashCode())
                        .putFile(profileImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            person.setDisplayImageUri(storageReference.getDownloadUrl().toString());
                            saveToFireStore();
                        } else {
                            new AlertDialog.Builder(NewPersonActivity.this).setMessage(task.getException().getMessage()).create().show();
                        }
                    }
                });
            } else {
                saveToFireStore();
            }
        }

    }

    private void saveToFireStore() {
         mFireStore.collection("Registered Persons")
                 .document(mCurrentUser.getUid())
                .collection("People")
                .add(person)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            new AlertDialog.Builder(NewPersonActivity.this).setMessage("Successfully Added Record").create().show();
                            clearTextFromEditTextView(R.id.person_l_name);
                            clearTextFromEditTextView(R.id.person_f_name);
                            clearTextFromEditTextView(R.id.person_m_name);
                            clearTextFromEditTextView(R.id.person_age);
                            clearTextFromEditTextView(R.id.notes);
                            clearTextFromEditTextView(R.id.address);
                            clearTextFromEditTextView(R.id.phone);
                        }
                        else{
                            new AlertDialog.Builder(NewPersonActivity.this).setMessage(task.getException().getMessage()).create().show();
                        }
                    }
                });
    }

    private String getTextFromEditTextView(int id){
        return ((EditText) findViewById(id)).getText().toString().trim();
    }
    private void clearTextFromEditTextView(int id){
        ((EditText) findViewById(id)).setText("");
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PICTURE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(NewPersonActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                profileImageURI = result.getUri();
                isProfileImageChanged = true;
                profile.setImageURI(profileImageURI);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Helper.log("Error: Crop Error.\nMessage: "+result.getError().getMessage());
                Helper.longToast(getApplicationContext(), result.getError().getMessage());
            }
        }

    }



}
