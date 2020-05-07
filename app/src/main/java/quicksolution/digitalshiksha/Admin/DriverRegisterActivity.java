package quicksolution.digitalshiksha.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.StudentDashboardActivity;

public class DriverRegisterActivity extends AppCompatActivity {
    private EditText DriverEmail,DriverPassword;
    private Button regButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    String currentUserId;
    private DatabaseReference driversDatabaseRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        mAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        loadingBar = new ProgressDialog(this);


        DriverEmail=(EditText)findViewById(R.id.reg_phone_number_input);
        DriverPassword=(EditText)findViewById(R.id.reg_password_input);
        regButton=(Button)findViewById(R.id.reg_btn);


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String Email=DriverEmail.getText().toString();
                String Password=DriverPassword.getText().toString();

                Register(Email,Password);

            }
        });
    }


    private void Register(String email, String password)
    {

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write Email id...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write password...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Registering..");
            loadingBar.setMessage("While BUS is processing on your data...");
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                currentUserId = mAuth.getCurrentUser().getUid();
                                driversDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(currentUserId);
                                driversDatabaseRef.setValue(true);

                                Intent intent = new Intent(DriverRegisterActivity.this, AdminDriverLoginActivity.class);
                                Paper.book().destroy();
                                startActivity(intent);
                                loadingBar.dismiss();
                                Toast.makeText(DriverRegisterActivity.this, "Registration Successful..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(DriverRegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });

        }


    }
}
