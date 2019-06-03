package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener {
    int testInt = 0;
    DrawingPaperActivity mPainter;
    TextView mRoomName;
    TextView mRemainTime;
    private ProgressBar progressBar;

    boolean didIDraw = false;
    boolean didInit = false;
    int remainTime_int;
    int turn;
    int maxPlayer;

    ArrayList<String> playerList = new ArrayList<String>();
    String mUid;
    // Firebase
    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRoomDataRef = mRootRef.child("roomData").child("paint");
    DatabaseReference mRoomRef;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        mPainter = (DrawingPaperActivity) findViewById(R.id.canvas);
        mRoomName = (TextView) findViewById(R.id.paint_roomName_textView);
        mRemainTime = (TextView) findViewById(R.id.remainTime_textView);
        progressBar = (ProgressBar) findViewById(R.id.paint_upload_progressBar);
        //buttons for color
        findViewById(R.id.black_button).setOnClickListener(this);
        findViewById(R.id.white_button).setOnClickListener(this);
        findViewById(R.id.red_button).setOnClickListener(this);
        findViewById(R.id.orange_button).setOnClickListener(this);
        findViewById(R.id.yellow_button).setOnClickListener(this);
        findViewById(R.id.green_button).setOnClickListener(this);
        findViewById(R.id.blue_button).setOnClickListener(this);
        findViewById(R.id.purple_button).setOnClickListener(this);
        //buttons at bottom
        findViewById(R.id.eraser_button).setOnClickListener(this);
        findViewById(R.id.pen_button).setOnClickListener(this);
        findViewById(R.id.endTurn_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        mPainter.setRoomRef(intent.getStringExtra("roomRef"));
        mRoomRef = mRoomDataRef.child(intent.getStringExtra("roomRef"));

        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                try {
                    remainTime_int = Integer.parseInt(dataSnapshot.child("remainTime").getValue().toString());
                    mRemainTime.setText(Integer.toString(remainTime_int));
                    turn = Integer.parseInt(dataSnapshot.child("turn").getValue().toString());
                    if (didInit == false) {
                        didInit = true;
                        maxPlayer = Integer.parseInt(dataSnapshot.child("maxPlayer").getValue().toString());
                        mRoomName.setText(dataSnapshot.child("roomName").getValue().toString());
                        for (int i = 0; i < maxPlayer; i++) {
                            if (!dataSnapshot.child("userList").child("userID_" + i).getValue().toString().equals("empty")) {
                                playerList.add(dataSnapshot.child("userList").child("userID_" + i).getValue().toString());
                            }
                        }
                    }
                    if (turn != maxPlayer) {
                        if (dataSnapshot.child("userList").child("userID_" + turn).getValue().toString().equals(mUid)) {
                            if (didIDraw == false) {
                                didIDraw = true;
                                setDrawAble(true);
                            }
                        } else {
                            setDrawAble(false);
                        }
                    } else {
                        setDrawAble(false);
                        //이미지를 PNG로 변환
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mPainter.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        //바이트 어레이로 담음
                        byte[] data = baos.toByteArray();

                        String random = UUID.randomUUID().toString();
                        String path = "work/paint/" + random + ".png";
                        StorageReference memeRef = storage.getReference(path);

                        String member = "";
                        for (int i = 0; i < playerList.size(); i++) {
                            if (i != 0) {
                                member += ",";
                            }
                            member += playerList.get(i);
                        }

                        progressBar.setVisibility(View.VISIBLE);
                        mRemainTime.setVisibility(View.GONE);

                        UploadTask uploadTask = memeRef.putBytes(data);
                        uploadTask.addOnSuccessListener(PaintActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBar.setVisibility(View.GONE);
                                mRemainTime.setVisibility(View.VISIBLE);
                            }
                        });
                        mRootRef.child("workData").child("paint").child(random).child("like").setValue(0);
                        mRootRef.child("workData").child("paint").child(random).child("artist").setValue(member);
                        mRootRef.child("workData").child("paint").child(random).child("title").setValue(mRoomName.getText().toString());
                        mRoomRef.removeValue();
                        Toast.makeText(getApplicationContext(), "Your work is saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void doPlus(int num, DatabaseReference ref) {
        ref.setValue(++num);
    }

    public void doMin(int num, DatabaseReference ref) {
        ref.setValue(--num);
    }


    public void setDrawAble(boolean bool) {
        mPainter.isDrawable = bool;
        findViewById(R.id.endTurn_button).setEnabled(bool);
        findViewById(R.id.eraser_button).setEnabled(bool);
        findViewById(R.id.pen_button).setEnabled(bool);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eraser_button:
                mPainter.clearAll();
                break;
            case R.id.black_button:
                mPainter.setColor(R.color.Black);
                break;
            case R.id.white_button:
                mPainter.setColor(R.color.White);
                break;
            case R.id.red_button:
                mPainter.setColor(R.color.Red);
                break;
            case R.id.orange_button:
                mPainter.setColor(R.color.Orange);
                break;
            case R.id.yellow_button:
                mPainter.setColor(R.color.Yellow);
                break;
            case R.id.green_button:
                mPainter.setColor(R.color.Green);
                break;
            case R.id.blue_button:
                mPainter.setColor(R.color.Blue);
                break;
            case R.id.purple_button:
                mPainter.setColor(R.color.Purple);
                break;
            case R.id.endTurn_button:
                mRoomRef.child("turn").setValue((turn + 1));
                break;
        }
    }
}