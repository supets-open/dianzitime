package com.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by KomoriWu
 * on 2017-06-29.
 */

public class ColorPickView extends View {
    private Context context;
    private int bigCircle; // 外圈半径
    private int rudeRadius; // 可移动小球的半径
    private int centerColor; // 可移动小球的颜色
    private Paint mPaint; // 背景画笔
    private Paint mCenterPaint; // 可移动小球画笔
    private Point centerPoint;// 中心位置
    private Point mRockPosition;// 小球当前位置
    private Point mRockPixel;// 小球贴近边缘时取相切点的值
    private OnColorChangedListener listener; // 小球移动的监听
    private int length; // 小球到中心位置的距离
    private float[] colorHSV = {0.0F, 0.0F, 1.0F};

    private Paint mColorWheelPaint;  // 绘制色盘
    private Bitmap mColorWheelBitmap;    // 彩灯位图
    private int centerX, centerY;
    private Rect mColorWheelRect;

    public ColorPickView(Context context) {
        super(context);
    }

    public ColorPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public ColorPickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    /**
     * @param attrs
     * @describe 初始化操作
     */
    private void init(AttributeSet attrs) {
        // 获取自定义组件的属性
        TypedArray types = context.obtainStyledAttributes(attrs,
                R.styleable.color_picker);
        try {
            bigCircle = types.getDimensionPixelOffset(
                    R.styleable.color_picker_circle_radius, 100);
            rudeRadius = types.getDimensionPixelOffset(
                    R.styleable.color_picker_center_radius, 10);
            centerColor = types.getColor(R.styleable.color_picker_center_color,
                    Color.WHITE);
        } finally {
            types.recycle(); // TypeArray用完需要recycle
        }
        // 中心位置坐标
        centerPoint = new Point(bigCircle, bigCircle);
        mRockPosition = new Point(centerPoint);
        mRockPixel = new Point(centerPoint);
        // 初始化背景画笔和可移动小球的画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mCenterPaint = new Paint();
        mCenterPaint.setColor(centerColor);

        mColorWheelPaint = new Paint();
        mColorWheelPaint.setAntiAlias(true);//消除锯齿
        mColorWheelPaint.setDither(true);//防抖动
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        centerX = width / 2;
        centerY = height / 2;

        mColorWheelRect = new Rect(centerX - bigCircle, centerY - bigCircle,
                centerX + bigCircle, centerY + bigCircle);

        mColorWheelBitmap = createColorWheelBitmap(bigCircle * 2, bigCircle * 2);
    }

    //HSV模型创建取色盘
    public Bitmap createColorWheelBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = 360 - (i * colorAngleStep) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors,
                null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2,
                bigCircle, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient,
                PorterDuff.Mode.SRC_OVER);

        mColorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, bigCircle, mColorWheelPaint);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画背景图片
        canvas.drawBitmap(mColorWheelBitmap, mColorWheelRect.left, mColorWheelRect.top, mPaint);
        // 画中心小球
        canvas.drawCircle(mRockPosition.x, mRockPosition.y, rudeRadius,
                mCenterPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下
                length = getLength(event.getX(), event.getY(), centerPoint.x,
                        centerPoint.y);
                if (length > bigCircle - rudeRadius) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                length = getLength(event.getX(), event.getY(), centerPoint.x,
                        centerPoint.y);
                if (length <= bigCircle - rudeRadius) {
                    mRockPosition.set((int) event.getX(), (int) event.getY());
                    mRockPixel.set((int) event.getX(), (int) event.getY());
                } else {
                    //相切时，取切点位置的颜色，但圆心还是在大圆内
                    mRockPosition = getBorderPoint(centerPoint, new Point(
                            (int) event.getX(), (int) event.getY()), bigCircle
                            - rudeRadius);
                    mRockPixel = getBorderPoint(centerPoint, new Point(
                            (int) event.getX(), (int) event.getY()), bigCircle);
                }
                listener.onColorChange(mColorWheelBitmap.getPixel(Math.min(mRockPixel.x,
                        mColorWheelBitmap.getWidth() - 1), Math.min(mRockPixel.y,
                        mColorWheelBitmap.getHeight() - 1)));
                break;
            case MotionEvent.ACTION_UP:// 抬起
                break;
            default:
                break;
        }
        invalidate(); // 更新画布
        return true;
    }

    //根据颜色值定位到像素点
    public void setPaintPixel(int colorInt) {
        Color.colorToHSV(colorInt, colorHSV);
        float f = (float) Math.toRadians(colorHSV[0]);
        int x = (int) Math.abs(Math.cos(f) * colorHSV[1] * (bigCircle - rudeRadius));
        int y = (int) Math.abs(Math.sin(f) * colorHSV[1] * (bigCircle - rudeRadius));

        if (colorHSV[0] <= 90) {
            x = bigCircle + x;
            y = bigCircle - y;
        } else if (colorHSV[0] <= 180) {
            x = bigCircle - x;
            y = bigCircle - y;
        } else if (colorHSV[0] <= 270) {
            x = bigCircle - x;
            y = bigCircle + y;
        } else if (colorHSV[0] <= 360) {
            x = bigCircle + x;
            y = bigCircle + y;
        }
        mRockPosition.set(x, y);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 视图大小设置为直径
        setMeasuredDimension(bigCircle * 2, bigCircle * 2);
    }

    //计算两点之间的位置
    public static int getLength(float x1, float y1, float x2, float y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    // 当触摸点超出圆的范围的时候，设置小球边缘位置
    public static Point getBorderPoint(Point a, Point b, int cutRadius) {
        float radian = getRadian(a, b);
        return new Point(a.x + (int) (cutRadius * Math.cos(radian)), a.x
                + (int) (cutRadius * Math.sin(radian)));
    }

    //触摸点与中心点之间直线与水平方向的夹角角度
    public static float getRadian(Point a, Point b) {
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y < a.y ? -1 : 1);
        return ang;
    }

    // 颜色发生变化的回调接口
    public interface OnColorChangedListener {
        void onColorChange(int color);
    }
}
