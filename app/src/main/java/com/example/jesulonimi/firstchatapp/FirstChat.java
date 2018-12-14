package com.example.jesulonimi.firstchatapp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class FirstChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        FirebaseAuth myAuth=FirebaseAuth.getInstance();
        FirebaseUser user=myAuth.getCurrentUser();
        if(user!=null){
        final DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("User").
                child(myAuth.getCurrentUser().getUid());
        userRef.addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null) {
                            userRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }
}}
