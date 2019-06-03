package com.example.myapplication;
/**
 * @author Suhwan Kown
 * @version 1.0.3
 * @deprecated setStrokeWide(int wide)
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingPaperActivity extends View {
    Bitmap mBitmap;
    Canvas mCanvas;
    Paint mPaint = new Paint();
    float mOldX;
    float mOldY;
    boolean isDrawable = false;
    // Firebase
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRoomDataRef = mRootRef.child("roomData").child("paint");
    DatabaseReference mRoomRef;

    public DrawingPaperActivity(Context context) {
        super(context);
    }

    public DrawingPaperActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCanvas = new Canvas();
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);

        mCanvas.drawColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
    }

    public boolean Save(String file_name) {
        try {
            FileOutputStream out = new FileOutputStream(file_name);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        if (command.equals("save")) {
            Save(path + "img.jpg");
            Toast.makeText(getContext(), "SAVE", Toast.LENGTH_SHORT).show();
            //TODO add save function to cloud DB
        }

        if (command.equals("open")) {
            Toast.makeText(getContext(), "OPEN", Toast.LENGTH_SHORT).show();
            Bitmap storedImg = BitmapFactory.decodeFile(path + "img.jpg");

            if (storedImg != null) {
                mBitmap.eraseColor(Color.WHITE);
                int width = storedImg.getWidth();
                int height = storedImg.getHeight();

                Bitmap sBitmap = Bitmap.createScaledBitmap(storedImg, width / 2,
                        height / 2, false);
                int x1 = mCanvas.getWidth() / 2 - sBitmap.getWidth() / 2;
                int y1 = mCanvas.getHeight() / 2 - sBitmap.getHeight() / 2;
                mCanvas.drawBitmap(sBitmap, x1, y1, mPaint);
            } else Toast.makeText(getContext(), "저장된 파일이 없습니다", Toast.LENGTH_SHORT).show();
        }
        command = "";
        */
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOldX = x;
                mOldY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDrawable){
                    try {
                        mRoomRef.child("Line")
                                .setValue(mOldX + ","
                                        + mOldY + ","
                                        + x + ","
                                        + y + ","
                                        + mPaint.getColor()
                                );
                    } catch (Exception e) {
                        //TODO-Lee define exact exception
                    }
                }
                mOldX = x;
                mOldY = y;
                break;
            case MotionEvent.ACTION_UP:
                if(isDrawable){
                    try {
                        mRoomRef.child("Line")
                                .setValue(mOldX + ","
                                        + mOldY + ","
                                        + x + ","
                                        + y + ","
                                        + mPaint.getColor()
                                );
                    } catch (Exception e) {
                        //TODO-Lee define exact exception
                    }
                }
                break;
        }
        //invalidate();
        return true;
    }

    public void clearAll() {
        mBitmap.eraseColor(getColorFromResource(R.color.White));
        invalidate();
    }

    //TODO delete in next version
    public void setStrokeWide(int wide) {
        mPaint.setStrokeWidth(wide);
    }

    public void setColor(int color) {
        mPaint.setColor(getColorFromResource(color));
    }

    /**
     * @param x1    x coordinates of point_1
     * @param y1    y coordinates of point_1
     * @param x2    x coordinates of point_2
     * @param y2    y coordinates of point_2
     * @param color color of line
     *              TODO-Kown consider to add strokeWide parameter
     * @author Suhwan Kown
     */
    public void addLine(float x1, float y1, float x2, float y2, int color) {
        mPaint.setColor(color);
        mCanvas.drawLine(x1, y1, x2, y2, mPaint);
        invalidate();
    }

    /**
     * @param color int parameter from colors.xml
     * @return int parameter that Paint object can read
     * @author Suhwan Kown
     */
    public int getColorFromResource(int color) {
        return getResources().getColor(color);
    }

    public void setRoomRef(String s) {
        mRoomRef = mRoomDataRef.child(s);
        mRoomRef.child("Line").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String tempString = dataSnapshot.getValue(String.class);
                    String[] tempIndex = tempString.split(",");
                    float tempOldX = Float.parseFloat(tempIndex[0]);
                    float tempOldY = Float.parseFloat(tempIndex[1]);
                    float tempX = Float.parseFloat(tempIndex[2]);
                    float tempY = Float.parseFloat(tempIndex[3]);
                    int tempColor = Integer.parseInt(tempIndex[4]);
                    DrawingPaperActivity.this.addLine(tempOldX, tempOldY, tempX, tempY, tempColor);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}