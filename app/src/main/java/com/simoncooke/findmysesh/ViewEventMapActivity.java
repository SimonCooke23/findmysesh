package com.simoncooke.findmysesh;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewEventMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            coordinates = extras.getString("EVENT_COORDINATES");
        }

        String[] coordinateArray = coordinates.split(",");
        Double latitude = Double.parseDouble(coordinateArray[0]);
        Double longitude = Double.parseDouble(coordinateArray[1]);

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng eventLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(eventLocation).title("Event Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
    }
}
