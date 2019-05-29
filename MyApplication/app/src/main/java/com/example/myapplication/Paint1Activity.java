package com.example.myapplication;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class Paint1Activity extends AppCompatActivity {
    CheckBox checkBox;
    Paint2Activity mypainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        mypainter = (Paint2Activity) findViewById(R.id.canvas);
        checkBox = (CheckBox)findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.setChecked(isChecked);
                mypainter.IsChecked = isChecked;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Bluring").setCheckable(true);
        menu.add(0, 2, 0, "Coloring").setCheckable(true);
        menu.add(0, 3, 0, "Pen Width Big").setCheckable(true);
        menu.add(0, 4, 0, "Pen Color Red");
        menu.add(0, 5, 0, "Pen Color Blue");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (item.isChecked()) {
                item.setChecked(false);
                mypainter.setBlurring(false);
            } else {
                item.setChecked(true);
                mypainter.setBlurring(true);
            }

        }
        else if (item.getItemId() == 2) {
            if (item.isChecked()) {
                item.setChecked(false);
                mypainter.setColoring(false);
            } else {
                item.setChecked(true);
                mypainter.setColoring(true);
            }

        }
        else if (item.getItemId() == 3) {
            if (item.isChecked()) {
                mypainter.setPenWidth(3);
                item.setChecked(false);
            } else {
                mypainter.setPenWidth(5);
                item.setChecked(true);
            }
        }
        else if (item.getItemId() == 4) {
            mypainter.setOperation("red");
        }
        else if (item.getItemId() == 5) {
            mypainter.setOperation("blue");
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClick(View v){
        if(v.getId()==R.id.b1) {
            mypainter.setOperation("clear");
        }
        else if(v.getId()==R.id.b2){
            mypainter.setOperation("open");
        }
        else if(v.getId()==R.id.b3){
            mypainter.setOperation("save");
            mypainter.path = getFilesDir() + "";
        }
        else if(v.getId()==R.id.b4){
            checkBox.setChecked(true);
            mypainter.IsChecked = true;
            mypainter.setOperation("rotate");
        }
        else if(v.getId()==R.id.b5){
            mypainter.IsChecked = true;
            checkBox.setChecked(true);
            mypainter.setOperation("move");
        }
        else if(v.getId()==R.id.b6){
            mypainter.IsChecked = true;
            checkBox.setChecked(true);
            mypainter.setOperation("scale");
        }
        else if(v.getId()==R.id.b7){
            mypainter.IsChecked = true;
            checkBox.setChecked(true);
            mypainter.setOperation("skew");
        }
    }
}