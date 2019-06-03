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

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mCategoryTextView;
    ListView mJoinListView;
    RelativeLayout mCreateLayout;

    EditText mRoomName;
    EditText mRoomTheme;
    Spinner mPlayerNumberSpinner;

    String mCategory;
    int maxPlayer;
    String mUid;
    // FireBase
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRoomDataRef = mRootRef.child("roomData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);
        // Views
        mCategoryTextView = (TextView) findViewById(R.id.category_textView);
        mJoinListView = (ListView) findViewById(R.id.roomList_join_listView);
        mCreateLayout = (RelativeLayout) findViewById(R.id.roomList_create_layout);

        mRoomName = (EditText) findViewById(R.id.roomList_roomName_editText);
        mRoomTheme = (EditText) findViewById(R.id.roomList_theme_editText);
        mPlayerNumberSpinner = (Spinner) findViewById(R.id.roomList_playerNumber_spinner);
        // Buttons
        findViewById(R.id.roomList_back_button).setOnClickListener(this);
        findViewById(R.id.roomList_join_button).setOnClickListener(this);
        findViewById(R.id.roomList_create_button).setOnClickListener(this);
        findViewById(R.id.roomList_conform_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");
        if (mCategory.equalsIgnoreCase("paint")) {
            mCategoryTextView.setText("Paint");
        } else {
            mCategoryTextView.setText("Text");
        }
        mRoomDataRef = mRoomDataRef.child(mCategory);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<RoomInform> roomList = new ArrayList<RoomInform>();
        mRoomDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RoomInform roomInform = new RoomInform();
                try {
                    roomInform.setRoomName(dataSnapshot.child("roomName").getValue().toString());
                    roomInform.setRoomTheme(dataSnapshot.child("roomTheme").getValue().toString());
                    roomInform.setPlayerNumber(Integer.parseInt(dataSnapshot.child("maxPlayer").getValue().toString()));
                    roomInform.setRoomRef(dataSnapshot.getKey());
                    for (int i = 0; i < roomInform.getPlayerNumber(); i++) {
                        roomInform.isEmpty.add(dataSnapshot.child("userList").child("userID_" + i).getValue().equals("empty"));
                    }
                } catch (Exception e) {

                }
                roomList.add(roomInform);
                adapter.add(roomInform.getRoomName());
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
        mJoinListView.setAdapter(adapter);

        mJoinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for (int i = 0; i < roomList.get(position).getPlayerNumber(); i++) {
                    if (roomList.get(position).isEmpty.get(i)) {
                        mRoomDataRef.child(roomList.get(position).getRoomRef()).child("userList").child("userID_" + i).setValue(mUid);
                        Intent intent = new Intent(getApplicationContext(), WaitingRoomActivity.class);
                        intent.putExtra("roomRef", roomList.get(position).getRoomRef());
                        intent.putExtra("category", mCategory);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        mPlayerNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                maxPlayer = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.roomList_back_button:
                finish();
                break;
            case R.id.roomList_join_button:
                mJoinListView.setVisibility(View.VISIBLE);
                mCreateLayout.setVisibility(View.GONE);
                break;
            case R.id.roomList_create_button:
                mJoinListView.setVisibility(View.GONE);
                mCreateLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.roomList_conform_button:
                DatabaseReference newRoomRef = mRoomDataRef.push();
                newRoomRef.child("roomName").setValue(mRoomName.getText().toString());
                newRoomRef.child("roomTheme").setValue(mRoomTheme.getText().toString());
                newRoomRef.child("maxPlayer").setValue(maxPlayer);
                newRoomRef.child("isStarted").setValue(false);
                newRoomRef.child("turn").setValue(-1);
                newRoomRef.child("remainTime").setValue(-1);
                for (int i = 0; i < maxPlayer; i++) {
                    if (i == 0)
                        newRoomRef.child("userList").child("userID_" + i).setValue(mUid);
                    else
                        newRoomRef.child("userList").child("userID_" + i).setValue("empty");
                }
                intent = new Intent(getApplicationContext(), WaitingRoomActivity.class);
                intent.putExtra("roomRef", newRoomRef.getKey());
                intent.putExtra("category", mCategory);
                startActivity(intent);
                finish();
                break;
        }
    }
}