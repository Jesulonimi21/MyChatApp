package com.example.jesulonimi.firstchatapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    String myId;
TextView pName,pWStatus,pFriendsNo;
ImageView pImage;
Button request;
DatabaseReference myReference;
DatabaseReference requestReference;
DatabaseReference friendDatabase;
Button declineRequest;
DatabaseReference notificationDatabase;
DatabaseReference rootRef;
    String current_state="not friends";
    String brainNoteRef;
    String brainCurrent_uid;
    private DatabaseReference checkOnline;
    String brainPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        pName=(TextView)findViewById(R.id.profileName);
        pWStatus=(TextView)findViewById(R.id.jobStatus);
        pFriendsNo=(TextView)findViewById(R.id.friendsNo);
        final String current_uId=getIntent().getStringExtra("theId");
        brainCurrent_uid=current_uId;
        notificationDatabase=FirebaseDatabase.getInstance().getReference().child("notification");

        rootRef=FirebaseDatabase.getInstance().getReference();

        declineRequest=(Button) findViewById(R.id.declineRequest);
        declineRequest.setVisibility(View.INVISIBLE);
        declineRequest.setEnabled(false);
        String authString=FirebaseAuth.getInstance().getCurrentUser().getUid();

checkOnline=FirebaseDatabase.getInstance().getReference().child("User").child(authString);
        friendDatabase=FirebaseDatabase.getInstance().getReference().child("friends");

        pImage=(ImageView)findViewById(R.id.profileImage);
        request=(Button)findViewById(R.id.requestbutton);


        FirebaseAuth mauth=FirebaseAuth.getInstance();
        FirebaseUser me=mauth.getCurrentUser();
        myId=me.getUid();


        requestReference=FirebaseDatabase.getInstance().getReference().child("friend_req");

        myReference= FirebaseDatabase.getInstance().getReference().child("User").child(current_uId);
        myReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String pN=dataSnapshot.child("name").getValue().toString();
                        String pS=dataSnapshot.child("status").getValue().toString();
                        final String pI=dataSnapshot.child("image").getValue().toString();
                        pName.setText(pN);
                        pWStatus.setText(pS);
                        Picasso.with(ProfileActivity.this).load(pI).networkPolicy(NetworkPolicy.OFFLINE).fit().into(pImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                        Picasso.with(ProfileActivity.this).load(pI).fit().into(pImage);
                            }
                        });

                        requestReference.child(myId).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(current_uId)){
                                            String acc=dataSnapshot.child(current_uId).child("request_type").getValue().toString();
                                            Toast.makeText(ProfileActivity.this,acc,Toast.LENGTH_LONG).show();
                                            if(acc.equals("received")){
                                                current_state="request recieved";
                                                request.setText("accept friend request");

                                                declineRequest.setEnabled(true);
                                                declineRequest.setVisibility(View.VISIBLE);
                                                }else if(acc.equals("sent")){
                                                current_state="request_sent";
                                                request.setText("cancel friend request");
                                                declineRequest.setVisibility(View.INVISIBLE);
                                                declineRequest.setEnabled(false);

                                            }}else{
                                                friendDatabase.child(myId).addListenerForSingleValueEvent(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(current_uId)){
                                                                    request.setText("unfriend");
                                                                    current_state="friends";

                                                                    declineRequest.setVisibility(View.INVISIBLE);
                                                                    declineRequest.setEnabled(false);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        }
                                                );

                                            }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        request.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        request.setEnabled(false);
                        if(current_state.equals("not friends")){
                            DatabaseReference noteRef=rootRef.child("notification").child(current_uId).push();
                            String amNote=noteRef.getKey();
                            brainNoteRef=amNote;
                            HashMap<String,String> input=new HashMap<String,String>();
                            input.put("from",myId);
                            input.put("type","request");


                          Map requestMap=new HashMap();
                          requestMap.put("friend_req"+"/"+myId+"/"+current_uId+"/"+"request_type","sent");
                          requestMap.put("friend_req"+"/"+current_uId+"/"+myId+"/"+"request_type","received");
                          requestMap.put("notification"+"/"+current_uId+"/"+amNote,input);
                          rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                              @Override
                              public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                  request.setEnabled(true);
                                  current_state="request_sent";
                                  request.setText("cancel friend request");

                                  }
                          });

                        }

                    if(current_state.equals("request_sent")){
                            request.setEnabled(false);
                    Map reqRec=new HashMap();
                    reqRec.put("friend_req"+"/"+myId+"/"+current_uId,null);
                    reqRec.put("friend_req"+"/"+current_uId+"/"+myId,null);
                    reqRec.put("notification"+"/"+current_uId+"/"+brainNoteRef,null);

                    rootRef.updateChildren(reqRec, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            request.setText("send friend request");
                            current_state="not friends";
                            request.setEnabled(true);
                        }
                    });

                    }
            if(current_state.equals("request recieved")){
        final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

        friendDatabase.child(myId).child(current_uId).child("date").setValue(currentDate).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
         friendDatabase.child(current_uId).child(myId).child("date").setValue(currentDate).addOnSuccessListener(
                 new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {

                         requestReference.child(myId).child(current_uId).removeValue().addOnSuccessListener(
                                 new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {




                                         declineRequest.setEnabled(false);
                                         declineRequest.setVisibility(View.INVISIBLE);

                                         requestReference.child(current_uId).child(myId).removeValue();
                                         request.setEnabled(true);
                                         request.setText("unfriend");
                                         current_state="friends";
                                     }});

                     }
                 }
         );
                    }
                }
        );
        }


        // unfriend feature------------------------------------------------------------------------


                        if(current_state=="friends"){
                            Map unfriendMap=new HashMap();
                            unfriendMap.put("friends"+"/"+myId,null);
                            unfriendMap.put("friends"+"/"+current_uId,null);

                        rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            }
                        });
                        }
                    }
                }
        );

    }
    public void DeclineRequest(View v){
        Map reqRec=new HashMap();
        reqRec.put("friend_req"+"/"+myId+"/"+brainCurrent_uid,null);
        reqRec.put("friend_req"+"/"+brainCurrent_uid+"/"+myId,null);
        reqRec.put("notification"+"/"+brainCurrent_uid+"/"+brainNoteRef,null);

        rootRef.updateChildren(reqRec, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                request.setText("send friend request");
                current_state="not friends";
                request.setEnabled(true);
                declineRequest.setVisibility(View.INVISIBLE);

    }
});

        }

    /* protected void onStop() {
        super.onStop();
        checkOnline.child("online").setValue(ServerValue.TIMESTAMP);
    }*/


     /*@Override
   protected void onStart() {
        super.onStart();
        checkOnline.child("online").setValue("true");
    }*/
}
