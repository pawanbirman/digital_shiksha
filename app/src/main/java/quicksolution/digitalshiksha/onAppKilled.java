package quicksolution.digitalshiksha;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class onAppKilled extends Service {
    private DatabaseReference RootRef;
    public static final String DEFAULT="NA";
    public String currentUserID="123",saveCurrentDate,saveCurrentTime;
    public static final String TAG = "ChatWithStaff";



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        Log.e(TAG, " on create me h ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, " onStartCommand me h");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, " state online offline " + "online");
        String  customerId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference customerRequests= FirebaseDatabase.getInstance().getReference("Customer Requests");
        GeoFire geoFire = new GeoFire(customerRequests);
        geoFire.removeLocation(customerId);

        DatabaseReference driverAvailable= FirebaseDatabase.getInstance().getReference("Drivers Available");
        GeoFire geoFireDriverAvailable = new GeoFire(driverAvailable);
        geoFireDriverAvailable.removeLocation(customerId);
    }

}
