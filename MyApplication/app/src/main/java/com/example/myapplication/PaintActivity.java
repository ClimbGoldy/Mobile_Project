package com.example.myapplication;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PaintActivity extends AppCompatActivity {


    MyView m_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_view = new MyView(this);
        setContentView(m_view);
    }

    protected class MyView extends View {
        float m_new_x;
        float m_new_y;
        float m_old_x;
        float m_old_y;

        boolean m_is_drawing_flag;
        boolean isCanvasLinked;

        int m_draw_mode;

        Paint m_paint;
        Canvas m_canvas;
        Bitmap m_bitmap;
        RectF m_rect;
        Path m_path;

        public MyView(Context context) {
            super(context);

            isCanvasLinked = false;
            m_draw_mode = 0;
            m_paint = new Paint();
            m_paint.setColor(Color.BLUE);
            m_path = new Path();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            m_bitmap= Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            m_canvas = new Canvas(m_bitmap);
            m_canvas.drawColor(Color.YELLOW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(m_bitmap,0,0,null);

            if (isCanvasLinked == false) {
                m_canvas = canvas;
                m_canvas.drawColor(Color.RED);
                isCanvasLinked = true;

                //없어도 됌
                m_canvas.drawCircle(200, 200, 50, m_paint);
                Toast.makeText(PaintActivity.this, "Made it!", Toast.LENGTH_SHORT).show();
            }
            //여기에 선그려지는거 추가해야함

        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) { // 화면을 손가락으로 터치한 경우
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    return true;
                case MotionEvent.ACTION_UP:
                    m_canvas.drawCircle(x, y, 50, m_paint);
                    Toast.makeText(PaintActivity.this, "Clicked! x:" + x + ", y:" + y, Toast.LENGTH_SHORT).show();
                    invalidate();
                    return true;
                /*
                case MotionEvent.ACTION_DOWN: // Pen 으로 그리기를 하고 있었던 경우
                    m_path.reset();
                    m_path.moveTo(x, y);
                    m_canvas.drawPath(m_path, m_paint);
                    m_is_drawing_flag = true;
                    invalidate();
                    m_old_x = x;
                    m_old_y = y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    m_path.quadTo(m_old_x, m_old_y, x, y);
                    m_canvas.drawPath(m_path, m_paint);
                    invalidate();
                    m_old_x = x;
                    m_old_y = y;
                    return true;
                case MotionEvent.ACTION_UP:
                    m_path.quadTo(m_old_x, m_old_y, x, y);
                    m_canvas.drawPath(m_path, m_paint);
                    invalidate();
                    m_is_drawing_flag = false;
                    return true;
                    */
            }
            return false;
        }
    }
}

