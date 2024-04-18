package com.example.hospinfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {


    EditText username,emailid,password;
    Button signupwithgoogle, submit;
    TextView redirectologinpage;
    LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Change this line to reference the correct layout file
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
        else {
            submit = findViewById(R.id.signup);
            username = findViewById(R.id.signupusername);
            emailid = findViewById(R.id.signupemail);
            password = findViewById(R.id.signuppassword);
            signupwithgoogle = findViewById(R.id.signupgoogle);
            redirectologinpage = findViewById(R.id.redirecttosignin);

            redirectologinpage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userText = username.getText().toString().trim();
                    String emailText = emailid.getText().toString().trim();
                    String passwordText = password.getText().toString().trim();
                    if (userText.isEmpty()) {
                        username.setError("Username should be provided");
                        username.requestFocus();
                        return;
                    }

                    if (emailText.isEmpty()) {
                        emailid.setError("Email should be provided");
                        emailid.requestFocus();
                        return;
                    }

                    if (passwordText.isEmpty()) {
                        password.setError("Password should be provided");
                        password.requestFocus();
                        return;
                    }
                    loadingDialog.startLoadingDialog();
                    firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    loadingDialog.dismissDialog();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "created an account", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        if (user != null) {
                                            updateUI(user);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Account exists!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }

    private void updateUI(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please verify your email address.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendEmailVerification();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sending verification email...");
            progressDialog.setCancelable(false); // Prevent dismiss by tapping outside of the dialog
            progressDialog.show();

            user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        checkEmailVerificationStatus();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "email already verified", Toast.LENGTH_SHORT).show();
                        redirectToAnotherPage();
                    }
                }
            });
        }
    }
    private void checkEmailVerificationStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("verification process...");
            progressDialog.setCancelable(false); // Prevent dismiss by tapping outside of the dialog
            progressDialog.show();
            if (user.isEmailVerified()) {
                progressDialog.dismiss();
                redirectToAnotherPage();
            } else {
                Toast.makeText(getApplicationContext(),"Verification is not done",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void redirectToAnotherPage() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }
}