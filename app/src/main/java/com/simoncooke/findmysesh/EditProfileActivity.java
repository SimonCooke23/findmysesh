package com.simoncooke.findmysesh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private StorageReference mStorage;

    private EditText addBioEditText;
    private EditText townEditText;
    private Button saveChangesButton;

    private static final String TAG = "EditProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //views
        addBioEditText = findViewById(R.id.addBioEditText);
        townEditText = findViewById(R.id.townEditText);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

    }


    private void createEvent() {

        String bio = addBioEditText.getText().toString().trim();
        String town = townEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(bio)){
            mDatabase.child("users").child(user.getUid()).child("bio").setValue(bio).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                        Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(!TextUtils.isEmpty(town)){
            mDatabase.child("users").child(user.getUid()).child("town").setValue(town).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                        Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }



    @Override
    public void onClick(View v) {
        if(v == saveChangesButton){
            createEvent();
        }
    }
}