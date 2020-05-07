package quicksolution.digitalshiksha.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import quicksolution.digitalshiksha.Admin.AdminAddServicesActivity;
import quicksolution.digitalshiksha.Model.Products;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.ViewHolder.DisplayViewHolder;
import quicksolution.digitalshiksha.ViewHolder.DisplayViewHolderToppers;

public class MeetWithToppersActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;


    private DatabaseReference ToppersRef, GetUserDetails;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String saveCurrentDate = "123", saveCurrentTime = "123", productRandomKey, id = "123";
    private String getType = "";
    public static final String DEFAULT = "NA";
    public static final String TAG = "ChatWithStaff";
    private Toolbar SettingsToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_with_toppers);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Be Like Toppers");
        if (bundle != null) {
            getType = getIntent().getStringExtra("Type");
        }

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy  ");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH : mm :ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        ToppersRef = FirebaseDatabase.getInstance().getReference().child("Toppers");




        recyclerView = findViewById(R.id.recycler_menu_toppers);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ToppersRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, DisplayViewHolderToppers> adapter=new
                FirebaseRecyclerAdapter<Products, DisplayViewHolderToppers>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DisplayViewHolderToppers holder, int position, @NonNull final Products model)
            {

                holder.txtToppersName.setText(model.getPname());
                holder.txtToppersDescription.setText(model.getDescription());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.imageViewToppers);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getType.equals("admin")) {
                            CharSequence options[] = new CharSequence[]
                                    {

                                            "Edit Existing Idea",
                                            "Delete Existing Idea",


                                    };

                            AlertDialog.Builder builder = new AlertDialog.Builder(MeetWithToppersActivity.this);
                            builder.setTitle("Admin Options:");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    if (i == 0) {
                                        Intent intent = new Intent(MeetWithToppersActivity.this, AdminAddServicesActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        intent.putExtra("Service", model.getService());
                                        startActivity(intent);

                                    }

                                    if (i == 1) {
                                        ToppersRef
                                                .child(model.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(MeetWithToppersActivity.this, "Deleted..", Toast.LENGTH_SHORT).show();

                                                        }

                                                    }
                                                });
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
            public DisplayViewHolderToppers onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meet_with_toppers_layout, viewGroup, false);
                DisplayViewHolderToppers holder = new DisplayViewHolderToppers(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
}
