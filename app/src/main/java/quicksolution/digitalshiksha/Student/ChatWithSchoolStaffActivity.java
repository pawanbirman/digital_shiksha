package quicksolution.digitalshiksha.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.onAppKilled;

public class ChatWithSchoolStaffActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener
        ,GroupFragment.OnFragmentInteractionListener

{

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabAccessorAdapter;
    private DatabaseReference RootRef;
    public FirebaseAuth mAuth;
    public String currentUserID,saveCurrentTime, saveCurrentDate;
    public static final String DEFAULT="NA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_school_staff);



        mToolbar=(Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Quality Chat");
        startService(new Intent(ChatWithSchoolStaffActivity.this, onAppKilled.class));


        SharedPreferences sharedPreferences=getSharedPreferences("loginDetails",MODE_PRIVATE);
        currentUserID=sharedPreferences.getString("Id",DEFAULT);



        myViewPager=(ViewPager)findViewById(R.id.chat_tab_pager);
        myTabAccessorAdapter=new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout=(TabLayout)findViewById(R.id.chat_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        RootRef = FirebaseDatabase.getInstance().getReference();

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.chat_menu_find_friends)
        {

            Intent findFriendsIntent = new Intent(ChatWithSchoolStaffActivity.this, FindFriendsActivity.class);
            startActivity(findFriendsIntent);
        }

        if(item.getItemId() == R.id.chat_menu_settings)
        {

            Intent intent = new Intent(ChatWithSchoolStaffActivity.this, SettingsNewActivity.class);
            intent.putExtra( "Type","Student" );
            startActivity(intent);

        }
        if (item.getItemId() == R.id.chat_menu_create_group)
        {
            RequestNewGroup();
        }

        return true;

    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithSchoolStaffActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(ChatWithSchoolStaffActivity.this);
        groupNameField.setHint("e.g school friend");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(ChatWithSchoolStaffActivity.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Chat").child("Group").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ChatWithSchoolStaffActivity.this, groupName + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


}

