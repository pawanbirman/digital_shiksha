package quicksolution.digitalshiksha.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.SettingsNewActivity;

public class AdminAddServicesActivity extends AppCompatActivity {


    private String ServiceName, ProductDescription, ProductName, saveCurrentDate, saveCurrentTime,productRandomKey;
    private Button AddNewProductButton;
    private EditText InputProductName, InputProductDescription;
    private ImageView InputProductImage;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private StorageReference ProductImageRef;
    private String downloadImageUrl;
    private DatabaseReference ProductRef,ToppersRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_services);


        loadingBar = new ProgressDialog(this);

        ServiceName = getIntent().getExtras().get( "Service" ).toString();
        productRandomKey=getIntent().getStringExtra( "pid" );

        ProductImageRef= FirebaseStorage.getInstance().getReference().child( "Product Images" );
        ProductRef= FirebaseDatabase.getInstance().getReference().child( "Products" );
        ToppersRef=FirebaseDatabase.getInstance().getReference().child( "Toppers" );

        AddNewProductButton = (Button) findViewById( R.id.add_new_product );
        InputProductName = (EditText) findViewById( R.id.product_name );
        InputProductDescription = (EditText) findViewById( R.id.product_description );
        InputProductImage = (ImageView) findViewById( R.id.select_product_image );


        userInfoDisplay(InputProductImage, InputProductName, InputProductDescription);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat( "MM,dd,yyyy" );
        saveCurrentDate = currentDate.format( calendar.getTime() );

        SimpleDateFormat currentTime = new SimpleDateFormat( "HH:mm:ss" );
        saveCurrentTime = currentTime.format( calendar.getTime() );

        InputProductImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }
        } );

        AddNewProductButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();

            }
        } );


    }
    private void userInfoDisplay(final ImageView InputProductImage, final EditText InputProductName, final EditText InputProductDescription)
    {

        if(ServiceName.equals("Student Dashboard"))
        {
            ProductRef=ProductRef;
        }
        else if(ServiceName.equals("Toppers"))
            {
                ProductRef=ToppersRef;
            }
        ProductRef.child(productRandomKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {

                            if (dataSnapshot.child("image").exists())
                            {
                                String image = dataSnapshot.child("image").getValue().toString();
                                String Pname = dataSnapshot.child("pname").getValue().toString();
                                String Description = dataSnapshot.child("description").getValue().toString();

                                Picasso.get().load(image).into(InputProductImage);
                                InputProductName.setText(Pname);
                                InputProductDescription.setText(Description);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction( Intent.ACTION_GET_CONTENT );
        galleryIntent.setType( "image/*" );
        startActivityForResult( galleryIntent, GalleryPick );



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {


            ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .setAspectRatio(1, 1)
                    .start(AdminAddServicesActivity.this);
            InputProductImage.setImageURI( ImageUri );
        }
    }


    private void ValidateProductData() {
        ProductDescription = InputProductDescription.getText().toString();
        ProductName = InputProductName.getText().toString();

        if (ImageUri == null) {
            Toast.makeText( this, "Product Image is mandatory.. ", Toast.LENGTH_SHORT ).show();
        } else if (TextUtils.isEmpty( ProductDescription )) {
            Toast.makeText( this, "Please write product description..", Toast.LENGTH_SHORT ).show();
        } else if (TextUtils.isEmpty( ProductName )) {
            Toast.makeText( this, "Please write product Name..", Toast.LENGTH_SHORT ).show();
        } else {
            StoreProductInformation();
        }


    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("Dear Admin,Please wait, while we are adding the new product ...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final StorageReference filePath=ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask=filePath.putFile(ImageUri  );

        uploadTask.addOnFailureListener( new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText( AdminAddServicesActivity.this, "Error: " +message , Toast.LENGTH_SHORT ).show();
                loadingBar.dismiss();

            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText( AdminAddServicesActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT ).show();
                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task <Uri>>()
                {
                    @Override
                    public Task <Uri> then(@NonNull Task <UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task <Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText( AdminAddServicesActivity.this, "Got Image Url successfully", Toast.LENGTH_SHORT ).show();
                            saveProduuctInfoToDatabase();
                        }

                    }
                } );


            }
        } );



    }

    private void saveProduuctInfoToDatabase()
    {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put( "pid",productRandomKey );
        productMap.put( "date",saveCurrentDate );
        productMap.put( "time",saveCurrentTime );
        productMap.put( "description",ProductDescription);
        productMap.put( "image",downloadImageUrl );
        productMap.put( "Service",ServiceName );
        productMap.put( "pname",ProductName );

        if(ServiceName.equals("Toppers"))
        {
            ToppersRef.child( productRandomKey ).updateChildren( productMap )
                    .addOnCompleteListener( new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task <Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent(AdminAddServicesActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);
                                loadingBar.dismiss();
                                Toast.makeText( AdminAddServicesActivity.this, "Product is added successfully..", Toast.LENGTH_LONG ).show();


                            }
                            else
                            {
                                loadingBar.dismiss();
                                String message=task.getException().toString();
                                Toast.makeText( AdminAddServicesActivity.this, "Error: " +message  , Toast.LENGTH_SHORT ).show();
                            }

                        }
                    } );


        }
        else  if(ServiceName.equals("Student Dashboard"))
        {

            ProductRef.child(productRandomKey).updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AdminAddServicesActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);
                                loadingBar.dismiss();
                                Toast.makeText(AdminAddServicesActivity.this, "Product is added successfully..", Toast.LENGTH_LONG).show();


                            } else {
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(AdminAddServicesActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }



    }
}

