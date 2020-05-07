package quicksolution.digitalshiksha.Driver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationCallback;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import quicksolution.digitalshiksha.Admin.AdminDashboardActivity;
import quicksolution.digitalshiksha.Admin.AdminDriverLoginActivity;
import quicksolution.digitalshiksha.MainActivity;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.ChatWithSchoolStaffActivity;
import quicksolution.digitalshiksha.Student.SettingsNewActivity;
import quicksolution.digitalshiksha.Student.StudentDashboardActivity;
import quicksolution.digitalshiksha.Student.StudentMapActivity;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;


    GoogleApiClient googleApiClient;
    Location LastLocation;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    private Button LogoutDriverBtn,ChatBtn,SettingBtn;
    private Button SettingsDriverButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLogOutUserStatus = true;


    //getting request customer's id
    private String customerID = "";
    private String driverID;
    private DatabaseReference AssignedCustomerRef;
    private DatabaseReference AssignedCustomerPickUpRef;
    Marker PickUpMarker;

    private ImageView backImageDriver;

    private ValueEventListener AssignedCustomerPickUpRefListner;

    private TextView txtName, txtPhone;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;
    final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();
        Paper.init(this);


        txtName = findViewById(R.id.name_customer);
        txtPhone = findViewById(R.id.phone_customer);
        profilePic = findViewById(R.id.profile_image_customer);
        relativeLayout = findViewById(R.id.rel2);
        backImageDriver=findViewById(R.id.back_driver);
        LogoutDriverBtn=findViewById(R.id.logout_driver_btn);
        SettingBtn=findViewById(R.id.driver_settings_btn);


        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(DriverMapActivity.this)
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
                                status.startResolutionForResult(DriverMapActivity.this,REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {}
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

        }



        backImageDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {



                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);
                geoFireAvailability.removeLocation(userID);

                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                Process.killProcess(Process.myPid());
                finish();
            }



        });
        LogoutDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Paper.book().destroy();

                DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);
                geoFireAvailability.removeLocation(userID);
                startActivity(new Intent(DriverMapActivity.this, MainActivity.class));
                finish();
                Process.killProcess(Process.myPid());


            }



        });


        SettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                Intent intent = new Intent(DriverMapActivity.this, DriverSettingActivity.class);
                startActivity(intent);
            }



        });







        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        getAssignedCustomersRequest();
    }

    private void getAssignedCustomersRequest()
    {
        AssignedCustomerRef = FirebaseDatabase.getInstance().getReference()
                .child("Bus Requests").child(driverID);


        AssignedCustomerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                customerID = dataSnapshot.getValue().toString();
                relativeLayout.setVisibility(View.GONE);
                //getting assigned customer location
                getAssignedCustomerInformation();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        if(currentLogOutUserStatus)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }






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

        if(getApplicationContext() != null)
        {
            //getting the updated location
            LastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));


            if(currentLogOutUserStatus)
            {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);
                geoFireAvailability.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));

            }

        }



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
    protected void onStop()
    {
        super.onStop();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);
        geoFireAvailability.removeLocation(userID);


    }


    private void getAssignedCustomerInformation()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Student").child(customerID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()  &&  dataSnapshot.getChildrenCount() > 0)
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();

                    Toast.makeText(DriverMapActivity.this, ""+name+"  is waiting.....", Toast.LENGTH_LONG).show();
                    Toast.makeText(DriverMapActivity.this, ""+name+"  is waiting.....", Toast.LENGTH_LONG).show();

                    reference.removeValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void onBackPressed() {



            final AlertDialog.Builder builder=new AlertDialog.Builder(DriverMapActivity.this);
            builder.setMessage("Are you sure want to Exit?");
            builder.setCancelable(true);
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                    GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);
                    geoFireAvailability.removeLocation(userID);

                    Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                    startActivity(intent);
                    Process.killProcess(Process.myPid());
                    finish();

                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }






}
