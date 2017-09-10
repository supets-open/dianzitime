package com.supets.map.led2.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.supets.map.led2.sms.BaseSmsActivity;

import java.io.FileOutputStream;
import java.util.List;

public class BaseCameraActivity extends BaseSmsActivity implements Camera.PreviewCallback, SurfaceHolder.Callback {

    Camera mCammera;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    int mCammeraId = 0;
    private int width = 1280;
    private int height = 720;

    private MyOrientationDetector detector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new SurfaceView(this);
        mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        setContentView(mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        detector = new MyOrientationDetector(this);


        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                takepicture();
            }
        }, 6000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void initCamera() {

        try {
            //打开相机
            mCammera = Camera.open(mCammeraId);
            //绑定预览
            mCammera.setPreviewDisplay(mSurfaceHolder);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            //设置参数
            Camera.Parameters parameters = mCammera.getParameters();
            //14
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);


            //设置预览大小
            parameters.setPreviewSize(width, height);
            parameters.setPreviewFormat(ImageFormat.NV21);
            //设置拍照大小
            parameters.setPictureSize(width, height);
            parameters.setPictureFormat(ImageFormat.JPEG);

            //设置预览方向
            //mCammera.setDisplayOrientation(90);//竖屏
            setCameraDisplayOrientation();
            //设置拍照方向
            parameters.setRotation(0);//图像数据里面不一定有这个值,这个值不一定起作用
            parameters.set("rotation", 0);

            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            for (Camera.Size size : pictureSizes) {
                Log.d("picturesize", "width=" + size.width + ",height=" + size.height);
            }

            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            for (Camera.Size size : previewSizes) {
                Log.d("previewSizes", "width=" + size.width + ",height=" + size.height);
            }

            mCammera.setParameters(parameters);//设置参数
            mCammera.setPreviewCallback(this);//设置每帧回调
            mCammera.startPreview();//启动预览

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }


    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCammeraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCammera.setDisplayOrientation(result);
    }


    public void destoryCamera() {
        if (mCammera == null) {
            return;
        }
        mCammera.setPreviewCallback(null);//先停止预览回调
        mCammera.release();//释放相机
        mCammera = null;//相机置空
    }


    public void changeCamera() {
        mCammeraId = 1 - mCammeraId;
        destoryCamera();//先销毁
        initCamera();//初始化相机
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        //Log.d("getPreviewSize", "width=" + size.width + ",height=" + size.height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        changeCamera();
        Log.d("surfaceCreated", "width=" + width + ",height=" + height);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                               int height) {
        Log.d("surfaceChanged", "width=" + width + ",height=" + height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        destoryCamera();
    }


    public void takepicture() {
        setRotation();
        mCammera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Camera.Size size = camera.getParameters().getPictureSize();
                Log.d("onPictureTaken", "width=" + size.width + ",height=" + size.height);
                Log.d("onPictureTaken", "width=" + bytes.length);


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;//不会存在bitmap，只能解析出基本图形信息
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        options);
                int originalFileHeight = options.outHeight;
                int originalfileWidth = options.outWidth;
                Log.d("onPictureTaken", "originalfileWidth=" + originalfileWidth
                        + ",originalFileHeight=" + originalFileHeight);

                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        options);
                Log.d("onPictureTaken", "originalfileWidth=" + bitmap.getWidth()
                        + ",originalFileHeight=" + bitmap.getHeight());

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.jpg";
                try {
                    FileOutputStream outputStream = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                    ExifInterface rotate = new ExifInterface(path);
                    Log.d("rotate", "rotate=" + rotate.getAttribute(ExifInterface.TAG_ORIENTATION));

                    String phone = getIntent().getStringExtra("phone");
                    if (!TextUtils.isEmpty(phone)) {
                        sendSms(phone, path);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mCammera.startPreview();
            }
        });
    }

    private void setRotation() {
        if (mCammera != null) {
            int orientation = detector.getOrientation();
            Camera.Parameters cameraParameter = mCammera.getParameters();
            cameraParameter.setRotation(90);
            cameraParameter.set("rotation", 90);
            if ((orientation >= 45) && (orientation < 135)) {
                cameraParameter.setRotation(180);
                cameraParameter.set("rotation", 180);
            }
            if ((orientation >= 135) && (orientation < 225)) {
                cameraParameter.setRotation(270);
                cameraParameter.set("rotation", 270);
            }
            if ((orientation >= 225) && (orientation < 315)) {
                cameraParameter.setRotation(0);
                cameraParameter.set("rotation", 0);
            }
            mCammera.setParameters(cameraParameter);
        }
    }

    //短信处理
    @Override
    public void sendSmsStatus(boolean status) {
        finish();
    }

}