/*
public class PaintActivity extends AppCompatActivity {
    float m_new_x;
    float m_new_y;
    float m_old_x;
    float m_old_y;
    boolean m_is_drawing_flag;
    int m_draw_mode;

    Bitmap m_bitmap;
    Paint m_paint;
    RectF m_rect;
    Path m_path;
    Canvas m_canvas;
    MyView m_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_view = new MyView(this);
        setContentView(m_view);

        setColor(android.R.color.holo_green_dark);
        setDrawMode(1);
    }


    protected class MyView extends View {
        public MyView(Context context) {
            super(context);

            m_bitmap = Bitmap.createBitmap(m_view.getWidth(), m_view.getHeight(), Bitmap.Config.ARGB_8888);
            m_canvas = new Canvas(m_bitmap);
            m_canvas.drawColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // 메모리 비트맵에 그린 이미지를 뷰에 그린다.
            canvas.drawBitmap(m_bitmap, 0, 0, null);

            // 그림을 그리고 있는 중인 경우

            if (m_is_drawing_flag == true) {

                if (m_draw_mode == 1) { // 선 그리기
                    // 뷰에 선을 그린다.
                    canvas.drawLine(m_old_x, m_old_y, m_new_x, m_new_y, m_paint);

                } else if (m_draw_mode == 2) { // 사각형 그리기
                    // 뷰에 사각형을 그린다.
                    canvas.drawRect(m_old_x, m_old_y, m_new_x, m_new_y, m_paint);

                } else if (m_draw_mode == 3) { // 원 그리기
                    // RectF 객체에 좌표를 구성한다.
                    m_rect.left = m_old_x;
                    m_rect.top = m_old_y;
                    m_rect.right = m_new_x;
                    m_rect.bottom = m_new_y;

                    // 원을 그린다.
                    canvas.drawOval(m_rect, m_paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            // 화면을 손가락으로 터치한 경우
            case MotionEvent.ACTION_DOWN:
                // Pen 으로 그리기를 하고 있었던 경우
                if (m_draw_mode == 0) {
                    // Path 객체를 초기화하고 시작 위치를 잡는다.
                    m_path.reset();
                    m_path.moveTo(x, y);

                    // 메모리 캔버스에 Path 를 그린다.
                    m_canvas.drawPath(m_path, m_paint);
                } else if (m_draw_mode == 1 || m_draw_mode == 2 || m_draw_mode == 3) {
                    // 현재 좌표값을 저장한다.
                    m_new_x = x;
                    m_new_y = y;
                }
                // 터치를 시작한 시점의 좌표값을 저장한다.
                m_old_x = x;
                m_old_y = y;
                m_view.invalidate();

                // 그리기 작업중임을 표시한다.
                m_is_drawing_flag = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                // Pen 으로 그리기를 하고 있었던 경우
                if (m_draw_mode == 0) {
                    // 곡선형으로 선을 이어나가며 Path 를 구성한다.
                    m_path.quadTo(m_old_x, m_old_y, x, y);
                    // 메모리 캔버스에 Path 를 그린다.
                    m_canvas.drawPath(m_path, m_paint);

                    // 현재 좌표값을 기억한다.
                    m_old_x = x;
                    m_old_y = y;
                } else if (m_draw_mode == 1 || m_draw_mode == 2 || m_draw_mode == 3) {
                    // 선, 사각형, 원을 그리는 경우 터치가 종료되는 시점에
                    // 메모리 캔버스에 그려져야하므로 현재 좌표만 기억하여
                    // 뷰에 직접 그리도록한다.
                    m_new_x = x;
                    m_new_y = y;
                }
                m_view.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                // Pen 으로 그리기를 하고 있었던 경우
                if (m_draw_mode == 0) {
                    // 곡선형으로 선을 이어서 Path 를 구성한다.
                    m_path.quadTo(m_old_x, m_old_y, x, y);
                    // 메모리 캔버스에 Path 를 그린다.
                    m_canvas.drawPath(m_path, m_paint);

                } else if (m_draw_mode == 1) { // 선
                    // 메모리 캔버스에 선을 그린다.
                    m_canvas.drawLine(m_old_x, m_old_y, x, y, m_paint);

                } else if (m_draw_mode == 2) { // 사각형
                    // 메모리 캔버스에 사각형을 그린다.
                    m_canvas.drawRect(m_old_x, m_old_y, x, y, m_paint);

                } else if (m_draw_mode == 3) { // 원
                    // RectF 객체를 구성한다.
                    m_rect.left = m_old_x;
                    m_rect.top = m_old_y;
                    m_rect.right = x;
                    m_rect.bottom = y;

                    // 메모리 캔버스에 원을 그린다.
                    m_canvas.drawOval(m_rect, m_paint);
                }
                m_view.invalidate();

                // 그리기 작업이 끝났음을 표시한다.
                m_is_drawing_flag = false;
                return true;
        }
        return false;
    }

    // 색상을 설정한다.
    public void setColor(int parm_color) {
        m_paint.setColor(parm_color);
    }

    public void setDrawMode(int parm_mode) {
        m_draw_mode = parm_mode;
    }
}
*/