package com.simoncooke.findmysesh;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class CreateProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;

    private TextView ageEditText;
    private TextView townEditText;
    private Button submitButton;
    private TextView welcomeTextView;
    private ImageView profilePicture;
    private ImageView addPhotoImageView;

    private ProgressDialog progress;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private static final int GALLERY_INTENT = 2;

    private int intAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //views
        ageEditText = (EditText)findViewById(R.id.ageEditText);
        townEditText = (EditText)findViewById(R.id.townEditText);
        submitButton = (Button)findViewById(R.id.submitButton);
        welcomeTextView = (TextView)findViewById(R.id.welcomeTextView);
        profilePicture = (ImageView)findViewById(R.id.profilePicture);
        addPhotoImageView = (ImageView)findViewById(R.id.addPhotoImageView);

        progress = new ProgressDialog(this);

        submitButton.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        addPhotoImageView.setOnClickListener(this);
        ageEditText.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();

        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("firstName").getValue().toString().trim();
                welcomeTextView.setText("Welcome " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = day + "/" + month + "/" + year;
                ageEditText.setText(date);

                Calendar dob = Calendar.getInstance();
                Calendar today = Calendar.getInstance();

                dob.set(year,month,day);

                intAge = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

                if(today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                    intAge--;
                }

            }
        };

    }

    private void saveInfo(){
        String age = ageEditText.getText().toString().trim();
        String town = townEditText.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();

        if(!TextUtils.isEmpty(town) && !TextUtils.isEmpty(age) && intAge >= 18){
            mDatabase.child("users").child(user.getUid()).child("town").setValue(town);
            mDatabase.child("users").child(user.getUid()).child("dateOfBirth").setValue(age);
            Toast.makeText(this,"Information Saved", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateProfileActivity.this,HomeActivity.class));
        }else if(intAge < 18){
            Toast.makeText(this,"You must be over 18 to register", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Please enter all fields", Toast.LENGTH_SHORT).show();
        }
    }


    private void changeProfilePicture(){
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        startActivityForResult(intent, GALLERY_INTENT);

    }

    private void setDob(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year-18,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            FirebaseUser user = mAuth.getCurrentUser();

            Uri uri = data.getData();

            StorageReference filePath = mStorage.child("profilePictures").child(user.getUid());

            progress.setMessage("Uploading...");
            progress.show();


            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progress.dismiss();
                    Toast.makeText(CreateProfileActivity.this,"File uploaded",Toast.LENGTH_SHORT).show();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    PicassoCache.getPicassoInstance(CreateProfileActivity.this).load(downloadUri).fit().centerCrop().into(profilePicture);

                }
            });
        }
    }



    @Override
    public void onClick(View v) {
        if(v == submitButton){
            saveInfo();
        }else if(v == profilePicture || v == addPhotoImageView){
            changeProfilePicture();
        }else if(v == ageEditText){
            setDob();
        }
    }
}

