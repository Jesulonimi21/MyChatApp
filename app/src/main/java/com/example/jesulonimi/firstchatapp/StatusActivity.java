package com.example.jesulonimi.firstchatapp;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
Toolbar mtoolbar;
EditText statusText;
Button setStatus;
FirebaseUser user;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
    user= FirebaseAuth.getInstance().getCurrentUser();
    String uID=user.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User").child(uID).child("status");


    mtoolbar=(Toolbar) findViewById(R.id.status_page_bar);
    setSupportActionBar(mtoolbar);
    getSupportActionBar().setTitle("STATUS");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    statusText=(EditText)findViewById(R.id.status_editText);
    setStatus=(Button) findViewById(R.id.setStatusButton);
    setStatus.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendStatus();
                }
            }
    );
    }
    public void sendStatus(){
        final String status=statusText.getText().toString();
        databaseReference.setValue(status).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
          if(task.isSuccessful()){
              Intent intent=new Intent(StatusActivity.this,AccountSettings.class);
              intent.putExtra("status",status);
              startActivity(intent);
          }else{
              Toast.makeText(StatusActivity.this,"failed",Toast.LENGTH_SHORT).show();
          }

                    }
                }
        );

    }



}
