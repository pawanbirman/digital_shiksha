package quicksolution.digitalshiksha.Student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import quicksolution.digitalshiksha.R;

public class SettingsNewActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, statusEditText;
    private Button update;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private String getType="";
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    final static int Gallery_Pick = 1;
    private Toolbar SettingsToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings_new );


        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        getType=getIntent().getStringExtra( "Type" );

        mAuth = FirebaseAuth.getInstance();

        ref  = FirebaseDatabase.getInstance().getReference().child("Users").child(getType);


        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        statusEditText = (EditText) findViewById(R.id.settings_profile_status);

        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);

        update = (Button) findViewById(R.id.settings_update);


        userInfoDisplay(profileImageView, fullNameEditText,statusEditText);
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
                        .start(SettingsNewActivity.this);
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

        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            fullNameEditText.setError("Please enter your Name...");
            fullNameEditText.requestFocus();
        }
        else if (TextUtils.isEmpty(statusEditText.getText().toString()))
        {
            statusEditText.setError("Please enter a cool status...");
            statusEditText.requestFocus();
        }
        else
            {
                HashMap<String, Object> userMap = new HashMap<>();
                userMap. put("name", fullNameEditText.getText().toString());
                userMap. put("status", statusEditText.getText().toString());

                ref.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                startActivity(new Intent(SettingsNewActivity.this,StudentDashboardActivity.class));
                Toast.makeText(SettingsNewActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                finish();

            }


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
                    Toast.makeText(SettingsNewActivity.this, "Error: \n" + "The image has not been cropped well. Try again.", Toast.LENGTH_SHORT).show();

                }

        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsNewActivity.this, SettingsNewActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            fullNameEditText.setError("Please enter your Name...");
            fullNameEditText.requestFocus();
        }
        else if (TextUtils.isEmpty(statusEditText.getText().toString()))
        {
            statusEditText.setError("Please enter a cool status...");
            statusEditText.requestFocus();
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
            String data =imageUri.toString();

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
                            userMap. put("status", statusEditText.getText().toString());
                            userMap. put("image", myUrl);
                            ref.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                            progressDialog.dismiss();
                            startActivity(new Intent(SettingsNewActivity.this, StudentDashboardActivity.class));
                            Toast.makeText(SettingsNewActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                            finish();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsNewActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeData(File myFile, String data)
    {

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText statusEditText)
    {

        ref.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>3)
                {

                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String Status = dataSnapshot.child("status").getValue().toString();


                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        statusEditText.setText(Status);

                    }
                    else
                        {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String Status = dataSnapshot.child("status").getValue().toString();

                            fullNameEditText.setText(name);
                            statusEditText.setText(Status);


                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
