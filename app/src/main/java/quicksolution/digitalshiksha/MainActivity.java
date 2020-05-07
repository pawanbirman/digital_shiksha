package quicksolution.digitalshiksha;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import quicksolution.digitalshiksha.Admin.AdminDriverLoginActivity;
import quicksolution.digitalshiksha.Student.StudentAuthActivity;


public class MainActivity extends AppCompatActivity
{
    private final int mPermissionRequestCode=1;
    private final int mStoragePermissionCode=1;
    private final static String TAG = "MainAcivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
       {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE))

            {
                Log.e(TAG, "inside 2nd if, all permissions are not showed or denied by user");
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CALL_PHONE
                        },
                        mStoragePermissionCode
                );

            }else
                {
                    Log.e(TAG, "inside else, all permissions are showed and granted");
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.CALL_PHONE
                            },
                            mStoragePermissionCode
                    );
                }

        }
        Log.e(TAG, "starting the app hello");
        startService(new Intent(MainActivity.this,onAppKilled.class));
    }

    public  void loginStudent(View view)
    {
        Intent student=new Intent( MainActivity.this,StudentAuthActivity.class );
        startActivity( student );


    }
    public  void loginDriver(View view)
    {
        Intent driver=new Intent( MainActivity.this,AdminDriverLoginActivity.class );
        startActivity( driver );

    }
    public  void loginAdmin(View view)
    {
        Intent admin=new Intent( MainActivity.this, AdminDriverLoginActivity.class );
        startActivity( admin );

    }



}