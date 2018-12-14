package com.example.jesulonimi.firstchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class
AccountSettings extends AppCompatActivity {
    Bitmap bitmap;
    TextView dpText;
    TextView statusText;
    CircleImageView dpPicture;
    Button cImage;
    Button cStatus;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    StorageReference storageReference;
    String ImageUrl;
    String uId;
    ProgressDialog progressDialog;
    public static final int SpecialCode = 101;

        DatabaseReference dbrOnline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        storageReference = FirebaseStorage.getInstance().getReference();
        dpText = (TextView) findViewById(R.id.dpName_accountSettings);
        statusText = (TextView) findViewById(R.id.status_accountSettings);
        dpPicture = (CircleImageView) findViewById(R.id.dpImage_accountSettings);
        cImage = (Button) findViewById(R.id.changeImage_AccountSettings);
        cStatus = (Button) findViewById(R.id.ChangeStatus_AccountSettings);

        Intent i = getIntent();
        if (i != null) {
            String stats = i.getStringExtra("status");
            statusText.setText(stats);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(uId);
        databaseReference.keepSynced(true);
        cImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                }
        );


        cStatus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(AccountSettings.this, StatusActivity.class));
                    }
                }
        );

        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();
                        dpText.setText(name);
                        statusText.setText(status);
                        if (!image.equals("default")) {
                            //Picasso.with(AccountSettings.this).load(image).placeholder(R.drawable.user_avatar_good).fit().into(dpPicture);
                            Picasso.with(AccountSettings.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                                    placeholder(R.drawable.user_avatar_good).fit().into(dpPicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(AccountSettings.this).load(image).
                                            placeholder(R.drawable.user_avatar_good).fit().into(dpPicture);


                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "select image"), SpecialCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SpecialCode && resultCode == RESULT_OK && data != null & data.getData() != null) {
            Uri uri = data.getData();
            CropImage.activity(uri).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("uploading image");
                progressDialog.setMessage("please wait");
                progressDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75).compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference file = storageReference.child("ProfileImage").child(uId + ".jpg");
                final StorageReference Thumb_FilePath = storageReference.child("ProfileImage").child("Bitmap bitmap").child(uId + ".jpg");
                file.putFile(resultUri)
                        .addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        file.getDownloadUrl().addOnSuccessListener(
                                                new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        final Uri download = uri;
                                                        UploadTask uploadTask = Thumb_FilePath.putBytes(thumb_byte);
                                                        uploadTask.addOnCompleteListener(
                                                                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> Thumb_task) {

                                                                        if (Thumb_task.isSuccessful()) {
                                                                            Thumb_FilePath.getDownloadUrl().addOnSuccessListener(
                                                                                    new OnSuccessListener<Uri>() {
                                                                                        @Override
                                                                                        public void onSuccess(Uri uri) {
                                                                                            final Uri ThumbUri = uri;
                                                                                            Map userMap = new HashMap();
                                                                                            userMap.put("image", download.toString());
                                                                                            userMap.put("THumb_image", ThumbUri.toString());
                                                                                            Toast.makeText(AccountSettings.this, "upload successfull", Toast.LENGTH_LONG).show();
                                                                                            databaseReference.updateChildren(userMap);
                                                                                            progressDialog.dismiss();
                                                                                        }
                                                                                    }
                                                                            );


                                                                        }
                                                                    }
                                                                }
                                                        );

                                                    }
                                                }
                                        );


                                    }
                                }
                        );
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                Exception error = result.getError();

            }

        }
    }

}




