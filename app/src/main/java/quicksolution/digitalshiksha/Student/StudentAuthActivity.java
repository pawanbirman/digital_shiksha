package quicksolution.digitalshiksha.Student;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.shuhart.stepview.StepView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import quicksolution.digitalshiksha.MainActivity;
import quicksolution.digitalshiksha.R;


public class StudentAuthActivity extends AppCompatActivity {

    private int currentStep = 0;
    LinearLayout layout1,layout2,layout3,layout4;
    StepView stepView;
    AlertDialog dialog_verifying,profile_dialog;

    private static String uniqueIdentifier = null;
    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final long ONE_HOUR_MILLI = 60*60*1000;

    private static final String TAG = "FirebasePhoneNumAuth";

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;

    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton;
    private ProgressDialog loadingBar;
    public String currentUserID,saveCurrentTime, saveCurrentDate;
    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText;
    private String mVerificationId,onlineCustomerId,id="123";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private DatabaseReference studentDatabaseRef,RootRef;
    public static final String DEFAULT="NA";



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_auth);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog( this );
        RootRef = FirebaseDatabase.getInstance().getReference();
        sendCodeButton = (Button) findViewById(R.id.submit1);
        SharedPreferences sharedPreferences=getSharedPreferences("loginDetails",MODE_PRIVATE);
        id=sharedPreferences.getString("Id",DEFAULT);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            if (mAuth.getCurrentUser().getUid().equals(id))
            {
                loadingBar.setTitle("Logging.....");
                loadingBar.setMessage("Please wait, while we are getting your saved information...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                DatabaseReference studentDatabaseRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child("Student").child(id);

                studentDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if(dataSnapshot.exists())
                        {

                            if(dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status") )
                            {

                                startActivity(new Intent(StudentAuthActivity.this,StudentDashboardActivity.class));
                                finish();
                                loadingBar.dismiss();

                            }
                            else
                            {
                                loadingBar.dismiss();
                                Intent intent = new Intent(StudentAuthActivity.this, SettingsNewActivity.class);
                                intent.putExtra( "Type","Student" );
                                startActivity(intent);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }
        else
            {
                layout1 = (LinearLayout) findViewById(R.id.layout1);
                layout2 = (LinearLayout) findViewById(R.id.layout2);
                layout3 = (LinearLayout) findViewById(R.id.first);
                layout4 = (LinearLayout) findViewById(R.id.directlogin);
                layout4.setVisibility(View.GONE);
                verifyCodeButton = (Button) findViewById(R.id.submit2);
                firebaseAuth = FirebaseAuth.getInstance();
                phoneNum = (EditText) findViewById(R.id.phonenumber);
                verifyCodeET = (PinView) findViewById(R.id.pinView);
                phonenumberText = (TextView) findViewById(R.id.phonenumberText);
                stepView = findViewById(R.id.step_view);
                stepView.setStepsNumber(3);
                stepView.go(0, true);
                layout1.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);

                sendCodeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String phoneNumber ="+91"+phoneNum.getText().toString();
                        phonenumberText.setText(phoneNumber);

                        if (TextUtils.isEmpty(phoneNumber)) {
                            phoneNum.setError("Enter a Phone Number");
                            phoneNum.requestFocus();
                        } else if (phoneNumber.length() < 10) {
                            phoneNum.setError("Please enter a valid phone");
                            phoneNum.requestFocus();
                        } else {

                            if (currentStep < stepView.getStepCount() - 1) {
                                currentStep++;
                                stepView.go(currentStep, true);
                            } else {
                                stepView.done(true);
                            }
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.VISIBLE);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    StudentAuthActivity.this,               // Activity (for callback binding)
                                    mCallbacks);        // OnVerificationStateChangedCallbacks
                        }
                    }
                });

                mCallbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        LayoutInflater inflater = getLayoutInflater();
                        View alertLayout= inflater.inflate(R.layout.processing_dialog,null);
                        AlertDialog.Builder show = new AlertDialog.Builder(StudentAuthActivity.this);

                        show.setView(alertLayout);
                        show.setCancelable(false);
                        dialog_verifying = show.create();
                        dialog_verifying.show();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        Toast.makeText(StudentAuthActivity.this,"Please verify your Account with Valid Phone Number..",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(StudentAuthActivity.this,StudentAuthActivity.class));



                    }
                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;

                        // ...
                    }
                };

                verifyCodeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String verificationCode = verifyCodeET.getText().toString();
                        if(verificationCode.isEmpty()){
                            Toast.makeText(StudentAuthActivity.this,"Enter verification code",Toast.LENGTH_SHORT).show();
                        }else {

                            LayoutInflater inflater = getLayoutInflater();
                            View alertLayout= inflater.inflate(R.layout.processing_dialog,null);
                            AlertDialog.Builder show = new AlertDialog.Builder(StudentAuthActivity.this);

                            show.setView(alertLayout);
                            show.setCancelable(false);
                            dialog_verifying = show.create();
                            dialog_verifying.show();

                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                            signInWithPhoneAuthCredential(credential);

                        }
                    }
                });
            }

    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            dialog_verifying.dismiss();
                            if (currentStep < stepView.getStepCount() - 1) {
                                currentStep++;
                                stepView.go(currentStep, true);
                            } else {
                                stepView.done(true);
                            }
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);


                            AlertDialog.Builder show = new AlertDialog.Builder(StudentAuthActivity.this);
                            show.setCancelable(false);
                            profile_dialog = show.create();
                            profile_dialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(
                                    new Runnable() {
                                @Override
                                public void run()
                                {

                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    onlineCustomerId=currentUser.getUid();

                                    Log.d(TAG, " AuthActivity user id  " + onlineCustomerId);
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    DatabaseReference studentDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                            .child("Users").child("Student").child(onlineCustomerId);
                                    studentDatabaseRef.child("id").setValue(true);
                                    studentDatabaseRef.child("device_token")
                                            .setValue(deviceToken);
                                    SharedPreferences sharedPreferences=getSharedPreferences("loginDetails",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("Id",onlineCustomerId.toString());
                                    editor.commit();

                                    profile_dialog.dismiss();

                                    studentDatabaseRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {

                                            if(dataSnapshot.exists())
                                            {

                                                if(dataSnapshot.hasChild("name"))
                                                {
                                                    startActivity(new Intent(StudentAuthActivity.this,StudentDashboardActivity.class));
                                                    finish();

                                                }
                                                else
                                                {
                                                    Intent intent = new Intent(StudentAuthActivity.this, SettingsNewActivity.class);
                                                    intent.putExtra( "Type","Student" );
                                                    startActivity(intent);

                                                }

                                            }



                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError)
                                        {

                                            Toast.makeText(StudentAuthActivity.this,"Something wrong",Toast.LENGTH_SHORT).show();
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                            }


                                        }
                                    });

                                }
                            },1000);

                            // ...
                        } else {

                            dialog_verifying.dismiss();
                            Toast.makeText(StudentAuthActivity.this,"Please Enter correct OTP.. ",Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

}