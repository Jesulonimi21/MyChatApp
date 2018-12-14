package com.example.jesulonimi.firstchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
Button notRegistered;
Button haveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        notRegistered=(Button) findViewById(R.id.notRegistered);
        haveAccount=(Button) findViewById(R.id.haveAccount);

        haveAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(StartActivity.this,LogInActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
        );


        notRegistered.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(StartActivity.this,RegisterActivity.class));
                        finish();
                    }
                }
        );
    }
}
