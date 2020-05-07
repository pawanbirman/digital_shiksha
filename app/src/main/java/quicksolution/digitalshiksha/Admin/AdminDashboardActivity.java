package quicksolution.digitalshiksha.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.paperdb.Paper;
import quicksolution.digitalshiksha.MainActivity;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.StudentDashboardActivity;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView Syllabus,Results,Toppers;
    private ImageView FindAllBus,AddOnDashboard,AddDriver;
    private String saveCurrentDate, saveCurrentTime,productRandomKey;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_dashboard );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle("Admin Home");
        setSupportActionBar( toolbar );
        mAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat( "MMddyyyy" );
        saveCurrentDate = currentDate.format( calendar.getTime() );

        SimpleDateFormat currentTime = new SimpleDateFormat( "HHmmss" );
        saveCurrentTime = currentTime.format( calendar.getTime() );

        productRandomKey=saveCurrentDate + saveCurrentTime;

        Syllabus=(ImageView)findViewById( R.id.admin_syllabus_img );
        Results=(ImageView)findViewById( R.id.admin_result_img);
        Toppers=(ImageView)findViewById( R.id.admin_toppers_img);
        FindAllBus=(ImageView)findViewById( R.id.admin_find_all_bus_img );
        AddOnDashboard=(ImageView)findViewById( R.id.admin_add_dashboard);
        AddDriver=(ImageView)findViewById( R.id.admin_add_driver_img);



        Syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAllDriverActivity.class);
                startActivity(intent);
            }
        });

        Toppers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAddServicesActivity.class);
                intent.putExtra("Service", "Toppers");
                intent.putExtra("pid",productRandomKey);
                startActivity(intent);
            }
        });


        Results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
                intent.putExtra("Service", "Online Services");
                intent.putExtra("pid",productRandomKey);
                startActivity(intent);
            }
        });

        FindAllBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAllDriverActivity.class);
                startActivity(intent);
            }
        });

        AddDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminDashboardActivity.this, DriverRegisterActivity.class);
                startActivity(intent);
            }
        });


        AddOnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAddServicesActivity.class);
                intent.putExtra("Service", "Student Dashboard");
                intent.putExtra("pid",productRandomKey);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            {
            final AlertDialog.Builder builder=new AlertDialog.Builder(AdminDashboardActivity.this);
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
                    Intent intent=new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.admin_dashboard, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_admin_MaintainNewIdeas)
        {
            Intent intent = new Intent(AdminDashboardActivity.this, StudentDashboardActivity.class);
            intent.putExtra( "Type","admin" );
            startActivity(intent);

        }
        else if (id == R.id.nav_admin_logout)
        {

            Paper.init(this);
            Paper.book().destroy();
            mAuth.signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            startActivity(intent);

        }






        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}