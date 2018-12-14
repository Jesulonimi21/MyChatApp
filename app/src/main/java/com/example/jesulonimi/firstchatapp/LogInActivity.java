package com.example.jesulonimi.firstchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LogInActivity extends AppCompatActivity {
EditText pass_logIn;
EditText email_logIn;
Button signIn;
FirebaseAuth mauth;
ProgressDialog  progressDialog;
android.support.v7.widget.Toolbar mtoolbar;
DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mauth=FirebaseAuth.getInstance();
        pass_logIn=(EditText) findViewById(R.id.pass_logiIn);
        email_logIn=(EditText) findViewById(R.id.email_logIn);
        signIn=(Button) findViewById(R.id.signIn);
        mtoolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.login_page_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("LOG IN");
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("registering user");
        progressDialog.setMessage("please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        signIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        logInUser();
                    }
                }
        ); }
        public void logInUser(){

        String email=email_logIn.getText().toString();
        String pass=pass_logIn.getText().toString();
        if(email.isEmpty()){
            email_logIn.setError("email required");
            email_logIn.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            pass_logIn.setError("password required");
            pass_logIn.requestFocus();
            return;
        }
            progressDialog.show();
        mauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String UID=mauth.getCurrentUser().getUid().toString();

                            dbRef= FirebaseDatabase.getInstance().getReference().child("User").child(UID);


                        String tokenId= FirebaseInstanceId.getInstance().getToken();
                        dbRef.child("device token").setValue(tokenId).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Intent intent=new Intent(LogInActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                        );


                        }else{
                            progressDialog.hide();
                        }
                    }
                }
        );



        }

}
