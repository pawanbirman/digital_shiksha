package quicksolution.digitalshiksha.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import quicksolution.digitalshiksha.R;

public class StudentMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location LastLocation;
    LocationRequest locationRequest;
    private Button CallCabCarButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference CustomerDatabaseRef;
    private LatLng CustomerPickUpLocation;

    private DatabaseReference DriverAvailableRef, DriverLocationRef;
    private DatabaseReference DriversRef,busRequest;
    private int radius = 1;

    private Boolean driverFound = false, requestType = false;
    private String driverFoundID="123";
    private String customerID="123",PhoneNumber="9493790771";
    Marker DriverMarker, PickUpMarker;
    GeoQuery geoQuery;

    private ValueEventListener DriverLocationRefListner;


    private TextView txtName, txtPhone, txtCarName;
    private ImageView backImage,callToDriver;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;
    final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_map);

        if (googleApiClient == null)
        {

            googleApiClient = new GoogleApiClient.Builder(StudentMapActivity.this)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            builder.setNeedBle(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final com.google.android.gms.common.api.Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try
                            {
                                status.startResolutionForResult(StudentMapActivity.this,REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {}
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

        }


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customer Requests");
        DriverAvailableRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");



        CallCabCarButton =  (Button) findViewById(R.id.call_a_car_button);


        txtName = findViewById(R.id.name_driver);
        txtPhone = findViewById(R.id.phone_driver);
        txtCarName = findViewById(R.id.car_name_driver);
        profilePic = findViewById(R.id.profile_image_driver);
        backImage=findViewById(R.id.back);
        callToDriver=findViewById(R.id.call_to_driver);

        relativeLayout = findViewById(R.id.rel1);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                DriversRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child("Drivers").child(driverFoundID).child("CustomerRideID");
                DriversRef.removeValue();


                String messageSenderRef = "Bus Requests/" + driverFoundID + "/" + customerID;
                busRequest = FirebaseDatabase.getInstance().getReference();
                busRequest.child(messageSenderRef).removeValue();


                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
                geoFire.removeLocation(customerId);

                Intent intent = new Intent(StudentMapActivity.this, StudentDashboardActivity.class);
                startActivity(intent);

            }



        });


        callToDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+PhoneNumber));
                startActivity(intent);


            }



        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        CallCabCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {



                if (requestType)
                {
                    requestType = false;
                    geoQuery.removeAllListeners();

                    if (driverFoundID != null)
                    {
                        String messageSenderRef = "Bus Requests/" + driverFoundID + "/" + customerID;


                        busRequest = FirebaseDatabase.getInstance().getReference();
                        busRequest.child(messageSenderRef).removeValue();




                        DriversRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child("Drivers").child(driverFoundID).child("CustomerRideID");

                        DriversRef.removeValue();


                        driverFoundID="123";

                    }


                    driverFound = false;
                    radius = 1;

                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
                    geoFire.removeLocation(customerId);

                    if (PickUpMarker != null)
                    {
                        PickUpMarker.remove();
                    }
                    if (DriverMarker != null)
                    {
                        DriverMarker.remove();
                    }

                    CallCabCarButton.setText("Find Nearest BUS");
                    relativeLayout.setVisibility(View.GONE);
                }
                else
                {
                    requestType = true;

                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
                    geoFire.setLocation(customerId, new GeoLocation(LastLocation.getLatitude(), LastLocation.getLongitude()));

                    CustomerPickUpLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    PickUpMarker = mMap.addMarker(new MarkerOptions().position(CustomerPickUpLocation).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

                    CallCabCarButton.setText("Getting your Driver...");
                    getClosestDriverCab();
                }
            }
        });





    }

    private void getClosestDriverCab()
    {
        GeoFire geoFire = new GeoFire(DriverAvailableRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerPickUpLocation.latitude, CustomerPickUpLocation.longitude), radius);


        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                //anytime the driver is called this method will be called
                //key=driverID and the location
                if(!driverFound && requestType)
                {
                    driverFound = true;
                    driverFoundID = key;


                    //we tell driver which customer he is going to have


                    String messageSenderRef = "Bus Requests/" + driverFoundID + "/" + customerID;

                    Map messageBodyDetails = new HashMap();
                    messageBodyDetails.put(messageSenderRef,"true");

                    DriversRef = FirebaseDatabase.getInstance().getReference();
                    DriversRef.updateChildren(messageBodyDetails);



                    //Show driver location on customerMapActivity
                    GettingDriverLocation();
                    CallCabCarButton.setText("Looking for Driver Location...");
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady()
            {
                if(!driverFound && radius <=5000)
                {
                    radius = radius + 1;


                    if(radius==5000)
                    {
                        Toast.makeText(StudentMapActivity.this, "With in 5,000 KM range no BUS available", Toast.LENGTH_SHORT).show();


                        CallCabCarButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        CallCabCarButton.setTextColor(Color.parseColor("#FF0000"));

                        CallCabCarButton.setText("No BUS Available...");

                    }


                    getClosestDriverCab();

                }




            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }



    //and then we get to the driver location - to tell customer where is the driver
    private void GettingDriverLocation()
    {
        DriverLocationRefListner = DriverAvailableRef.child(driverFoundID).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                            double LocationLat = 0;
                            double LocationLng = 0;
                            CallCabCarButton.setText("Driver Found");


                            relativeLayout.setVisibility(View.VISIBLE);
                            getAssignedDriverInformation();


                            if(driverLocationMap.get(0) != null)
                            {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if(driverLocationMap.get(1) != null)
                            {
                                LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            //adding marker - to pointing where driver is - using this lat lng
                            LatLng DriverLatLng = new LatLng(LocationLat, LocationLng);
                            if(DriverMarker != null)
                            {
                                DriverMarker.remove();
                            }


                            Location location1 = new Location("");
                            location1.setLatitude(CustomerPickUpLocation.latitude);
                            location1.setLongitude(CustomerPickUpLocation.longitude);

                            Location location2 = new Location("");
                            location2.setLatitude(DriverLatLng.latitude);
                            location2.setLongitude(DriverLatLng.longitude);

                            int Distance = (int) (location1.distanceTo(location2)/1000.0);

                            if (Distance < 1)
                            {
                                CallCabCarButton.setText("BUS is  Nearby,be quick...");
                            }
                            else
                            {
                                CallCabCarButton.setText("BUS Found: " + String.valueOf(Distance) + " KM");
                            }


                            DriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("your driver is here").icon(BitmapDescriptorFactory.fromResource(R.drawable.school_bus)));




                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }




    private void getAssignedDriverInformation()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Driver").child(driverFoundID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()  &&  dataSnapshot.getChildrenCount() > 0)
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    PhoneNumber = dataSnapshot.child("phone").getValue().toString();
                    String bus = dataSnapshot.child("bus").getValue().toString();

                    txtName.setText(name);
                    txtPhone.setText(PhoneNumber);
                    txtCarName.setText(bus);

                    if (dataSnapshot.hasChild("image"))
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // now let set user location enable
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //

            return;
        }
        //it will handle the refreshment of the location
        //if we dont call it we will get location only once
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {

        //getting the updated location
        LastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }


    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    public void onBackPressed()
    {
        DriversRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverFoundID).child("CustomerRideID");
        DriversRef.removeValue();


        String messageSenderRef = "Bus Requests/" + driverFoundID + "/" + customerID;
        busRequest = FirebaseDatabase.getInstance().getReference();
        busRequest.child(messageSenderRef).removeValue();


        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
        geoFire.removeLocation(customerId);
    }
}
