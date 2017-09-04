package com.supets.map.led2.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.colorpicker.ColorPickView;
import com.supets.map.led2.R;
import com.supets.map.led2.storge.TimeConfig;
import com.supets.map.led2.utils.App;


public class ColorPickerActivity extends Activity {
    private TextView txtColor;
    private ColorPickView myView;
    private int mColor=TimeConfig.getTimeColor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpicker);
        myView = findViewById(R.id.color_picker_view);
        txtColor = findViewById(R.id.txt_color);
        txtColor.setBackgroundColor(mColor);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeConfig.setTimeColor(mColor);
                App.toast("你的姿势完全正确！");
                finish();
            }
        });

        myView.setPaintPixel(Color.rgb(255, 0, 0));
        myView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {

            @Override
            public void onColorChange(int color) {
                mColor=color;
                txtColor.setBackgroundColor(mColor);
//                int r = Color.red(color);
//                int g = Color.green(color);
//                int b = Color.blue(color);
            }

        });
    }

}