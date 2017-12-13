package com.simoncooke.findmysesh;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.simoncooke.findmysesh.models.EventInfo;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateEventActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private TextView locationTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private Button submitEventButton;
    private LinearLayout addPhotoLayout;
    private LinearLayout locationLayout;
    private LinearLayout dateLayout;
    private LinearLayout timeLayout;
    private ImageView addedPhotoImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private StorageReference mStorage;

    private EventInfo eventInfo;

    private String creatorName;
    private int seshLevel;
    private int partiesHosted;
    private Uri downloadUri;
    private String coordinates;
    private int hour_x;
    private int minute_x;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int GALLERY_INTENT = 2;
    private static final int DIALOG_ID = 0;

    private static final String TAG = "CreateEventActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //views
        locationTextView = (TextView)findViewById(R.id.locationTextView);
        dateTextView = (TextView)findViewById(R.id.dateTextView);
        timeTextView = (TextView)findViewById(R.id.timeTextView);
        eventNameEditText = (EditText)findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = (EditText)findViewById(R.id.eventDescriptionTextView);
        submitEventButton = (Button)findViewById(R.id.submitEventButton);
        addPhotoLayout = (LinearLayout)findViewById(R.id.addPhotoLayout);
        locationLayout = (LinearLayout)findViewById(R.id.locationLayout);
        dateLayout = (LinearLayout)findViewById(R.id.dateLayout);
        timeLayout = (LinearLayout)findViewById(R.id.timeLayout);
        addedPhotoImageView = (ImageView)findViewById(R.id.addedPhotoImageView);

        eventDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        eventDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        submitEventButton.setOnClickListener(this);
        addPhotoLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        dateLayout.setOnClickListener(this);
        timeLayout.setOnClickListener(this);

        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                creatorName = dataSnapshot.child("firstName").getValue().toString();
                creatorName += " " + dataSnapshot.child("lastName").getValue().toString();
                partiesHosted = Integer.parseInt(dataSnapshot.child("partiesHosted").getValue().toString());
                seshLevel = Integer.parseInt(dataSnapshot.child("seshLevel").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void createEvent() {

        String location = locationTextView.getText().toString().trim();
        String time = timeTextView.getText().toString().trim();
        String name = eventNameEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String eventPicture = downloadUri.toString();
        String date = dateTextView.getText().toString().trim();

        eventInfo = new EventInfo(location, date, time, name, description, user.getUid(), creatorName, eventPicture, coordinates);

        if(!TextUtils.isEmpty(location) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)){
            showProgressDialog("Creating Event...");
            mDatabase.child("events").child(user.getUid() + "" + partiesHosted).setValue(eventInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        partiesHosted++;
                        //IMPLEMENT SCALING FORMULA HERE
                        seshLevel++;
                        mDatabase.child("users").child(user.getUid()).child("partiesHosted").setValue(partiesHosted);
                        mDatabase.child("users").child(user.getUid()).child("seshLevel").setValue(seshLevel);
                        startActivity(new Intent(CreateEventActivity.this,HomeActivity.class));
                        Toast.makeText(CreateEventActivity.this,"Event Created Successfully",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CreateEventActivity.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                }
            });

        }else{
            Toast.makeText(CreateEventActivity.this,"Please enter all fields",Toast.LENGTH_SHORT).show();
        }

    }

    private void locationPicker(){
        Log.d(TAG, "locationPicker: Attempting to pick location");
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Intent intent;
        try {
            intent = builder.build(CreateEventActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            FirebaseUser user = mAuth.getCurrentUser();
            Uri uri = data.getData();
            StorageReference filePath = mStorage.child("eventPictures").child(user.getUid() + "" + partiesHosted);
            showProgressDialog("Uploading...");
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();
                    Toast.makeText(CreateEventActivity.this,"File uploaded",Toast.LENGTH_SHORT).show();
                    downloadUri = taskSnapshot.getDownloadUrl();
                    PicassoCache.getPicassoInstance(CreateEventActivity.this).load(downloadUri).fit().centerCrop().into(addedPhotoImageView);
                }
            });
        }

        else if(requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(data, this);
            locationTextView.setText(place.getName().toString());
            coordinates = place.getLatLng().latitude + "," + place.getLatLng().longitude;
        }
    }

    public void datePicker(){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getFragmentManager(), "date");
    }

    private void setDate(final Calendar calendar){
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView)findViewById(R.id.dateTextView)).setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    private void timePicker(){
        showDialog(DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID){
            return new TimePickerDialog(CreateEventActivity.this, mTimePickerListener, hour_x, minute_x, true);
        }
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener mTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            String hourString = Integer.toString(hourOfDay);
            String minuteString = Integer.toString(minute);

            if(hourString.length() == 1){
                hourString = "0" + hourString;
            }
            if(minuteString.length() == 1){
                minuteString = "0" + minuteString;
            }
            timeTextView.setText(hourString + ":" + minuteString);
        }
    };

    private void changeEventPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    public void onClick(View view) {
        if(view == submitEventButton){
            createEvent();
        }else if(view == addPhotoLayout){
            changeEventPicture();
        }else if(view == locationLayout){
            locationPicker();
        }else if(view == dateLayout){
            datePicker();
        }else if(view == timeLayout || view == timeTextView){
            timePicker();
        }
    }

    public static class DatePickerFragment extends DialogFragment{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        }
    }


}
