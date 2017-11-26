package hbmeter.hbmeter;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Giuseppe on 02/07/2017.
 */

public class PreviewDemo extends Activity implements View.OnClickListener {

    ProgressDialog dialog;
    static final int REQUEST_TAKE_PHOTO  = 2;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    private boolean inPreview = false;
    ImageView image;
    Bitmap bmp;
    static Bitmap mutableBitmap;
    File imageFileName = null;
    private static  final int FOCUS_AREA_SIZE= 300;
    File imageFileFolder = null;
    private MyMediaScannerConnectionClient msConn;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);

        Button b=(Button)findViewById(R.id.button2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();

            }
        });
        image = (ImageView) findViewById(R.id.image);
        preview = (SurfaceView) findViewById(R.id.surface);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    focusOnTouch(event);
                }
                return true;
            }
        });
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);

        previewHolder.setFixedSize(getWindow().getWindowManager()
                .getDefaultDisplay().getWidth(), getWindow().getWindowManager()
                .getDefaultDisplay().getHeight());


    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };

    private void focusOnTouch(MotionEvent event) {
        if (camera != null ) {

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i("TAG","fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                camera.setParameters(parameters);
                camera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                camera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / preview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / preview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
        Box box = new Box(this);
        addContentView(box, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;
        super.onPause();
    }

    private Camera.Size getBestPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setDisplayOrientation(90);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCall","Exception in setPreviewDisplay()", t);
                Toast.makeText(PreviewDemo.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();

            Camera.Size size = getBestPreviewSize(mSupportedPreviewSizes, width,
                    height);

            if (size != null) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(size.width, size.height);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
                camera.startPreview();
                inPreview = true;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

    boolean save=false;



    Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, final Camera camera) {


            Log.i("prova save",Boolean.toString(save));
            AlertDialog.Builder builder1 = new AlertDialog.Builder(PreviewDemo.this);
            builder1.setMessage("Assicurati che la congiuntiva è completamente contenuta nel riquadro verde. Hai scattato correttamente la foto?");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                          //dialog = ProgressDialog.show(PreviewDemo.this, "", "Taglio dell'immagine in corso");
                            ProgressDialog dialog2 = new ProgressDialog(PreviewDemo.this);
                            dialog2.setMessage("ciao");
                            dialog2.setCancelable(false);
                            dialog2.show();


                            new Thread() {
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (Exception ex) {}
                                    onPictureTake(data, camera);
                                }
                            }.start();
                            dialog2.dismiss();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                        }
                    });
            AlertDialog alert=builder1.create();
            alert.getWindow().setGravity(Gravity.BOTTOM);
            alert.show();



        }
    };




    public void onPictureTake(byte[] data, Camera camera) {


        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        savePhoto(mutableBitmap);
    }


    class SavePhotoTask extends AsyncTask< byte[], String, String > {@Override
    protected String doInBackground(byte[]...jpeg) {
        File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(jpeg[0]);
            fos.close();
        } catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return (null);
    }
    }


    public void savePhoto(Bitmap bmp) {
        imageFileFolder = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter");
        if(!imageFileFolder.exists()){
            imageFileFolder.mkdir();
        }
        imageFileFolder = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter/HbMeterPhotos");
        if(!imageFileFolder.exists()){
            imageFileFolder.mkdir();
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bmrt= Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.DAY_OF_MONTH))+"-"+((fromInt((c.get(Calendar.MONTH))+1)))+"-"+fromInt(c.get(Calendar.YEAR))+"_"+ fromInt(c.get(Calendar.HOUR_OF_DAY))+":"+ fromInt(c.get(Calendar.MINUTE))+":"+ fromInt(c.get(Calendar.SECOND));
        imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
        Log.i("prova",imageFileName.toString());



        try {
            out = new FileOutputStream(imageFileName);
            bmrt.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }




        File imageFileNameRotated=null;
        imageFileNameRotated = new File(imageFileFolder, date.toString() + "_ROTATED" + ".jpg");

        CutPhoto.cut(bmrt,imageFileNameRotated.toString());

        scanPhoto(imageFileNameRotated.toString());
        Intent end=new Intent();
        end.putExtra("image",imageFileNameRotated.toString());
        setResult(REQUEST_TAKE_PHOTO,end);
        finish();


    }

    public String fromInt(int val) {
        return String.valueOf(val);
    }

    class MyMediaScannerConnectionClient
            implements MediaScannerConnection.MediaScannerConnectionClient {

        private String mFilename;
        private String mMimetype;
        private MediaScannerConnection mConn;

        public MyMediaScannerConnectionClient
                (Context ctx, File file, String mimetype) {
            this.mFilename = file.getAbsolutePath();
            mConn = new MediaScannerConnection(ctx, this);
            mConn.connect();
        }
        @Override
        public void onMediaScannerConnected() {
            mConn.scanFile(mFilename, mMimetype);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mConn.disconnect();
        }
    }

    public void scanPhoto(final String imageFileName) {
        Log.i("provaname",imageFileName.toString());
        /*msConn = new MediaScannerConnection(PreviewDemo.this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj  in Photo ", "connection established");
            }
            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj in Photo U", "scan completed");
            }
        });*/
        msConn=new MyMediaScannerConnectionClient(getApplicationContext(),new File(imageFileName),null);
    }

    public void onBack() {
        Log.e("onBack :", "yes");
        camera.takePicture(null, null, photoCallback);
        inPreview = false;
    }



    @Override
    public void onClick(View v) {

    }
}
