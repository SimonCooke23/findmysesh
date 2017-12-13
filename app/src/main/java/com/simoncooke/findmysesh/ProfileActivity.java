package com.simoncooke.findmysesh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Button logoutButton;

    private TextView profileNameTextView;
    private TextView partiesHostedTextView;
    private ImageView profilePicture;
    private ImageView coverPhotoImageView;
    private TextView seshLevelTextView;
    private TextView livesInTextView;
    private TextView addBioTextView;
    private TextView bioTextView;
    private RelativeLayout profilePictureLayout;
    private LinearLayout createEventLayout;
    private LinearLayout editProfileLayout;

    private String name;
    private String partiesHosted;
    private String seshLevel;
    private String seshLevelDescription;
    private String livesIn;
    private String bio;
    private String hasCoverPhoto;
    private int seshLevelInt;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseUser user;

    private static final String TAG = "ProfileActivity";
    private static final int PROFILE_PICTURE_GALLERY_INTENT = 1;
    private static final int COVER_PHOTO_GALLERY_INTENT = 2;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_profile:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileNameTextView = findViewById(R.id.profileNameTextView);
        profilePicture = findViewById(R.id.profilePicture);
        logoutButton = findViewById(R.id.logoutButton);
        partiesHostedTextView = findViewById(R.id.partiesHostedTextView);
        seshLevelTextView = findViewById(R.id.seshLevelTextView);
        livesInTextView = findViewById(R.id.livesInTextView);
        profilePictureLayout = findViewById(R.id.profilePictureLayout);
        coverPhotoImageView = findViewById(R.id.coverPhotoImageView);
        addBioTextView = findViewById(R.id.addBioTextView);
        bioTextView = findViewById(R.id.bioTextView);
        createEventLayout = findViewById(R.id.createEventLayout);
        editProfileLayout = findViewById(R.id.editProfileLayout);

        addBioTextView.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        profilePictureLayout.setOnClickListener(this);
        coverPhotoImageView.setOnClickListener(this);
        createEventLayout.setOnClickListener(this);
        editProfileLayout.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(ProfileActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        StorageReference profilePictureFilePath = mStorage.child("profilePictures").child(user.getUid());

        profilePictureFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                PicassoCache.getPicassoInstance(ProfileActivity.this).load(uri).fit().centerCrop().into(profilePicture);
            }
        });


        //Retrieve all necessary data from database
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("firstName").getValue().toString();
                name = name + " " + dataSnapshot.child("lastName").getValue().toString();
                partiesHosted = dataSnapshot.child("partiesHosted").getValue().toString();
                seshLevel = dataSnapshot.child("seshLevel").getValue().toString();
                livesIn = dataSnapshot.child("town").getValue().toString();
                bio = dataSnapshot.child("bio").getValue().toString();
                hasCoverPhoto = dataSnapshot.child("hasCoverPhoto").getValue().toString();

                seshLevelInt = Integer.parseInt(seshLevel);

                switch(seshLevelInt) {
                    case 1: seshLevelDescription = "Sesh Goblin"; break;
                    case 2: seshLevelDescription = "Sesh Enthusiast"; break;
                    case 3: seshLevelDescription = "Sesh Moth"; break;
                    case 4: seshLevelDescription = ""; break;
                    case 5: seshLevelDescription = ""; break;
                    case 6: seshLevelDescription = ""; break;
                    case 7: seshLevelDescription = ""; break;
                    case 8: seshLevelDescription = "Lord Of The Sesh"; break;
                    case 9: seshLevelDescription = "Sesh Grand Master"; break;
                    case 10: seshLevelDescription = "Sesh God"; break;
                    default:
                        Toast.makeText(ProfileActivity.this, "Something is very wrong here", Toast.LENGTH_SHORT).show();
                        break;
                }

                profileNameTextView.setText(name);
                String parties = "Parties Hosted: " + partiesHosted;
                partiesHostedTextView.setText(parties);
                String sesh = "Sesh Level: " + seshLevel + " - " + seshLevelDescription;
                seshLevelTextView.setText(sesh);
                String lives = "Lives in: " + livesIn;
                livesInTextView.setText(lives);

                if(!bio.isEmpty()){
                    addBioTextView.setVisibility(View.GONE);
                    bioTextView.setVisibility(View.VISIBLE);
                    bioTextView.setText(bio);
                }else{
                    addBioTextView.setVisibility(View.VISIBLE);
                    bioTextView.setVisibility(View.GONE);
                }

                if(hasCoverPhoto.equals("1")){
                    StorageReference coverPhotoFilePath = mStorage.child("coverPhotos").child(user.getUid());
                    coverPhotoFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            PicassoCache.getPicassoInstance(ProfileActivity.this).load(uri).fit().centerCrop().into(coverPhotoImageView);
                        }
                    });
                }else{
                    coverPhotoImageView.setImageResource(R.drawable.alt_cover_photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void changeProfilePicture(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PROFILE_PICTURE_GALLERY_INTENT);

    }

    private void changeCoverPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, COVER_PHOTO_GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PROFILE_PICTURE_GALLERY_INTENT && resultCode == RESULT_OK){

            FirebaseUser user = mAuth.getCurrentUser();
            Uri uri = data.getData();
            StorageReference filePath = mStorage.child("profilePictures").child(user.getUid());
            showProgressDialog("Uploading Image...");

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();
                    Toast.makeText(ProfileActivity.this,"File uploaded",Toast.LENGTH_SHORT).show();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    PicassoCache.getPicassoInstance(ProfileActivity.this).load(downloadUri).fit().centerCrop().into(profilePicture);
                }
            });
        }
        else if(requestCode == COVER_PHOTO_GALLERY_INTENT && resultCode == RESULT_OK){

            Uri uri = data.getData();
            StorageReference filePath = mStorage.child("coverPhotos").child(user.getUid());
            showProgressDialog("Uploading Image...");

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();
                    Toast.makeText(ProfileActivity.this,"File uploaded",Toast.LENGTH_SHORT).show();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    PicassoCache.getPicassoInstance(ProfileActivity.this).load(downloadUri).fit().centerCrop().into(coverPhotoImageView);
                    mDatabase.child("users").child(user.getUid()).child("hasCoverPhoto").setValue("1");

                }
            });
        }
    }

    private void SignOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == logoutButton) {
            SignOut();
        }
        else if(v == profilePictureLayout){
            changeProfilePicture();
        }
        else if(v == coverPhotoImageView){
            changeCoverPhoto();
        }
        else if(v == addBioTextView){
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        }
        else if(v == createEventLayout){
            startActivity(new Intent(ProfileActivity.this, CreateEventActivity.class));
        }
        else if(v == editProfileLayout){
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        }
    }



}
