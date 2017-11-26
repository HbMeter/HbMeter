package hbmeter.hbmeter;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.ximgproc.SuperpixelSLIC;
import org.opencv.ximgproc.Ximgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Fragment prova;
    static final int REQUEST_ORIGINAL_CAMERA = 22;
    android.app.FragmentManager manager;
    final int PERMISSION_CAMERA_SETTINGS=20;
    final int REQUEST_STORAGE_SETTINGS=5;
    final int PERMISSION_CAMERA=3;
    final int REQUEST_STORAGE=4;
    final int REQUEST_STORAGE_REPORT=9;
    final int REQUEST_TAKE_PHOTO  = 2;

    static{
        if (OpenCVLoader.initDebug()){
            Log.i("success","OpenCV initialize success");
        } else {
            Log.i("failed","OpenCV initialize failed");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        View view = navigation.findViewById(R.id.navigation_dashboard);
        view.performClick();







    }


    @Override
    public void onBackPressed() {
        Fragment f = getFragmentManager().findFragmentById(R.id.content);
        if (f instanceof DettaglioFragment){
            prova = new ReportFragment();
            manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content, prova).commit();
        }else  if (f instanceof ElaborazioneFragment){
            prova = new AnalizzaFragment();
            manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content, prova).commit();
        }else super.onBackPressed();
    }


  /*  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //Log.i("prova",uri.toString());
                Mat g=new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
                Utils.bitmapToMat(bitmap,g,true);
                SuperpixelSLIC x= Ximgproc.createSuperpixelSLIC(g,Ximgproc.SLIC,100,3);
                x.iterate(1);
                if (50>0)
                    x.enforceLabelConnectivity(50);
                Mat mask=new Mat();
                x.getLabelContourMask(mask,true);
                g.setTo( new Scalar(0,0,255),mask);
                Utils.matToBitmap(g,bitmap);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
               e.printStackTrace();
            }
        }
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle args=new Bundle();
        String path=null;
        if (requestCode == REQUEST_ORIGINAL_CAMERA) {
            Log.i("foto", "ciao");
            getFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment()).commit();
        }else if (requestCode == REQUEST_TAKE_PHOTO && data != null && data.getStringExtra("image")!= null) {
            Log.i("we",data.getStringExtra("image"));
            path=data.getStringExtra("image");
        }
        if(data!=null) {
            args.putString("image", path);
            Fragment elab = new ElaborazioneFragment();
            elab.setArguments(args);
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content, elab).commit();
            ///////caricamento elaborazione in corso
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_CAMERA_SETTINGS: {
                Uri uriSavedImage = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", (new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/HbMeter", "imgprofile.jpg")));
                //Uri uriSavedImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/HbMeter", "imgprofile.jpg"));
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(camera, REQUEST_ORIGINAL_CAMERA);
                return;
            }

            case REQUEST_STORAGE: {

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    ((AnalizzaFragment) prova).GetorTakePicture();
                }

                return;
            }

            case REQUEST_STORAGE_SETTINGS:{
                getFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment()).commit();
                return;
            }
            case REQUEST_STORAGE_REPORT:{
                getFragmentManager().beginTransaction().replace(R.id.content, new ReportFragment()).commit();
                return;
            }

            case PERMISSION_CAMERA:{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    if( Build.VERSION.SDK_INT<21) {
                        Intent i = new Intent(MainActivity.this, PreviewDemo.class);
                        startActivityForResult(i, REQUEST_TAKE_PHOTO);
                    }else if(Build.VERSION.SDK_INT>=21){
                        Intent i = new Intent(MainActivity.this, CameraActivity.class);
                        startActivityForResult(i,REQUEST_TAKE_PHOTO);
                    }
                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    prova = new ReportFragment();
                    manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content, prova).commit();
                    return true;

                case R.id.navigation_dashboard:
                    prova = new AnalizzaFragment();
                    manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content, prova).commit();
                    return true;

                case R.id.navigation_notifications:
                    prova = new SettingsFragment();
                    manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content, prova).commit();
                    return true;
            }
            return false;
        }

    };


   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }*/

}
