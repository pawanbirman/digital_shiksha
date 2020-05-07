package quicksolution.digitalshiksha.Driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.SettingsNewActivity;
import quicksolution.digitalshiksha.Student.StudentDashboardActivity;

public class DriverSettingActivity extends AppCompatActivity {


    private CircleImageView profileImageView;
    private EditText fullNameEditText, phoneEditText,busNameEditText,statusDriver;
    private Button update;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private String getType="";
    private DatabaseReference ref,chatRef;
    private FirebaseAuth mAuth;
    final static int Gallery_Pick = 1;
    private Toolbar SettingsToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);

        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        mAuth = FirebaseAuth.getInstance();

        ref  = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver");
        chatRef  = FirebaseDatabase.getInstance().getReference().child("Users").child("Student");


        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures Driver");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image_driver);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name_driver);
        phoneEditText = (EditText) findViewById(R.id.settings_phoneNumber_driver);
        busNameEditText = (EditText) findViewById(R.id.settings_carName_driver);
        statusDriver = (EditText) findViewById(R.id.settings_profile_status_driver);

        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn_driver);

        update = (Button) findViewById(R.id.settings_update);


        userInfoDisplay(profileImageView, fullNameEditText,phoneEditText,busNameEditText,statusDriver);
        closeKeyboard();


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }



            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(DriverSettingActivity.this);
            }
        });


    }

    private void closeKeyboard()
    {
        View view=this.getCurrentFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    private void updateOnlyUserInfo()
    {

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("phone", phoneEditText.getText().toString());
        userMap. put("bus", busNameEditText.getText().toString());
        userMap. put("status", statusDriver.getText().toString());

        ref.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
        chatRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

        startActivity(new Intent(DriverSettingActivity.this, DriverMapActivity.class));
        Toast.makeText(DriverSettingActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE )
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                imageUri = result.getUri();

                profileImageView.setImageURI(imageUri);
            }
            else
            {
                Toast.makeText(DriverSettingActivity.this, "Error: \n" + "The image has not been cropped well. Try again.", Toast.LENGTH_SHORT).show();

            }

        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(DriverSettingActivity.this, DriverSettingActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "phone number is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(busNameEditText.getText().toString()))
        {
            Toast.makeText(this, "bus name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(statusDriver.getText().toString()))
        {
            Toast.makeText(this, "status is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(mAuth.getCurrentUser().getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();




                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap. put("name", fullNameEditText.getText().toString());
                        userMap. put("phone", phoneEditText.getText().toString());
                        userMap. put("bus", busNameEditText.getText().toString());
                        userMap. put("status", statusDriver.getText().toString());
                        userMap. put("image", myUrl);
                        ref.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        chatRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(DriverSettingActivity.this, DriverMapActivity.class));
                        Toast.makeText(DriverSettingActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(DriverSettingActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText phoneEditText,final EditText busNameEditText,final EditText statusDriver)
    {

        ref.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>2)
                {

                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String Phone = dataSnapshot.child("phone").getValue().toString();
                        String Bus = dataSnapshot.child("bus").getValue().toString();
                        String Status = dataSnapshot.child("status").getValue().toString();


                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        phoneEditText.setText(Phone);
                        busNameEditText.setText(Bus);
                        statusDriver.setText(Status);

                    }
                    else
                    {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String Phone = dataSnapshot.child("phone").getValue().toString();
                        String Bus = dataSnapshot.child("bus").getValue().toString();
                        String Status = dataSnapshot.child("status").getValue().toString();

                        fullNameEditText.setText(name);
                        phoneEditText.setText(Phone);
                        busNameEditText.setText(Bus);
                        statusDriver.setText(Status);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
