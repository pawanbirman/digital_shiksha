package quicksolution.digitalshiksha;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import quicksolution.digitalshiksha.Student.StudentAuthActivity;


public class MainActivity extends AppCompatActivity
{
    private static final int MY_PERMISSION_REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }



    public  void loginStudent(View view)
    {
        Intent student=new Intent( MainActivity.this,StudentAuthActivity.class );
        startActivity( student );


    }
    public  void loginDriver(View view)
    {
        Intent driver=new Intent( MainActivity.this,MainActivity.class );
        startActivity( driver );

    }
    public  void loginAdmin(View view)
    {
        Intent admin=new Intent( MainActivity.this,MainActivity.class );
        startActivity( admin );

    }

}