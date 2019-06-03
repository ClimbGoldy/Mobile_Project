package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mUserName;
    TextView mUserPoint;
    TextView mUserRank;

    String mUid;
    // Firebase
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserDataRef = mRootRef.child("userData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        // Views
        mUserName = findViewById(R.id.mainMenu_userName_textView);
        mUserPoint = findViewById(R.id.mainMenu_userPoint_textView);
        mUserRank = findViewById(R.id.mainMenu_userRank_textView);
        // Buttons
        findViewById(R.id.doPaint_button).setOnClickListener(this);
        findViewById(R.id.doText_button).setOnClickListener(this);
        findViewById(R.id.mainMenu_profile_imageView).setOnClickListener(this);
        findViewById(R.id.mainMenu_work_imageView).setOnClickListener(this);
        findViewById(R.id.mainMenu_rank_imageView).setOnClickListener(this);
        findViewById(R.id.mainMenu_setting_imageView).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();
        mUserDataRef.child(mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserName.setText(dataSnapshot.child("userName").getValue().toString());
                mUserPoint.setText(dataSnapshot.child("point").getValue().toString());
                mUserRank.setText(dataSnapshot.child("rank").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.doPaint_button:
                intent = new Intent(getApplicationContext(), RoomListActivity.class);
                intent.putExtra("category", "paint");
                startActivity(intent);
                break;
            case R.id.doText_button:
                intent = new Intent(getApplicationContext(), RoomListActivity.class);
                intent.putExtra("category", "text");
                startActivity(intent);
                break;
            case R.id.mainMenu_profile_imageView:
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                finish();
                break;
            case R.id.mainMenu_work_imageView:
                startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
                finish();
                break;
            case R.id.mainMenu_rank_imageView:
                break;
            case R.id.mainMenu_setting_imageView:
                break;

        }
    }
}