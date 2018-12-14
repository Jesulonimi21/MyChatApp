package com.example.jesulonimi.firstchatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
List<friends> frList;
    RecyclerView friendsRecycler;
    String theId=FirebaseAuth.getInstance().getCurrentUser().getUid();
DatabaseReference dbr=FirebaseDatabase.getInstance().getReference().child("friends").child(theId);

DatabaseReference dbrFriend=FirebaseDatabase.getInstance().getReference().child("User");
FirebaseRecyclerOptions<friends> options=new FirebaseRecyclerOptions.Builder<friends>()
        .setQuery(dbr,friends.class).build();

String brainThumb;


    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_friends, container, false);
        friendsRecycler=(RecyclerView) v.findViewById(R.id.friendsRecycler);
        friendsRecycler.setHasFixedSize(true);
        friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        dbr.keepSynced(true);
        dbrFriend.keepSynced(true);
        return v;

    }

    FirebaseRecyclerAdapter friendsRecyclerAdapter=new FirebaseRecyclerAdapter<friends,myFriendsViewHolder>(options){


        @Override
        protected void onBindViewHolder(@NonNull final myFriendsViewHolder holder, int position, @NonNull friends model) {
                    holder.setDate(model.getDate());
                  final  String ref=getRef(position).getKey();
                    dbrFriend.child(ref).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String userName=dataSnapshot.child("name").getValue().toString();
                                    final String thumb_imageUrl=dataSnapshot.child("THumb_image").getValue().toString();
                                    brainThumb=thumb_imageUrl;
                                    holder.setThumbImage(getContext(),thumb_imageUrl);
                                    holder.setName(userName);
                                    if(dataSnapshot.hasChild("online")){
                                    String online_stats= dataSnapshot.child("online").getValue().toString();
                                    holder.setOnline_Status(online_stats);

                                    holder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
                                            builder.setTitle("select option");
                                            CharSequence[] option=new CharSequence[]{"go to profile","send message"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                         if(which==0){
                                             Intent i=new Intent(getContext(),ProfileActivity.class);
                                             i.putExtra("theId",ref);
                                             startActivity(i);
                                         }
                                         if(which==1){
                                             Intent i=new Intent(getContext(),ChatActivity.class);
                                             i.putExtra("uName",userName);
                                             i.putExtra("thumb_i",thumb_imageUrl);
                                             i.putExtra("chatId",ref);
                                             startActivity(i);
                                         }
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                }}

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );


        }

        @NonNull
        @Override
        public myFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.format,parent,false);
            return new myFriendsViewHolder(v);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        friendsRecycler.setAdapter(friendsRecyclerAdapter);
        friendsRecyclerAdapter.startListening();
    }

    public static class myFriendsViewHolder extends  RecyclerView.ViewHolder{
View mView;
        public myFriendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDate(String str){
            TextView t=(TextView) mView.findViewById(R.id.userStatus);
            t.setText(str);
        }

        public void setThumbImage(final Context c, final String url){
            final CircleImageView civ=mView.findViewById(R.id.userImage);
            Picasso.with(c).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(civ, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
Picasso.with(c).load(url).into(civ);
                }
            });
            }
            public void setName(String n){
            TextView nV=mView.findViewById(R.id.userName);
            nV.setText(n);
            }

            public void setOnline_Status(String online_status){
                ImageView onlineicon=mView.findViewById(R.id.onlineicon);
                if(online_status.equals("true")){
                    onlineicon.setVisibility(View.VISIBLE);
                    }else{onlineicon.setVisibility(View.INVISIBLE);}
            }
    }


}
