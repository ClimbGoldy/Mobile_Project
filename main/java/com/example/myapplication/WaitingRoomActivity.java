package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mRoomName;
    TextView mRoomTheme;
    TextView mPlayerNumber;
    ListView mPlayerList;

    int maxPlayer;
    boolean isRoomOwner;
    String mCategory;
    String mUid;
    // FireBase
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRoomDataRef = mRootRef.child("roomData");
    DatabaseReference mRoomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitingroom);
        // Views
        mRoomName = (TextView) findViewById(R.id.waitingRoom_roomName_textView);
        mRoomTheme = (TextView) findViewById(R.id.waitingRoom_roomTheme_textView);
        mPlayerNumber = (TextView) findViewById(R.id.waitingRoom_roomPlayerNumber_textView);
        mPlayerList = (ListView) findViewById(R.id.waitingRoom_roomPlayerList_listView);
        // Buttons
        findViewById(R.id.waitingRoom_back_button).setOnClickListener(this);
        findViewById(R.id.waitingRoom_start_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();

        Intent intent_from_pre = getIntent();
        mCategory = intent_from_pre.getStringExtra("category");
        mRoomDataRef = mRoomDataRef.child(mCategory);
        mRoomRef = mRoomDataRef.child(intent_from_pre.getStringExtra("roomRef"));

        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    mRoomName.setText(dataSnapshot.child("roomName").getValue().toString());
                    mRoomTheme.setText(dataSnapshot.child("roomTheme").getValue().toString());
                    mPlayerNumber.setText(dataSnapshot.child("maxPlayer").getValue().toString());

                    if (dataSnapshot.child("userList").child("userID_0").getValue().equals(mUid)) {
                        isRoomOwner = true;
                        findViewById(R.id.waitingRoom_start_button).setVisibility(View.VISIBLE);
                    } else {
                        isRoomOwner = false;
                        findViewById(R.id.waitingRoom_start_button).setVisibility(View.GONE);
                    }

                    if(isRoomOwner==false){
                        if (dataSnapshot.child("isStarted").getValue().toString().equals(true)) {
                            Intent intent;
                            if (mCategory.equalsIgnoreCase("paint")) {
                                intent = new Intent(getApplicationContext(), PaintActivity.class);
                                mRoomRef.child("isStarted").setValue(true);
                                intent.putExtra("roomRef", mRoomRef.getKey());
                                startActivity(intent);
                                finish();
                            } else {
                                //TODO
                                /*
                                intent = new Intent(getApplicationContext(), PaintActivity.class);
                                intent.putExtra("roomRef", mRoomRef.getKey());
                                startActivity(intent);
                                finish();
                                */
                            }
                        }
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mRoomRef.child("userList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.add(dataSnapshot.getValue().toString());
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
        mPlayerList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.waitingRoom_back_button:
                finish();
                break;
            case R.id.waitingRoom_start_button:
                if (mCategory.equalsIgnoreCase("paint")) {
                    intent = new Intent(getApplicationContext(), PaintActivity.class);
                    mRoomRef.child("isStarted").setValue(true);
                    mRoomRef.child("turn").setValue(0);
                    mRoomRef.child("remainTime").setValue(10);

                    intent.putExtra("roomRef", mRoomRef.getKey());
                    startActivity(intent);
                    finish();
                } else {
                    //TODO
                    intent = new Intent(getApplicationContext(), PaintActivity.class);

                    intent.putExtra("roomRef", mRoomRef.getKey());
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
}