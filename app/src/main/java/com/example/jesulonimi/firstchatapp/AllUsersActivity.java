package com.example.jesulonimi.firstchatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String uId=user.getUid();

    DatabaseReference mdatabase= FirebaseDatabase.getInstance().getReference().child("User");



EditText et_search;

    Toolbar toolbar;
RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        toolbar=(Toolbar) findViewById(R.id.UsersActivity_page_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
et_search=(EditText) findViewById(R.id.searchText);
                mdatabase.keepSynced(true);
    recyclerView=(RecyclerView)findViewById(R.id.Recycle_usersActivity);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }
    public void searchImage(View v) {
        String searchText=et_search.getText().toString();

        Query myDbQuery=mdatabase.orderByChild("name").startAt(searchText).endAt(searchText+"\uf8ff");


        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(myDbQuery, User.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull User model) {
                holder.setName(model.getName());
                holder.setPicture(AllUsersActivity.this, model.getTHumb_image());
                holder.setStatus(model.getStatus());
                final String usersId = getRef(position).getKey();
                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        i.putExtra("theId", usersId);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.format, parent, false);
                return new myViewHolder(v);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    public static class myViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public myViewHolder(View itemView) {
            super(itemView);
            myView=itemView;
        }

        public void setName(String stats){
            TextView stv=myView.findViewById(R.id.userName);
            stv.setText(stats);
        }
        public void setPicture(final Context c, final String url){
            final CircleImageView imv=myView.findViewById(R.id.userImage);

           // Picasso.with(c).load(url).placeholder(R.drawable.user_avatar_good).fit().into(imv);
            Picasso.with(c).load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_avatar_good).
                    fit().into(imv, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(c).load(url).placeholder(R.drawable.user_avatar_good).fit().into(imv);
                }
            });
        }

        public void setStatus(String nm){
            TextView ftv=myView.findViewById(R.id.userStatus);
            ftv.setText(nm);
        }
    }



}
