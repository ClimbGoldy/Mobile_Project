package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView images_gridView;
    // Firebase
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mWorkDataRef = mRootRef.child("workData").child("paint");
    StorageReference mStorageRootRef = FirebaseStorage.getInstance().getReference();
    StorageReference mStoragePathRef = mStorageRootRef.child("work/paint/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        // Views
        images_gridView = (ListView) findViewById(R.id.gallery_images_gridView);
        // Buttons
        findViewById(R.id.gallery_profile_imageView).setOnClickListener(this);
        findViewById(R.id.gallery_work_imageView).setOnClickListener(this);
        findViewById(R.id.gallery_rank_imageView).setOnClickListener(this);
        findViewById(R.id.gallery_setting_imageView).setOnClickListener(this);
        final ListViewGalleryAdapter adapter = new ListViewGalleryAdapter();
        mWorkDataRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        final long ONE_MEGABYTE = 1024 * 1024;
                        try{
                            final String title = dataSnapshot.child("title").getValue().toString();
                            final String artist = dataSnapshot.child("artist").getValue().toString();
                            int like = Integer.parseInt(dataSnapshot.child("like").getValue().toString());
                            mStoragePathRef.child(dataSnapshot.getKey() + ".png").getBytes(ONE_MEGABYTE)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            Drawable d = new BitmapDrawable(bitmap);
                                            adapter.addItem(d, title, artist);
                                            images_gridView.setAdapter(adapter);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }catch (Exception e){
                        }
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
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery_profile_imageView:
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                finish();
                break;
            case R.id.gallery_work_imageView:
                startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
                finish();
                break;
            case R.id.gallery_rank_imageView:
                break;
            case R.id.gallery_setting_imageView:
                break;
        }
    }
}
/*
ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mPainter.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //바이트 어레이로 담음
        byte[] data = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        mStoragePathRef.

        final long ONE_MEGABYTE = 1024 * 1024;
        mStoragePathRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                adapter.add();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
 */