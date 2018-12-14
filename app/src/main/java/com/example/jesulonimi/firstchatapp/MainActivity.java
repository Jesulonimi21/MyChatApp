package com.example.jesulonimi.firstchatapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mauth;
android.support.v7.widget.Toolbar mToolbar;
SectionPagerAdapter sectionPagerAdapter;
ViewPager viewPager;
TabLayout tabLayout;
private com.google.firebase.database.DatabaseReference checkOnline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mauth=FirebaseAuth.getInstance();

        mToolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lonimi's chat app");

        viewPager=(ViewPager) findViewById(R.id.view);
        tabLayout=(TabLayout) findViewById(R.id.tl);
        sectionPagerAdapter=new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    protected void onStop() {
        super.onStop();
/*        FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
        if(mUser!=null){
        checkOnline.child("online").setValue(ServerValue.TIMESTAMP);
    }*/}

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mauth.getCurrentUser();
        if(user==null){
            Intent intent=new Intent(MainActivity.this,StartActivity.class);
            startActivity(intent);
        finish();
        }else{
            String authString=FirebaseAuth.getInstance().getCurrentUser().getUid();
            checkOnline=com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child("User").child(authString);

            checkOnline.child("online").setValue("true");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,StartActivity.class));
        }
        if(item.getItemId()==R.id.as){
            startActivity(new Intent(MainActivity.this,AccountSettings.class));
        }
        if(item.getItemId()==R.id.allUsers){
            startActivity(new Intent(MainActivity.this,AllUsersActivity.class));
        }
        return true;
    }


}
