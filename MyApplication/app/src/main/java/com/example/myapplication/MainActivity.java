package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    TextView mTextViewCondition;
    Button mButtonSunny;
    Button mButtonRainy;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("condition");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCondition = (TextView) findViewById(R.id.textCondition);
        mButtonSunny = (Button) findViewById(R.id.buttonSunny);
        mButtonRainy = (Button) findViewById(R.id.buttonRainy);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                mTextViewCondition.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mButtonSunny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConditionRef.setValue("Sunny");
            }
        });

        mButtonRainy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mConditionRef.setValue("Rainy");
            }
        });
    }
}