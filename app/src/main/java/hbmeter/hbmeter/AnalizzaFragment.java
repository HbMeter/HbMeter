package hbmeter.hbmeter;


import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.tooltip.Tooltip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * A simple {@link Fragment} subclass.
 */
public class AnalizzaFragment extends Fragment {

    private static final String[] TEXTS = { "Come primo passo l'utente deve montare la lente macro in corrispondenza della fotocamera posteriore del proprio cellulare.", "Come secondo passo l'utente deve scattare una foto da analizzare, o sceglierne una tra quelle già presenti, cliccando sul bottone SCEGLI/SCATTA FOTO.", "Dopo aver scelto/scattato la foto, l'utente potrà effettuare la selezione dei superpixel relativi all'area interessata, tracciando una linea o selezionando i singoli superpixel.", "Dopo aver selezionato i superpixel, cliccando sul bottone analizza l'utente potrà analizzare la parte selezionata e verificarne il risultato." };
    private static final int[] IMAGES = { R.drawable.step21, R.drawable.step22,
            R.drawable.step23, R.drawable.step24 };
    private int mPosition = 0;

    ViewPager vp;

    private TextSwitcher ts;
    private ImageSwitcher is;


    final int PERMISSION_CAMERA=3;
    BottomNavigationView we;

    final int REQUEST_TAKE_PHOTO=2;
    String mCurrentPhotoPath;
    String timeStamp;
    final int PICK_IMAGE_REQUEST = 1;
    final int REQUEST_STORAGE=4;




    public AnalizzaFragment() {
        // Required empty public constructor
    }


    public void GetorTakePicture() {


        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Scegli foto o scatta una nuova foto");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Scegli foto",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        Intent intent=new Intent(getActivity(),ListFileActivity.class);
                        startActivityForResult(intent,PICK_IMAGE_REQUEST);

                    }
                });

        builder1.setNegativeButton(
                "Scatta foto",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    PERMISSION_CAMERA);
                        }
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            if( Build.VERSION.SDK_INT<=21) {
                                Intent i = new Intent(getActivity(), PreviewDemo.class);
                                startActivityForResult(i, REQUEST_TAKE_PHOTO);
                            }else if(Build.VERSION.SDK_INT>21){
                                Intent i = new Intent(getActivity(), CameraActivity.class);
                                startActivityForResult(i,REQUEST_TAKE_PHOTO);
                            }
                        }
                    }
                } );
        ;
            builder1.show();
    }






    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view= inflater.inflate(R.layout.fragment_analizza2, container, false);

        we=(BottomNavigationView)getActivity().findViewById(R.id.navigation);
        we.setVisibility(View.VISIBLE);
        we.getMenu().getItem(1).setChecked(true);


        final ImageView x=(ImageView) view.findViewById(R.id.imageButton);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_STORAGE);

                    }

                 else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    GetorTakePicture();
                }


            }
        });

        x.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                Log.i("aaa","aa");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("aaa","aa");
                          x.setAlpha((float)0.6);
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.i("aaa","aa");
                        x.setAlpha((float)1);;
                        break;
                }
                return false;

            }
        });

        Button y =(Button)view.findViewById(R.id.button6);
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition =0;
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View view = factory.inflate(R.layout.view_pager, null);
                builder.setView(view);

                vp = (ViewPager) view.findViewById(R.id.viewPagerr);
                ImageAdapter ia = new ImageAdapter(getActivity());
                vp.setAdapter(ia);


                TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
                tabLayout.setupWithViewPager(vp);



                builder.setCancelable(true);
                builder.show();


            }
        });


        getActivity().setTitle(R.string.app_name);
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle args=new Bundle();
        String path=null;
        ProgressBar bar=(ProgressBar)getView().findViewById(R.id.bar);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getStringExtra("path")!=null) {
            bar.setVisibility(View.VISIBLE);
            try {
                Uri uri=Uri.parse(new File("file://"+data.getStringExtra("path")).toString());
                path=data.getStringExtra("path").split("\\.")[0];
                path+="_ROTATED.jpg";
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                CutPhoto.cut(bitmap, path);
           } catch (Exception e) {
                e.printStackTrace();
           }
       } else if (requestCode == REQUEST_TAKE_PHOTO && data != null && data.getStringExtra("image")!= null) {
            bar.setVisibility(View.VISIBLE);
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
}


    /*Fragment prova = new ElaborazioneFragment();
    Bundle args = new Bundle();
            args.putString("image","/storage/emulated/0/Android/data/HbmeterPhotos/642017112029_ROTATED.jpg");
                    prova.setArguments(args);
                    android.app.FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content, prova).commit();


                    Fragment fr = new ElaborazioneFragment();
                    Bundle bun = new Bundle();
                    bun.putString("image", timeStamp2);
                    fr.setArguments(bun);

                    android.app.FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content, fr).commit();

     Mat g=new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
                Utils.bitmapToMat(bitmap,g,true);
                SuperpixelSLIC x= Ximgproc.createSuperpixelSLIC(g, Ximgproc.SLIC,100,3);
                x.iterate(1);
                if (50>0)
                    x.enforceLabelConnectivity(50);
                Mat mask=new Mat();
                x.getLabelContourMask(mask,true);
                g.setTo( new Scalar(0,0,255),mask);
                Utils.matToBitmap(g,bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else
        {



                //////////
                Bitmap SOURCE_BITMAP = BitmapFactory.decodeFile(uri.getPath()); // Get the source Bitmap using your favorite method :-)
                int START_X = 0;
                int START_Y = (bitmap.getHeight()/3)*2;
                int WIDTH_PX =  bitmap.getWidth();
                int HEIGHT_PX = bitmap.getHeight()/3;

// Crop bitmap
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, START_X, START_Y, WIDTH_PX, HEIGHT_PX, null, false);


                Matrix matrix = new Matrix();

                matrix.postRotate(-90);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(newBitmap,newBitmap.getWidth(),newBitmap.getHeight(),true);

                rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

// Assign new bitmap to ImageView

                FileOutputStream out = null;
                try {
                    timeStamp2=Environment.DIRECTORY_PICTURES+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        out = new FileOutputStream(timeStamp2);
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

//////////////