package quicksolution.digitalshiksha.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import quicksolution.digitalshiksha.Admin.AdminAddServicesActivity;
import quicksolution.digitalshiksha.Admin.AdminDashboardActivity;
import quicksolution.digitalshiksha.Admin.AdminDriverLoginActivity;
import quicksolution.digitalshiksha.MainActivity;
import quicksolution.digitalshiksha.Model.Products;
import quicksolution.digitalshiksha.Prevalent.Prevalent;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.ViewHolder.DisplayViewHolder;
import quicksolution.digitalshiksha.onAppKilled;

public class StudentDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;


    private DatabaseReference ProductRef, GetUserDetails;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String saveCurrentDate = "123", saveCurrentTime = "123", productRandomKey, id = "123";
    private String getType = "";
    public static final String DEFAULT = "NA";
    public static final String TAG = "ChatWithStaff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            getType = getIntent().getStringExtra("Type");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mAuth = FirebaseAuth.getInstance();


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        id = sharedPreferences.getString("Id", DEFAULT);


        setSupportActionBar(toolbar);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy  ");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH : mm :ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        GetUserDetails = FirebaseDatabase.getInstance().getReference().child("Users");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        final TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        final CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);


        if (!getType.equals("admin")) {
            GetUserDetails.child("Student").child(id.toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 2) {
                        if (dataSnapshot.child("image").exists()) {
                            String image = dataSnapshot.child("image").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            Picasso.get().load(image).into(profileImageView);
                            userNameTextView.setText(name);


                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!getType.equals("admin")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboardActivity.this);
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
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    finish();
                    startActivity(intent);



                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (getType.equals("admin")) {
            Intent intent = new Intent(StudentDashboardActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_call) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:09493790771"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_syllabus) {
            // Handle the camera action
        } else if (id == R.id.nav_feesStructure) {
            Intent intent = new Intent(StudentDashboardActivity.this, FeesStructureActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(StudentDashboardActivity.this, ChatWithSchoolStaffActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_meetWithToppers) {

            Intent intent = new Intent(StudentDashboardActivity.this, MeetWithToppersActivity.class);
            intent.putExtra( "Type",getType );
            startActivity(intent);

        } else if (id == R.id.nav_results) {

        } else if (id == R.id.nav_busLocation) {

            Intent intent = new Intent(StudentDashboardActivity.this, StudentMapActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(StudentDashboardActivity.this, SettingsNewActivity.class);
            intent.putExtra("Type", "Student");
            startActivity(intent);

        } else if (id == R.id.nav_logout) {


            mAuth.signOut();
            startActivity(new Intent(StudentDashboardActivity.this, MainActivity.class));
            finish();

        } else if (id == R.id.nav_share)
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody="I strongly recommend you to download Digital Shiksha app for the knowledge of a different type of Education World  https://play.google.com/store/apps/details?id=quicksolution.digitalshiksha  Download  now... ";
            String shareSub="your sub here";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(shareIntent,"Share via"));

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, DisplayViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, DisplayViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final DisplayViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getType.equals("admin"))
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {

                                                    "Edit Existing Idea",
                                                    "Delete Existing Idea",


                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboardActivity.this);
                                    builder.setTitle("Admin Options:");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            if (i == 0) {
                                                Intent intent = new Intent(StudentDashboardActivity.this, AdminAddServicesActivity.class);
                                                intent.putExtra("pid", model.getPid());
                                                intent.putExtra("Service", model.getService());
                                                startActivity(intent);

                                            }

                                            if (i == 1) {
                                                ProductRef
                                                        .child(model.getPid())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(StudentDashboardActivity.this, "Deleted..", Toast.LENGTH_SHORT).show();

                                                                }

                                                            }
                                                        });
                                            }

                                        }
                                    });
                                    builder.show();

                                }
                                else
                                    {
                                        CharSequence options[] = new CharSequence[]
                                                {

                                                        "View Full Image",


                                                };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboardActivity.this);
                                        builder.setTitle("Student Option:");

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                if (i == 0) {
                                                    Intent intent = new Intent(StudentDashboardActivity.this, ImageViewerActivity.class);
                                                    intent.putExtra("url",model.getImage());
                                                    startActivity(intent);

                                                }



                                            }
                                        });
                                        builder.show();
                                    }


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public DisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_dashboard_display, parent, false);
                        DisplayViewHolder holder = new DisplayViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

}