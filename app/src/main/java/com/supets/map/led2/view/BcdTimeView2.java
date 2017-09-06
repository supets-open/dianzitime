package com.supets.map.led2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.supets.map.led2.storge.TimeConfig;

import java.util.Calendar;
import java.util.Date;


//w+h=3p   2w+3h  22p  18p  假w=5h

public class BcdTimeView2 extends TextView {

    private Paint paint = new Paint();

    public BcdTimeView2(Context context) {
        super(context);
    }

    public BcdTimeView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BcdTimeView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                (int) (MeasureSpec.getSize(widthMeasureSpec) * 13f / 36));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //   canvas.drawColor(TimeConfig.getBackColor());
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        int P = getWidth() / 18;
        drawBCD(canvas, paint, P);
        drawdian(canvas, paint, P);
    }

    private void drawdian(Canvas canvas, Paint paint, int p) {
        if (dian) {
            paint.setColor(TimeConfig.getTimeColor());
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(9 * p, (float) (1.5 * p), (float) (0.5*p), paint);
            canvas.drawCircle(9 * p, (float) (4.5 * p), (float) (0.5*p), paint);
        } else if (isshowKong) {
            paint.setColor(TimeConfig.getTimeColor());
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(9 * p, (float) (1.5 * p), (float) (0.5*p), paint);
            canvas.drawCircle(9 * p, (float) (4.5 * p), (float) (0.5*p), paint);
        }
    }

    private void drawBCD(Canvas canvas, Paint paint, int P) {

        Point[] xy = new Point[]{
                new Point(0, 0),
                new Point((int) (4.5 * P), 0),
                new Point((int) (10.5 * P), 0),
                new Point(15 * P, 0),
        };


        for (int j = 0; j < 4; j++) {
            Point point = xy[j];
            Path[] path2 = BCD.buildBCDW5H(P, point.x, point.y);
            for (int i = 0; i < 7; i++) {
                Path d = path2[i];
                boolean is = BCD.isBitLight(BCD.getNum(rom[j]), i);
                if (is) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(TimeConfig.getTimeColor());
                    canvas.drawPath(d, paint);
                } else if (isshowKong) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(TimeConfig.getTimeColor());
                    canvas.drawPath(d, paint);

                }
            }
        }
    }


    private int[] rom = new int[]{8, 8, 8, 8};
    private boolean dian = true;
    private int ampm = 2;

    private boolean isshowKong = false;


    public void updateData(TimeCallBack callBack) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int shi0 = calendar.get(Calendar.HOUR);
        int miao = calendar.get(Calendar.MINUTE);
        dian = !dian;
        ampm = calendar.get(Calendar.AM_PM);

        int shi = (ampm == 1) ? shi0 + 12 : 0;

        if (rom[0] != 8) {
            if (callBack != null) {
                callBack.onTime((rom[0] * 10 + rom[1]) != shi, ampm == 0, shi0);
            }
        }

        rom[0] = shi / 10;
        rom[1] = shi % 10;
        rom[2] = miao / 10;
        rom[3] = miao % 10;
    }

    public String getData() {
        return (ampm == 0 ? "上午" : "下午") + (rom[0] * 10 + rom[1]) + "点" + (rom[2] * 10 + rom[3]) + "分";
    }


}