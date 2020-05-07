package quicksolution.digitalshiksha.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import io.paperdb.Paper;
import quicksolution.digitalshiksha.Driver.DriverMapActivity;
import quicksolution.digitalshiksha.Driver.DriverSettingActivity;
import quicksolution.digitalshiksha.Prevalent.Prevalent;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.SettingsNewActivity;
import quicksolution.digitalshiksha.Student.StudentAuthActivity;
import quicksolution.digitalshiksha.Student.StudentDashboardActivity;

public class AdminDriverLoginActivity extends AppCompatActivity {

    private Button Login;
    private EditText Email,Password;
    private ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    private com.rey.material.widget.CheckBox chkBoxRememberMe;

    private String onlineDriverId;

    private DatabaseReference driverDatabaseRef;
    private static final String TAG = "FirebasePhoneNumAuth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_driver_login);

        mAuth=FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        Login=(Button)findViewById(R.id.login_btn);
        Email=(EditText)findViewById(R.id.login_email_input);
        Password=(EditText)findViewById(R.id.login_password_input);





        chkBoxRememberMe=(com.rey.material.widget.CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        final String email = Paper.book().read( Prevalent.UserEmail );
        final String password = Paper.book().read( Prevalent.UserPassword );
        if (email != "" && password != "") {
            if (!TextUtils.isEmpty( email ) && !TextUtils.isEmpty( password ))
            {

                loadingBar.setTitle("Logging :");
                loadingBar.setMessage("Please wait,While we are  processing on your data...");
                loadingBar.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {


                            Toast.makeText(AdminDriverLoginActivity.this, "Sign In , Successful...", Toast.LENGTH_SHORT).show();

                            if(email.equals("digitalshiksha1994@gmail.com") && password.equals("123456"))
                            {
                                loadingBar.dismiss();

                                Intent intent = new Intent(AdminDriverLoginActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);


                            }

                            else if(email !="digitalshiksha1994@gmail.com" && password !="123456")
                                {
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(AdminDriverLoginActivity.this, DriverMapActivity.class);
                                    startActivity(intent);
                                }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(AdminDriverLoginActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        }




        Login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String email = Email.getText().toString();
                final String password = Password.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(AdminDriverLoginActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(AdminDriverLoginActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    loadingBar.setTitle("Logging :");
                    loadingBar.setMessage("Please wait,While we are  processing on your data...");
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                if(chkBoxRememberMe.isChecked())
                                {
                                    Paper.book().write(Prevalent.UserEmail,email);
                                    Paper.book().write(Prevalent.UserPassword,password);

                                }



                                onlineDriverId=mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                DatabaseReference driverDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Driver").child(onlineDriverId);
                                driverDatabaseRef.child("id").setValue(true);
                                driverDatabaseRef.child("device_token")
                                        .setValue(deviceToken);

                                driverDatabaseRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {

                                        if(dataSnapshot.exists())
                                        {

                                            if(dataSnapshot.hasChild("name"))
                                            {

                                                if(email.equals("digitalshiksha1994@gmail.com") && password.equals("123456"))
                                                {
                                                    loadingBar.dismiss();

                                                    Intent intent = new Intent(AdminDriverLoginActivity.this, AdminDashboardActivity.class);
                                                    startActivity(intent);


                                                }

                                                else
                                                {
                                                    loadingBar.dismiss();
                                                    Intent intent = new Intent(AdminDriverLoginActivity.this, DriverMapActivity.class);
                                                    startActivity(intent);
                                                }

                                            }
                                            else
                                            {
                                                loadingBar.dismiss();
                                                Intent intent = new Intent(AdminDriverLoginActivity.this, DriverSettingActivity.class);
                                                startActivity(intent);

                                            }

                                        }



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                        loadingBar.dismiss();

                                        Toast.makeText(AdminDriverLoginActivity.this,"Something wrong",Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                        }


                                    }
                                });

                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(AdminDriverLoginActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }



}
