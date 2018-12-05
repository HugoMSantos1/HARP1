package harp.medpick.com.inventory;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.app.ActionBar;
import android.widget.FrameLayout;

//cmanager
import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

//preview
import java.io.IOException;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

//hovers
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class BarcodeReader extends Activity {
    private CameraPreview mPreview;
    private CameraManager mCameraManager;
    private HoverView mHoverView;
    //private static final int REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

       // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
               // REQUEST_CAMERA);
        Display display = getWindowManager().getDefaultDisplay();
        mHoverView = (HoverView)findViewById(R.id.hover_view);
        mHoverView.update(display.getWidth(), display.getHeight());

        mCameraManager = new CameraManager(this);
        mPreview = new CameraPreview(this, mCameraManager.getCamera());
        mPreview.setArea(mHoverView.getHoverLeft(), mHoverView.getHoverTop(), mHoverView.getHoverAreaWidth(), display.getWidth());
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        ActionBar actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

    }

   @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
        mCameraManager.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mCameraManager.onResume();
        mPreview.setCamera(mCameraManager.getCamera());
    }
}


class CameraManager {
    private Camera mCamera;
    private Context mContext;


    public CameraManager(Context context) {
        mContext = context;
        // Create an instance of Camera
        mCamera = getCameraInstance();
    }

    public Camera getCamera() {
        return mCamera;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    public void onPause() {
        releaseCamera();
    }

    public void onResume() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }

        Toast.makeText(mContext, "preview size = " + mCamera.getParameters().getPreviewSize().width +
                ", " + mCamera.getParameters().getPreviewSize().height, Toast.LENGTH_LONG).show();
    }

    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

}

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final String TAG = "camera";
    private int mWidth, mHeight;
    private Context mContext;
    private MultiFormatReader mMultiFormatReader;
    private AlertDialog mDialog;
    private int mLeft, mTop, mAreaWidth, mAreaHeight;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Parameters params = mCamera.getParameters();

        mWidth = 640;
        mHeight = 480;

        params.setPreviewSize(mWidth, mHeight);
        mCamera.setParameters(params);

        mMultiFormatReader = new MultiFormatReader();

        mDialog =  new AlertDialog.Builder(mContext).create();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null){
            return;
        }

        try {
            mCamera.stopPreview();

        } catch (Exception e){
            Log.d(TAG, "Error stopping camera preview: " + e.getMessage());
        }

        try {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void onPause() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
    }

    private Camera.PreviewCallback mPreviewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub

            if (mDialog.isShowing())
                return;

            LuminanceSource source = new PlanarYUVLuminanceSource(data, mWidth, mHeight, mLeft, mTop, mAreaWidth, mAreaHeight, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                    source));
            Result result;

            try {
                result = mMultiFormatReader.decode(bitmap, null);
                if (result != null) {
                    mDialog.setTitle("Result");
                    mDialog.setMessage(result.getText());
                    mDialog.show();
                }
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public void setArea(int left, int top, int mAreaWidthareaWidth, int width) {
        double ratio = width / mWidth;
        mLeft = (int) (left / (ratio + 1));
        mTop = (int) (top / (ratio + 1));
        mAreaHeight = mAreaWidth = mWidth - mLeft * 2;
    }

}

class HoverView extends View {
    private Paint mPaint;
    private int mLeft, mTop, mRight, mBottom;

    public HoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void update(int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2;
        mLeft = centerX - 200;
        mRight = centerX + 200;
        mTop = centerY - 200;
        mBottom = centerY + 200;
        invalidate();
    }

    public int getHoverLeft() {
        return mLeft;
    }

    public int getHoverTop() {
        return mTop;
    }

    public int getHoverAreaWidth() {
        return mRight - mLeft;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        canvas.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
    }
}

