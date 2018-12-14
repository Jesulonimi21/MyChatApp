package com.example.jesulonimi.firstchatapp;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    String brainStrangersImageUrl;
    String strangersImageUrl;
    String brainImage;
    Context c;
List<messages> messagesList;
FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public MessageAdapter(List<messages> messagesList, Context con) {
       this.messagesList=messagesList;
       c=con;
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_format,parent,false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
messages m=messagesList.get(position);

strangersImageUrl=m.getFrom();

if(m.getType().equals("text")) {
    holder.message_text.setText(m.getMessage());




String myId=mAuth.getCurrentUser().getUid();

String from_user=m.getFrom();
if(from_user!=null){
if(from_user.equals(myId)){

    holder.message_text.setBackgroundResource(R.drawable.my_text_shape);
    holder.message_text.setTextColor(Color.BLACK);
    //holder.rl.setGravity(Gravity.LEFT);
}else{
    holder.message_text.setBackgroundResource(R.drawable.text_shape);
    holder.message_text.setTextColor(Color.WHITE);

}}
}else{
    Toast.makeText(c,"reached here",Toast.LENGTH_LONG).show();
    Picasso.with(c).load(m.getMessage()).into(holder.messageImage);
}
        String myId=mAuth.getCurrentUser().getUid();
        String from_user=m.getFrom();
if(from_user!=null){
ChatActivity Ch=new ChatActivity();
if(from_user.equals(myId)) {
    Picasso.with(c).load(getMyImage()).networkPolicy(NetworkPolicy.OFFLINE).fit().into(holder.message_imv_view, new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {
Picasso.with((c)).load(getMyImage()).placeholder(R.drawable.user_avatar_good).fit().into(holder.message_imv_view);
        }
    });
}else{
    Picasso.with(c).load(getStrangersImage()).networkPolicy(NetworkPolicy.OFFLINE).fit().into(holder.message_imv_view, new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {
            Picasso.with((c)).load(getStrangersImage()).fit().into(holder.message_imv_view);
        }
    });
}}



    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
TextView message_text;
CircleImageView message_imv_view;
ImageView messageImage;
//RelativeLayout rl;

        public MessageViewHolder(View itemView) {
            super(itemView);
          //  rl=itemView.findViewById(R.id.message_layout);
            message_text=(TextView) itemView.findViewById(R.id.message_text);
            message_imv_view=(CircleImageView) itemView.findViewById(R.id.message_image);
            messageImage=(ImageView) itemView.findViewById(R.id.ImageMessage);

        }
    }

  public String getMyImage(){
      final String[] img = new String[1];
        String myId=mAuth.getCurrentUser().getUid();
      DatabaseReference dbr= FirebaseDatabase.getInstance().getReference().child("User").child(myId);
      dbr.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         String image=dataSnapshot.child("THumb_image").getValue().toString();

        brainImage=image;
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
      return brainImage;

  }

  public String getStrangersImage(){
   DatabaseReference dbr= FirebaseDatabase.getInstance().getReference().child("User").child(strangersImageUrl);
      dbr.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         String image=dataSnapshot.child("THumb_image").getValue().toString();

        brainStrangersImageUrl=image;
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
      return brainStrangersImageUrl;

    }
}
