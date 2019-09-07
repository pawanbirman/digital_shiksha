package quicksolution.digitalshiksha.Student;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shuhart.stepview.StepView;

import java.util.concurrent.TimeUnit;

import quicksolution.digitalshiksha.MainActivity;
import quicksolution.digitalshiksha.R;


public class StudentAuthActivity extends AppCompatActivity {

    private int currentStep = 0;
    LinearLayout layout1,layout2;
    StepView stepView;
    AlertDialog dialog_verifying,profile_dialog;

    private static String uniqueIdentifier = null;
    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final long ONE_HOUR_MILLI = 60*60*1000;

    private static final String TAG = "FirebasePhoneNumAuth";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;

    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton;

    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText;

    private String mVerificationId,onlineCustomerId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    private FirebaseAuth mAuth;

    private DatabaseReference studentDatabaseRef;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(StudentAuthActivity.this, StudentDashboardActivity.class));
            finish();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_auth);

        mAuth = FirebaseAuth.getInstance();


        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        sendCodeButton = (Button) findViewById(R.id.submit1);
        verifyCodeButton = (Button) findViewById(R.id.submit2);
        firebaseAuth = FirebaseAuth.getInstance();
        phoneNum = (EditText) findViewById(R.id.phonenumber);
        verifyCodeET = (PinView) findViewById(R.id.pinView);
        phonenumberText = (TextView) findViewById(R.id.phonenumberText);


        stepView = findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);

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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run()
                                {


                                    onlineCustomerId=mAuth.getCurrentUser().getUid();
                                    studentDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                            .child("Users").child("Students").child(onlineCustomerId);
                                    studentDatabaseRef.child("id").setValue(true);
                                    profile_dialog.dismiss();
                                    startActivity(new Intent(StudentAuthActivity.this,StudentDashboardActivity.class));
                                    finish();
                                }
                            },1000);

                            // ...
                        } else {

                            dialog_verifying.dismiss();
                            Toast.makeText(StudentAuthActivity.this,"Something wrong",Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }


}