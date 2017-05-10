package com.letslunch.agileteam8.letslunch.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.letslunch.agileteam8.letslunch.R;
import com.letslunch.agileteam8.letslunch.Restaurant;
import com.letslunch.agileteam8.letslunch.Utils.DBHandler;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int TAG_CODE_PERMISSION_LOCATION = 0;
    // Firebase variables
    DBHandler database;

    DatabaseReference databaseReference;
    DatabaseReference dbResSelectionRef=null;

    View.OnClickListener eatHereListener = null;
    Button eatHereButton;

    String groupID;

    String prevSelection=null;

    GoogleMap mMap;
    Marker campus;

    private static final double
            LINDHOLMEN_LAT = 57.7067061,
            LINDHOLMEN_LNG = 11.9330267;



    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(MapsActivity.this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {googleMap.setMyLocationEnabled(true);googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, TAG_CODE_PERMISSION_LOCATION);
        }

        database = DBHandler.getInstance();
        database.setActivity(this);

        dbResSelectionRef = database.databaseReference.child("UsersAndTheirRestaurants").child(groupID).child(database.getUser());
        if(dbResSelectionRef!=null){
            dbResSelectionRef.addValueEventListener(database.getSelectionListener());
        }


        database.databaseReference.child("GroupsAndTheirRestaurants").child(groupID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {


                //Loop through restaurants in Firebase
                for(DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                    //Retrieve restaurant from Firebase
                    Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);

                    //Put restaurant marker on the map
                    final Marker restaurantMarker = mMap.addMarker(new MarkerOptions()
                            .position(restaurant.getLatLng())
                            .title(restaurant.getName()));
                    if (restaurantMarker.getTag()==null) {
                        restaurantMarker.setTag(restaurant.getId());
                    }

                    //attempr to retrieve users at the restaurant
                    database.databaseReference.child("RestaurantsAndTheirUsers")
                            .child(restaurant.getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    restaurantMarker.setSnippet(Long.toString(dataSnapshot2.getChildrenCount()));

                                    for (DataSnapshot userSnap : dataSnapshot2.getChildren()) {
                                        restaurantMarker.setSnippet(restaurantMarker.getSnippet() + "," + userSnap.getValue());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //setting current location
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.GPS_PROVIDER;

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        currentLatitude = lastKnownLocation.getLatitude();
        currentLongitude = lastKnownLocation.getLongitude();


        LatLng zoomPoint = new LatLng(currentLatitude,currentLongitude);
        float zoomLevel = 14.00f;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomPoint, zoomLevel), 2000, null);


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

                // Setting the restaurant
                String restaurant = arg0.getTitle();
                restName.setText(restaurant);

                String[] info = arg0.getSnippet().split(",");


                // Setting the people
                people.setText("People coming:"+ info[0]);

                //change to get values from snippet string

                //People names
                TextView person1 = (TextView) v.findViewById(R.id.person1);



                CharSequence text = "";
                for (int i=1; i< info.length; i++ ) {
                    if ( person1.getText()!=null) {
                        text=person1.getText();
                    }
                    if (i+1!=info.length) {
                        person1.setText(text + info[i] + "\n");
                    }
                    else {
                        person1.setText(text + info[i]);
                    }

                }

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // Adding and showing marker while touching the GoogleMap
        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                if(eatHereButton!=null) {
                    eatHereButton.setVisibility(View.INVISIBLE);
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

                //input stuff

                // Set up the input
                final EditText input = new EditText(MapsActivity.this);
                alertBuilder.setView(input);


                alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String resName = input.getText().toString();

                        Marker m1 = mMap.addMarker(new MarkerOptions()
                            .position(latLngCopy)
                                .title(resName)
                                    .snippet("0")

                        );
                        // User clicked OK button
                        database.saveRestaurants(resName, lat, lng, m1);

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

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick (Marker arg0) throws  SecurityException{

                // Navigate until the restaurant pointed by the marker
                LatLng position = arg0.getPosition();
                String cLo = Double.toString(position.longitude);
                String cLa = Double.toString(position.latitude);

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+cLa+","+cLo+"&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) throws SecurityException{
        switch (requestCode) {
            case TAG_CODE_PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mMap.setMyLocationEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        FloatingActionButton rest = (FloatingActionButton) findViewById(R.id.myFAB);
        rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Shows all restaurants near your location
                String cLa = Double.toString(currentLatitude);
                String cLo = Double.toString(currentLongitude);

                Uri gmmIntentUri = Uri.parse("geo:"+cLa+","+cLo+"?z=10&q=restaurants");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public ValueEventListener getSelectionListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    prevSelection =(String)dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    //method that defines what happens when clicking a marker
    @Override
    public boolean onMarkerClick(final Marker marker) {
        final String restId = marker.getTag().toString();
        marker.showInfoWindow();
        database.databaseReference.child("RestaurantsAndTheirUsers").child(restId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //Retrieve users at the restaurant
                database.databaseReference.child("RestaurantsAndTheirUsers")
                        .child(restId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                marker.setSnippet(Long.toString(dataSnapshot2.getChildrenCount()));


                                for (DataSnapshot userSnap : dataSnapshot2.getChildren()) {

                                    marker.setSnippet(marker.getSnippet() + "," + userSnap.getValue());
                                }

                                //refresh infowindow with new info
                                if (marker != null && marker.isInfoWindowShown()) {
                                    marker.hideInfoWindow();
                                    marker.showInfoWindow();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Animating to the currently touched marker
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        eatHereButton = (Button) findViewById(R.id.eatHereButton);
        eatHereButton.setVisibility(View.VISIBLE);

        eatHereListener = new View.OnClickListener() {
            CharSequence text="";
            @Override
            public void onClick(View v) {
                if (marker.getTag()!=null) {
                    database.joinRestaurant(marker.getTag().toString(), prevSelection);
                    text = "Joining Restaurant";
                    eatHereButton.setVisibility(View.INVISIBLE);
                }
                else {
                    text = "failed to join restaurant try again ";
                }
                int duration = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        };
        eatHereButton.setOnClickListener(eatHereListener);



        return true;
    }




    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {

            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }
    }
      
    

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

}
