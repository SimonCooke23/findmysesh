package com.simoncooke.findmysesh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;

    private ImageView eventPictureImageView;
    private TextView eventTitleTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventTimeTextView;
    private TextView eventDateTextView;
    private Button seeOnMapButton;
    private String eventCoordinates;

    private String EVENT_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            EVENT_KEY = extras.getString("EVENT_KEY");
        }

        eventPictureImageView = (ImageView)findViewById(R.id.eventPictureImageView);
        eventTitleTextView = (TextView)findViewById(R.id.eventTitleTextView);
        eventDescriptionTextView = (TextView)findViewById(R.id.eventDescriptionTextView);
        eventLocationTextView = (TextView)findViewById(R.id.eventLocationTextView);
        eventTimeTextView = (TextView)findViewById(R.id.eventTimeTextView);
        eventDateTextView = (TextView)findViewById(R.id.eventDateTextView);
        seeOnMapButton = (Button)findViewById(R.id.seeOnMapButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        seeOnMapButton.setOnClickListener(this);

        mDatabase.child("events").child(EVENT_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventTitleTextView.setText(dataSnapshot.child("eventName").getValue().toString());
                eventDescriptionTextView.setText(dataSnapshot.child("eventDescription").getValue().toString());
                eventLocationTextView.setText(dataSnapshot.child("eventLocation").getValue().toString());
                eventTimeTextView.setText(dataSnapshot.child("eventTime").getValue().toString());
                eventDateTextView.setText(dataSnapshot.child("eventDate").getValue().toString());
                String eventPictureUrl = dataSnapshot.child("eventPictureUrl").getValue().toString();
                PicassoCache.getPicassoInstance(EventActivity.this).load(eventPictureUrl).fit().centerCrop().into(eventPictureImageView);

                eventCoordinates = dataSnapshot.child("eventCoordinates").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view == seeOnMapButton){
            startActivity(new Intent(EventActivity.this, ViewEventMapActivity.class).putExtra("EVENT_COORDINATES",eventCoordinates));
        }
    }

    public static class UserInfo {

        public String firstName;
        public String lastName;
        public int partiesHosted;
        public int seshLevel;
        public String town;
        public String dateOfBirth;
        public String email;


        public UserInfo(){

        }

        public UserInfo(String firstName, String lastName, String email, int partiesHosted, int seshLevel, String town, String dateOfBirth){
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.partiesHosted = partiesHosted;
            this.seshLevel = seshLevel;
            this.town = town;
            this.dateOfBirth = dateOfBirth;
        }

    }
}
