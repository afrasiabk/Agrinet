package alast.hm.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import alast.hm.R;
import cdflynn.android.library.checkview.CheckView;

import static alast.hm.Activities.HomeActivity.login_data;

public class PhoneVerifyActivity extends AppCompatActivity {

    private static final String TAG = PhoneVerifyActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private EditText phone_input, code_input, name_input;
    private TextView textView, login_btn, resend_btn;
    private CheckView checkView;
    private ImageView iconView;
    private String codeSent;
    private boolean from_cart = false;
    private String authPhoneNumber;
    private boolean registered = false;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        backBtn();
        fromIntent();
        assignViews();
        listeners();
    }

    private void fromIntent() {
        if (getIntent().hasExtra("from")){
            if (getIntent().getStringExtra("from").equals("cart")){
                from_cart = true;
            }
        }
    }

    private void assignViews() {
        progressBar = (ProgressBar) findViewById(R.id.phone_verify_pb);
        login_btn = (TextView) findViewById(R.id.phone_login_btn);
        textView = (TextView) findViewById(R.id.phone_verify_text);
        phone_input = (EditText) findViewById(R.id.phone_verify_phn);
        code_input = (EditText) findViewById(R.id.phone_verify_code);
        name_input = (EditText) findViewById(R.id.phone_verify_name);
        checkView = (CheckView) findViewById(R.id.phone_verify_check);
        iconView = (ImageView) findViewById(R.id.phone_verify_icon);
        resend_btn = (TextView) findViewById(R.id.pv_resend);
        Picasso.get().load(R.drawable.logo_icon).fit().centerCrop().into((ImageView) findViewById(R.id.phone_verify_icon));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phone_input.setAutofillHints(View.AUTOFILL_HINT_PHONE);
            name_input.setAutofillHints(View.AUTOFILL_HINT_NAME);
        }
    }

    private void listeners() {
            screens("phone"); //first time
        if (from_cart){
            textView.setText("Please verify your phone number to proceed");
        }
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (login_btn.getText().equals("Login")){
                        checkNumber();
                    }
                    else if (login_btn.getText().equals("Verify")){
                        verifySignInCode();
                    }
                    else{
                        register();
                    }
                }
            });
            resend_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    regenerateCode();
                }
            });
    }

    //calls generate code
    private void checkNumber(){

        String phone = phone_input.getText().toString();

        if(phone.isEmpty()){
            phone_input.setError("Phone number is required");
            phone_input.requestFocus();
            return;
        }

        if ((phone.length() < 10 )){
            phone_input.setError("Please enter a valid phone");
            phone_input.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Checking Credentials");

        if (phone.substring(0,3).equals("+92")) {
            authPhoneNumber = phone;
            generateCode();
        }
        else if (phone.substring(0,1).equals("3")) {
            authPhoneNumber = "+92"+phone;
            generateCode();
        }
        else{
            authPhoneNumber = "+92" + phone.substring(1, 11);
            generateCode();
        }
    }

    //sets callbacks
    private void generateCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                authPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    //sets callbacks
    private void regenerateCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                authPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,
                resendToken);        // OnVerificationStateChangedCallbacks
    }

    //auto verification ->calls auth_sign_in
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            screens("code");
            codeSent = s;
            resendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            auth_sign_in(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            screens("phone");

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                textView.setText("Invalid Phone Number");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                textView.setText("verification attempts exceeded\ntry again later");
            }
            else{
                textView.setText(e.getMessage());
            }                Log.i(TAG, "fb_error"+e.getMessage());
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            if (login_btn.getText().equals("Verify")) {
                textView.setText("Timeout\nEnter code manually.");
                progressBar.setVisibility(View.INVISIBLE);
            }
            super.onCodeAutoRetrievalTimeOut(s);
        }
    };

    //manual verification ->calls auth_sign_in
    private void verifySignInCode(){
        if(code_input.getText().toString().isEmpty()){
            code_input.setError("Code is required");
            code_input.requestFocus();
            return;
        }
        textView.setText("Verifying code");
        progressBar.setVisibility(View.VISIBLE);
        auth_sign_in(PhoneAuthProvider.getCredential(codeSent, code_input.getText().toString()));
    }

    //auth signed in if code credential in correct ->calls check_register
    private void auth_sign_in(final PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkView.setVisibility(View.VISIBLE);
                            iconView.setVisibility(View.INVISIBLE);
                            checkView.check();
                            textView.setText("Phone Verified");
                            check_register();
                        }
                        else{
                            textView.setText("Incorrect code");
                            code_input.getText().clear();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }

    //calls register() or login()
    private void check_register() {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference("Agrinet/Users");
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(authPhoneNumber).exists()){
                    login();
                }
                else{
                    screens("name");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PhoneVerifyActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //calls login()
    private void register() {
        final String name = name_input.getText().toString();
        if (TextUtils.isEmpty(name)){
            name_input.setError("Name is required");
            name_input.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Creating Account");

        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("name",name);
        userdataMap.put("phone",authPhoneNumber);
        userdataMap.put("token", FirebaseInstanceId.getInstance().getToken());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agrinet/Users");

        reference.child(authPhoneNumber).updateChildren(userdataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            textView.setText("Complete");
                            progressBar.setVisibility(View.INVISIBLE);
                            login();
                        }
                        else {
                            Toast.makeText(PhoneVerifyActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //sets registered = true , loads data in home and hence prevalent,  intents!
    private void login() {
        registered = true;
        progressBar.setVisibility(View.INVISIBLE);
        login_data();
        Toast.makeText(PhoneVerifyActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        if (from_cart){
            Intent intent = new Intent(PhoneVerifyActivity.this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(PhoneVerifyActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void screens(String type){
        if (type.equals("phone")){
            progressBar.setVisibility(View.INVISIBLE);
            login_btn.setText("Login");
            textView.setText("login with phone number");
            phone_input.setVisibility(View.VISIBLE);
            code_input.setVisibility(View.INVISIBLE);
            name_input.setVisibility(View.INVISIBLE);
            resend_btn.setVisibility(View.INVISIBLE);
        }
        else if (type.equals("code")){
            progressBar.setVisibility(View.VISIBLE);
            login_btn.setText("Verify");
            textView.setText("Automatic Verification in Progress\nYou  may enter the code manually");
            phone_input.setVisibility(View.INVISIBLE);
            code_input.setVisibility(View.VISIBLE);
            name_input.setVisibility(View.INVISIBLE);
            resend_btn.setVisibility(View.VISIBLE);
        }
        else if (type.equals("name")){
            progressBar.setVisibility(View.INVISIBLE);
            login_btn.setText("Create Account");
            textView.setText("Phone number verified.\nPlease enter your name to complete registration");
            phone_input.setVisibility(View.INVISIBLE);
            code_input.setVisibility(View.INVISIBLE);
            name_input.setVisibility(View.VISIBLE);
            resend_btn.setVisibility(View.INVISIBLE);
            if (getSupportActionBar()!=null) {
                getSupportActionBar().setTitle("Register");
            }


        }
        else{
            if (getSupportActionBar()!=null) {
                getSupportActionBar().setTitle("Login");
            }
            screens("phone");
            authPhoneNumber = "";
            code_input.getText().clear();
            codeSent = "";
        }
    }

    @Override
    public void onBackPressed() {
        if (!login_btn.getText().equals("Login")){
            checkView.setVisibility(View.GONE);
            iconView.setVisibility(View.VISIBLE);
            screens("fresh");
            return;
        }
        super.onBackPressed();
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    protected void onStop() {
        if (!registered){
            FirebaseAuth.getInstance().signOut();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (!registered){
            FirebaseAuth.getInstance().signOut();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
