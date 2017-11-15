package com.simoncooke.findmysesh;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simoncooke.findmysesh.models.EventInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "HomeActivity";
    private static final int ERROR_DIAGLOG_REQUEST = 9001;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private FirebaseAuth mAuth;

    private RecyclerView eventRecyclerView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                case R.id.topbarCreateEvent:
                    startActivity(new Intent(HomeActivity.this, CreateEventActivity.class));
                    return true;
                case R.id.topbarMaps:
                    if(isServicesOk()){
                        startActivity(new Intent(HomeActivity.this, MapsActivity2.class));
                        return true;
                    }
                    return false;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        eventRecyclerView = (RecyclerView)findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationView topBar = (BottomNavigationView) findViewById(R.id.topBar);
        topBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        FirebaseRecyclerAdapter<EventInfo, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EventInfo, EventViewHolder>(
                EventInfo.class,
                R.layout.custom_row,
                EventViewHolder.class,
                mDatabase.child("events")
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, EventInfo model, int position) {

                final String EVENT_KEY = getRef(position).getKey();

                viewHolder.setEventTitleTextView(model.getEventName());
                viewHolder.setEventDescriptionTextView(model.getEventDescription());
                viewHolder.setEventCreatorTextView(model.getCreatorName());
                viewHolder.setEventImageView(model.getEventPictureUrl());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(HomeActivity.this, EventActivity.class).putExtra("EVENT_KEY", EVENT_KEY));
                    }
                });
            }
        };
        eventRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onClick(View view) {

    }

    public static class EventViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        TextView eventTitleTextView;
        TextView eventDescriptionTextView;
        TextView eventCreatorTextView;
        ImageView eventImageView;

        public EventViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            eventTitleTextView = (TextView)itemView.findViewById(R.id.eventTitleTextView);
            eventDescriptionTextView = (TextView)itemView.findViewById(R.id.eventDescriptionTextView);
            eventCreatorTextView = (TextView)itemView.findViewById(R.id.eventCreatorTextView);
            eventImageView = (ImageView)itemView.findViewById(R.id.eventImageView);
        }

        public void setEventTitleTextView(String title) {
            eventTitleTextView.setText(title + "");
        }

        public void setEventDescriptionTextView(String description) {
            eventDescriptionTextView.setText(description + "");
        }

        public void setEventCreatorTextView(String creator) {
            eventCreatorTextView.setText("Created By: " + creator);
        }

        public void setEventImageView(String image){
            Picasso.with(mView.getContext()).load(image).fit().centerCrop().into(eventImageView);
        }

    }

    public boolean isServicesOk(){

        Log.d(TAG, "isServicesOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);
        if(available == ConnectionResult.SUCCESS){

            Log.d(TAG, "isServicesOk: Google play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOk: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIAGLOG_REQUEST);
        }
        else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
