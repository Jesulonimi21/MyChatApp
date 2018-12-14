package com.example.jesulonimi.firstchatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    Toolbar chatToolbar;
    CircleImageView civ;
    TextView lastSeenTextView;
    TextView chat_nameTextView;
    DatabaseReference rootRef;
    private SwipeRefreshLayout swipeRefreshLayout;
   private String brainHisId;
    private  String myId= FirebaseAuth.getInstance().getCurrentUser().getUid();
  private  ImageButton plus;
    private ImageButton send;
    private EditText editText_chat;
    private static final int Total_items_to_load=10;
    private  int currentPage=1;

    private StorageReference imageReference;
    private String prevKey;
    private String mLastKey;
    private int itemPos=0;
    String imUrl;
    List<messages> messageList;
    MessageAdapter mAdapter;
    RecyclerView rView;
    LinearLayoutManager LLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String userName=getIntent().getStringExtra("uName");
     imUrl=getIntent().getStringExtra("thumb_i");
        String hisId=getIntent().getStringExtra("chatId");
brainHisId=hisId;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);

        send=(ImageButton) findViewById(R.id.send_chat);
        plus=(ImageButton) findViewById(R.id.plus_chat);
        editText_chat=(EditText) findViewById(R.id.editText_chat);




        chatToolbar=(Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar=getSupportActionBar();
     actionBar.setDisplayHomeAsUpEnabled(true);
     actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.custom_action_bar,null);
        actionBar.setCustomView(action_bar_view);

      civ =(CircleImageView) action_bar_view.findViewById(R.id.chatIcon);
      lastSeenTextView=(TextView) action_bar_view.findViewById(R.id.online_stats);
      chat_nameTextView=(TextView) action_bar_view.findViewById(R.id.chatName);

      swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
LLM=new LinearLayoutManager(ChatActivity.this);
        rView=(RecyclerView)findViewById(R.id.recycler_message);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(LLM);
        messageList=new ArrayList<>();
        mAdapter=new MessageAdapter(messageList,ChatActivity.this);
        rView.setAdapter(mAdapter);
        loadMessage();

        imageReference= FirebaseStorage.getInstance().getReference();

      rootRef.child("User").child(hisId).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String timestats=dataSnapshot.child("online").getValue().toString();
              final String imageurl=dataSnapshot.child("THumb_image").getValue().toString();
              String chattersName=dataSnapshot.child("name").getValue().toString();
          //    Toast.makeText(ChatActivity.this,timestats,Toast.LENGTH_LONG).show();
              chat_nameTextView.setText(chattersName);
              Picasso.with(ChatActivity.this).load(imageurl).networkPolicy(NetworkPolicy.OFFLINE)
              .fit().into(civ, new Callback() {
                  @Override
                  public void onSuccess() {

                  }

                  @Override
                  public void onError() {
Picasso.with(ChatActivity.this).load(imageurl).fit().into(civ);
                  }
              });
         if(timestats.equals("true")){
             lastSeenTextView.setText("online");
         }else {
             GetTimeAgo ATimeAgo=new GetTimeAgo();
             long lastTime=Long.parseLong(timestats);
             String lastSeenTime= ATimeAgo.getTimeAgo(lastTime,getApplicationContext());
            lastSeenTextView.setText(lastSeenTime);
         }

          }


          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });



//-------------------putting values in the chat activity---------------------//
        rootRef.child("chats").child(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(brainHisId)){
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timeStamp", ServerValue.TIMESTAMP);

                    Map chatUserMap=new HashMap();
                    chatUserMap.put("chat/"+myId+"/"+brainHisId,chatAddMap);
                    chatUserMap.put("chat/"+brainHisId+"/"+myId,chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        }
                    })  ;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


      //------setting thre message button clicked---------------------------//
      send.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              sendMessage();
          }
      });

      swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
              currentPage++;
              itemPos=0;
              loadMoreMessages();
          }
      });

    }

    public void sendMessage(){
        String message=editText_chat.getText().toString();
        if(!TextUtils.isEmpty(message)){

            DatabaseReference dbrPush=rootRef.child("messsages").child(myId).child(brainHisId).push();
            String push_id=dbrPush.getKey();
            Map messageMap=new HashMap();

            String my_mesage_ref="messages"+"/"+myId+"/"+brainHisId;
            String his_message_ref="messages"+"/"+brainHisId+"/"+myId;
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",myId);

           Map MessageUserMap=new HashMap();
           MessageUserMap.put(my_mesage_ref+"/"+push_id,messageMap);
           MessageUserMap.put(his_message_ref+"/"+push_id,messageMap);

           mAdapter.notifyDataSetChanged();
           editText_chat.setText("");

           rootRef.updateChildren(MessageUserMap, new DatabaseReference.CompletionListener() {
               @Override
               public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

               }
           });

           }
        }

        public void loadMoreMessages(){
            DatabaseReference inRef=rootRef.child("messages").child(myId).child(brainHisId);
     Query sQuery=inRef.orderByKey().endAt(mLastKey).limitToLast(10);
     sQuery.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
             messages message=dataSnapshot.getValue(messages.class);
             String messageKey=dataSnapshot.getKey();
             if(!prevKey.equals(messageKey)) {
                 messageList.add(itemPos++, message);
             }else{prevKey=mLastKey;}

             if(itemPos==1){
                 mLastKey=dataSnapshot.getKey();
             }


             mAdapter.notifyDataSetChanged();
             swipeRefreshLayout.setRefreshing(false);
             LLM.scrollToPositionWithOffset(10,0);
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });


        }

        public void loadMessage(){
        DatabaseReference inRef=rootRef.child("messages").child(myId).child(brainHisId);
        inRef.keepSynced(true);
        Query dbQ=inRef.limitToLast( currentPage*Total_items_to_load);
        dbQ.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                itemPos++;
                if(itemPos==1){
                String messageKey=dataSnapshot.getKey();
                mLastKey=messageKey;
                   prevKey=messageKey;
                }
                messages message=dataSnapshot.getValue(messages.class);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();
              rView.scrollToPosition(messageList.size()-1);
              swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        }
        public void SendPicture(View v){
        Intent galleryIntent=new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"select image"),1);
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&data.getData()!=null&&resultCode==RESULT_OK){
            Uri ImageUri=data.getData();
            final String myRef="messages/"+myId+"/"+brainHisId;
            final String hisRef="messages/"+brainHisId+"/"+myId;

            DatabaseReference user_message_push=rootRef.child("message").child(myId).child(brainHisId).push();
            final  String push_id=user_message_push.getKey();
            final StorageReference filePath=imageReference.child("message-image").child(push_id+".jpg");
            filePath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                  String imageDownload=uri.toString();
                       Map messageMap=new HashMap();
                       messageMap.put("message",imageDownload);
                       messageMap.put("seen",false);
                       messageMap.put("type","image");
                       messageMap.put("time",ServerValue.TIMESTAMP);
                       messageMap.put("from",myId);

                       Map MessageUserMap=new HashMap();
                       MessageUserMap.put(myRef+"/"+push_id,messageMap);
                       MessageUserMap.put(hisRef+"/"+push_id,messageMap);

                       mAdapter.notifyDataSetChanged();
                       editText_chat.setText("");

                       rootRef.updateChildren(MessageUserMap, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                           }
                       });




                   }
               });
                }
            });
        }
    }
}
