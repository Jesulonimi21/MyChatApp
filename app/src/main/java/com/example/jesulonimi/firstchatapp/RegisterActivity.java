package com.example.jesulonimi.firstchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
Button createUser;
EditText dpName;
EditText email;
EditText pass;
Toolbar mtoolbar;
FirebaseAuth mauth;
DatabaseReference databaseReference;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("registering user");
        progressDialog.setMessage("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        mtoolbar=(Toolbar) findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("create account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dpName=(EditText) findViewById(R.id.dpName_reg);
        email=(EditText) findViewById(R.id.emName_reg);
        pass=(EditText) findViewById(R.id.pass_reg);
        mauth=FirebaseAuth.getInstance();
        createUser =(Button) findViewById(R.id.createUser);
         createUser.setOnClickListener(

                 new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         progressDialog.show();
                         registerUser();
                     }
                 }
         );
    }


    public void registerUser() {

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String em = email.getText().toString();
     final   String dp = dpName.getText().toString();
        String pa = pass.getText().toString();





        mauth.createUserWithEmailAndPassword(em, pa).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String user_id = user.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user_id);


                            String UID=mauth.getCurrentUser().getUid().toString();

                            databaseReference= FirebaseDatabase.getInstance().getReference().child("User").child(UID);

                            String deviceToken= FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", dp);
                            hashMap.put("status", "this is me using Lonimis chat app");
                            hashMap.put("THumb_image", "default");
                            hashMap.put("image","this is my image");
                            hashMap.put("device token",deviceToken);
                            databaseReference.setValue(hashMap);

                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();

                         }
                         else{
                            progressDialog.hide();
                        }
                    }
                }


        );
    }
}
