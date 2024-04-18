package com.example.hospinfo;

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

public class LoginActivity extends AppCompatActivity {

    Button signinwithgoogle,signinwithemail;
    EditText email,password;
    TextView redirectosignup;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        email = findViewById(R.id.signinemail);
        signinwithemail = findViewById(R.id.signin);
        password = findViewById(R.id.signinpassword);
        signinwithgoogle = findViewById(R.id.signingoogle);
        redirectosignup = findViewById(R.id.redirectsignup);
        auth=FirebaseAuth.getInstance();

        redirectosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        signinwithemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailid = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                auth.signInWithEmailAndPassword(emailid,pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"No User Found!!! Create account",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}