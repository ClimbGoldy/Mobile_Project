package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Paint2Activity extends View {
    Bitmap mBitmap;
    Canvas mCanvas;
    String operation = "";
    String command = "";
    String path = "";
    String option = "";
    int width = 0, height = 0;
    Boolean IsSelected = false;
    int cx= -1, cy = -1;
    Paint mPaint = new Paint();
    boolean IsChecked = false;
    int sx=-1, sy=-1;

    public Paint2Activity (Context context) {
        super(context);
    }

    public Paint2Activity (Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mPaint.setStrokeWidth(3);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(Color.YELLOW);
    }
    private void drawStamp(){
        Matrix matrix = new Matrix();
        Bitmap img = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        width = img.getWidth();
        height = img.getHeight();
        mCanvas.save();
        if(operation.equals("rotate")){
            mCanvas.rotate(30,mCanvas.getWidth()/2,mCanvas.getHeight()/2);
        }
        else if(operation.equals("move")) {
            sx += 10;
            sy += 10;
        }
        else if(operation.equals("scale")){
            mCanvas.scale(1.5f,1.5f);
            sx= (int)(cx / 1.5 - (width /2)) ;sy=(int)(cy / 1.5- (height /2));
        }
        else if(operation.equals("skew"))
            mCanvas.skew(0.2f,0.0f);
        mCanvas.drawBitmap(img,sx,sy,mPaint);
    }

    public void setOperation(String op){
        if(op.equals("save")) this.command = op;
        else if(op.equals("clear")) this.command = op;
        else if(op.equals("open")) this.command = op;
        else if(op.equals("blue")) this.option = op;
        else if(op.equals("red")) this.option = op;
        else {this.operation = op; IsSelected = true;}
        invalidate();
    }
    public boolean Save(String file_name){
        try{
            FileOutputStream out = new FileOutputStream(file_name);
            mBitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException",e.getMessage());
        } catch (IOException e) {
            Log.e("IOException",e.getMessage());
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(IsChecked) {
            if(!IsSelected){
                drawStamp();
                operation="";
                mCanvas.restore();}
            else{
                IsSelected = false;
            }
        }
        if(command.equals("clear")) {
            mBitmap.eraseColor(Color.WHITE);
        }
        if(command.equals("save")){

            Save(path + "img.jpg");
            Toast.makeText(getContext(),"SAVE",Toast.LENGTH_SHORT).show();
        }

        if(command.equals("open")){
            Toast.makeText(getContext(),"OPEN",Toast.LENGTH_SHORT).show();
            Bitmap storedImg = BitmapFactory.decodeFile(path+"img.jpg");

            if(storedImg !=null){
                mBitmap.eraseColor(Color.WHITE);
                int width = storedImg.getWidth();
                int height = storedImg.getHeight();

                Bitmap sBitmap = Bitmap.createScaledBitmap(storedImg,width/2,
                        height/2,false);
                int x1 = mCanvas.getWidth() /2 - sBitmap.getWidth()/2;
                int y1 = mCanvas.getHeight()/2 - sBitmap.getHeight()/2;
                mCanvas.drawBitmap(sBitmap,x1,y1,mPaint);
            }
            else Toast.makeText(getContext(),"저장된 파일이 없습니다",Toast.LENGTH_SHORT).show();
        }

        if(option.equals("big")) {mPaint.setStrokeWidth(5);}
        if(option.equals("small")) mPaint.setStrokeWidth(3);
        if(option.equals("red")) mPaint.setColor(Color.RED);
        if(option.equals("blue")) mPaint.setColor(Color.BLUE);
        command="";
        option="";
        canvas.drawBitmap(mBitmap,0,0,null);
    }


    public void setBlurring(boolean tf){
        if(tf) {
            BlurMaskFilter blur = new BlurMaskFilter(100, BlurMaskFilter.Blur.NORMAL);
            mPaint.setMaskFilter(blur);
        }
        else{
            mPaint.setMaskFilter(null);
        }
    }

    public void setColoring(boolean tf){
        if(tf) {
            float[] array = {
                    2, 0, 0, 0, -25f,
                    0, 2, 0, 0, -25f,
                    0, 0, 2, 0, -25f,
                    0, 0, 0, 2, 0
            };

            ColorMatrix colorMatrix = new ColorMatrix(array);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
            mPaint.setColorFilter(filter);
        }
        else{
            mPaint.setColorFilter(null);
        }
    }

    public void setPenWidth(int size){
        if(size==5)
            mPaint.setStrokeWidth(5);
        else{
            mPaint.setStrokeWidth(3);
        }
    }

    int oldx = -1; int oldy = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!IsChecked){
            int x = (int)event.getX();
            int y = (int)event.getY();

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                oldx =x; oldy = y;
            }
            else if(event.getAction() == MotionEvent.ACTION_MOVE){
                if (oldx != -1){
                    mCanvas.drawLine(oldx,oldy,x,y,mPaint);
                    invalidate();
                    oldx = x; oldy = y;
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                if (oldx != -1){
                    mCanvas.drawLine(oldx,oldy,x,y,mPaint);
                    invalidate();
                }
                oldx = -1; oldy = -1;
            }
        }
        else{
            cx = (int)event.getX();
            cy = (int)event.getY();
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                sx= cx - (width/2) ;sy=cy - (height/2);
                invalidate();
            }
        }
        return true;
    }

}