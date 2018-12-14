package com.example.jesulonimi.firstchatapp;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    /*FirebaseRecyclerOptions<User> options =
            new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(mdatabase, User.class)
                    .build();*/



    private RecyclerView rcv;
    DatabaseReference userDbr=FirebaseDatabase.getInstance().getReference().child("User");
String myId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference req_databaseObj= FirebaseDatabase.getInstance().getReference().child("friend_req").child(myId);
    FirebaseRecyclerOptions<friend_req> options=new FirebaseRecyclerOptions.Builder<friend_req>()
            .setQuery(req_databaseObj,friend_req.class).build();



    public RequestFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v= inflater.inflate(R.layout.fragment_request, container, false);
        rcv=(RecyclerView) v.findViewById(R.id.friend_reqRecycler);
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

            userDbr.keepSynced(true);
            req_databaseObj.keepSynced(true);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        rcv.setAdapter(firebaseRecyclerAdapter);
    }

    FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<friend_req,ReqViewHolder>(options){


        @Override
        protected void onBindViewHolder(@NonNull final ReqViewHolder holder, int position, @NonNull friend_req model) {
         /*   holder.setName(model.getName());
            holder.setPicture(AllUsersActivity.this,model.getTHumb_image());
            holder.setStatus(model.getStatus());*/

            final  String usersId=getRef(position).getKey();
            userDbr.child(usersId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child("name").getValue().toString();
                    String thumbImage=dataSnapshot.child("THumb_image").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    holder.setPicture(getContext(),thumbImage);
                    holder.setName(name);
                    holder.setStatus(status);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.myView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(getContext(),ProfileActivity.class);
                            i.putExtra("theId",usersId);
                            startActivity(i);
                        }
                    }
            );
        }



        @NonNull
        @Override
        public ReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.format,parent,false);
            return new ReqViewHolder(v);
        }
    };





public class ReqViewHolder extends RecyclerView.ViewHolder{
    View myView;
    public ReqViewHolder(View itemView) {
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
