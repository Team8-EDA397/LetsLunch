package com.letslunch.agileteam8.letslunch.Activities;

/*
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.AppCompatActivity;
import android.view.Window;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap nMap;
    Marker campus;
    private static final double
            LINDHOLMEN_LAT = 57.7067061,
            LINDHOLMEN_LNG = 11.9330267,

            JOHANNEBERG_LAT = 57.6890079,
            JOHANNEBERG_LNG = 11.9726245;

    // Include the OnCreate() method here too, as described above.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        nMap = googleMap;
        // Add a marker in Lindholmen (Gothenburg), Sweden,
        // and move the map's camera to the same location.
        LatLng lindholmen = new LatLng(LINDHOLMEN_LAT, LINDHOLMEN_LNG);
        campus = nMap.addMarker(new MarkerOptions().position(lindholmen)
                .title("Marker in Lindholmen"));

        float zoomLevel = 14.00f; //This goes up to 21
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lindholmen,zoomLevel));
        nMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lindholmen, zoomLevel), 2000, null);

        // Another possible location
        /*
        LatLng johanneberg = new LatLng(JOHANNEBERG_LAT, JOHANNEBERG_LNG);
        googleMap.addMarker(new MarkerOptions().position(johanneberg)
                .title("Marker in Johanneberg"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(johanneberg));

    }
}
*/

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.letslunch.agileteam8.letslunch.R;
import com.letslunch.agileteam8.letslunch.Restaurant;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    // Firebase variables
    DatabaseReference databaseRestaurants;

    String groupID;

    GoogleMap mMap;
    Marker campus;
    private static final double
            LINDHOLMEN_LAT = 57.7067061,
            LINDHOLMEN_LNG = 11.9330267,

            JOHANNEBERG_LAT = 57.6890079,
            JOHANNEBERG_LNG = 11.9726245;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(MapsActivity.this);
        // Add a marker in Lindholmen (Gothenburg), Sweden,
        // and move the map's camera to the same location.

        databaseRestaurants = FirebaseDatabase.getInstance().getReference();

        databaseRestaurants.child("GroupsAndTheirRestaurants").child(groupID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {


                //Loop through restaurants in Firebase
                for(DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                    //Retrieve restaurant from Firebase
                    Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                    //Put restaurant marker on the map
                    Marker restaurantMarker = mMap.addMarker(new MarkerOptions()
                            .position(restaurant.getLatLng())
                            .title(restaurant.getName()));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final LatLng lindholmen = new LatLng(LINDHOLMEN_LAT, LINDHOLMEN_LNG);
        campus = googleMap.addMarker(new MarkerOptions().position(lindholmen)
                .title("Marker in Lindholmen"));

        float zoomLevel = 14.00f; //This goes up to 21
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lindholmen,zoomLevel));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lindholmen, zoomLevel), 2000, null);

        // Another possible location
        /*
        LatLng johanneberg = new LatLng(JOHANNEBERG_LAT, JOHANNEBERG_LNG);
        googleMap.addMarker(new MarkerOptions().position(johanneberg)
                .title("Marker in Johanneberg"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(johanneberg));
        */

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowinfo, null);

                // Getting reference to the TextView to set latitude
                TextView restName = (TextView) v.findViewById(R.id.restaurant);

                // Getting reference to the TextView to set longitude
                TextView people = (TextView) v.findViewById(R.id.people);

                //People names
                TextView person1 = (TextView) v.findViewById(R.id.person1);
                TextView person2 = (TextView) v.findViewById(R.id.person2);

                // Setting the restaurant
                restName.setText(arg0.getTitle());

                // Setting the people
                people.setText("People coming:"+ " 2");

                person1.setText("Pedro Gómez López");
                person2.setText("Rami Salem");

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // Adding and showing marker while touching the GoogleMap
        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                if(arg0.equals(lindholmen)) {
                    // Clears any existing markers from the GoogleMap
                    mMap.clear();

                    // Creating an instance of MarkerOptions to set position
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting position on the MarkerOptions
                    markerOptions.position(arg0);

                    // Animating to the currently touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));

                    // Adding marker on the GoogleMap
                    final Marker marker = mMap.addMarker(markerOptions);

                    // Showing InfoWindow on the GoogleMap
                    marker.showInfoWindow();
                }
            }
        });

        //Add location after long click.
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                final LatLng latLngCopy = latLng;
                final double lat = latLng.latitude;
                final double lng = latLng.longitude;

                //add dialog to allow for adding locations
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsActivity.this);
                alertBuilder.setTitle("Add Location ");
                alertBuilder.setMessage("Enter name of restaurant:");

                //input stuuff
                // Set up the input
                final EditText input = new EditText(MapsActivity.this);
                alertBuilder.setView(input);


                alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String resName = input.getText().toString();

                        Marker m1 = mMap.addMarker(new MarkerOptions()
                            .position(latLngCopy)
                                .title(resName)
                        );
                        // User clicked OK button
                        saveRestaurants(resName, lat, lng);
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });


                AlertDialog dialog = alertBuilder.create();
                dialog.show();



            }
        });

    }

    // Method for saving the restaurant to the Firebase database.
    private void saveRestaurants(String name, double latitude, double longitude) {

        String createdRestaurantID  = this.databaseRestaurants.push().getKey();

        Restaurant currentRestaurant = new Restaurant(createdRestaurantID, name, latitude, longitude);
        System.out.println("******************************************************" + groupID);
        this.databaseRestaurants.child("GroupsAndTheirRestaurants").child(groupID).child(currentRestaurant.getId()).setValue(currentRestaurant).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(MapsActivity.this, "Unable to save restaurant. Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        groupID = getIntent().getStringExtra("GROUP_ID");

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker == campus){
            Intent select = new Intent(MapsActivity.this, SelectionActivity.class);
            startActivity(select);
        }
    }

    //method that defines what happens when clicking a marker
    @Override
    public boolean onMarkerClick(Marker marker) {
        //TODO: implement showing a button that allows you to say you are going to that place
        marker.showInfoWindow();
        return true;
    }
}
