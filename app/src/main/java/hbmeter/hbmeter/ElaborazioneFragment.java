package hbmeter.hbmeter;



import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.ximgproc.SuperpixelSLIC;
import org.opencv.ximgproc.Ximgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class ElaborazioneFragment extends Fragment {

    android.app.FragmentManager manager;

    double[] medie_superpixel;

    ArrayList<Integer[]> channels;
    Bitmap bmt;
    String imgpath, imgpathselected;
    BottomNavigationView we;
    Bitmap xd;
    ImageView g;
    Bitmap image;
    Bitmap backup_image;
    Bitmap backup_hd;
    Bitmap backup_nomask;
    ArrayList<Integer> superpixel_used = new ArrayList<Integer>();
    Button reset, analizza;
    ArrayList<float[]> prova;
    ArrayList<Double> astar;
    ArrayList<Double> Lstar;
    ArrayList<Double> bstar;

    double a_medio, L_medio, b_medio;
    //int numofsuperpixels;
    int superpixelminimo =-1;
    double mediaminima = 0;







    boolean switchvalue = false;












    View view;

    int x = 123;
    int y = 64;

    Mat imgmat, imgmat2;

    ProgressBar progbar;
    Uri originaluri;

    // settaggio parametri superpixels
    int region_size = 100;
    int ruler = 3;
    int min_element_size = 50;
    int num_iterations = 1;
    TextView text;
    TextView textsel;

    float startx;
    ProgressDialog  mProgressDialog;
    float starty;

    int cols, rows, valore, valore_touch;
    Mat newMat, newMatPass, labels;


    Superpixel[] imagelist;
    int  numofsuperpixels;


    public ElaborazioneFragment() {
        // Required empty public constructor
    }

    void preanalizza() {
        superpixel_used = new ArrayList<Integer>();
        newMat = new Mat();
        Utils.bitmapToMat(image, newMat, true);  //image trasformata in matrice
        SuperpixelSLIC slic = Ximgproc.createSuperpixelSLIC(newMat, Ximgproc.SLIC, rows / 9, (float) 25);
        slic.iterate(num_iterations);

        if (min_element_size > 0)
            slic.enforceLabelConnectivity(min_element_size);
        Mat mask = new Mat(); //creazione maschera
        slic.getLabelContourMask(mask, true);
        newMat.setTo(new Scalar(0, 0, 255), mask); //applicazione maschera
        Utils.matToBitmap(newMat, image);
        labels = new Mat();
        slic.getLabels(labels);
        numofsuperpixels = slic.getNumberOfSuperpixels();
        imagelist = new Superpixel[numofsuperpixels]; //QUI

        for (int i=0; i<imagelist.length;i++){
            imagelist[i]=new Superpixel();
        }

        imagelist[20].addcoordY(7);
        analyzeSLIC();
        ArrayList<Integer> coordX =  imagelist[7].getCoordX();
        Log.i("aaaaaaaaaa",String.valueOf(coordX.get(2)));
        backup_image = image.copy(image.getConfig(), false);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                g.setImageBitmap(image);
                mProgressDialog.cancel();
            }
        });



        //INIZIALIZZAZIONE ARRAY MEDIE SUPERPIXELS
        numofsuperpixels = slic.getNumberOfSuperpixels();
        medie_superpixel = new double[numofsuperpixels];
        for(int i=0; i<numofsuperpixels; i++){
            medie_superpixel[i]=0.0;
        }








      /*  int[] numeri=new int[(int)labels.total()*labels.channels()];
        int valore=0;
        double[] prova=new double[3];
        for (int i=1;i<=rows;i++){
            for(int j=1;j<=cols;j++){
                valore=labels.get(j,i,numeri);
                if(!(Arrays.asList(numeri).contains(valore))){
                    Arrays.asList(numeri).add(valore);
                }
            }
        }

        Mat nuova=new Mat(cols,rows,24,new Scalar(0,0,0,0));
        Mat nuova1=nuova;

        double[] prova2=new double[3];
        prova2=labels.get(x,y);
        int valore_touch=(int)prova2[0];


        double[] prova3=new double[3];
        double[] prova4=new double[3];
        for(int i=1;i<=rows;i++){
            for (int j=1;j<=cols;j++){
                prova3=labels.get(j,i);
                valore=(int)prova3[0];
                if(valore==valore_touch){
                    prova4[0]=100;
                    prova4[1]=100;
                    prova4[2]=100;
                    labels.put(j,i,prova4);

                }
            }
        }

        Mat mat_test=new Mat(cols,rows,newMat.type());
        Bitmap bit=null;
        Utils.matToBitmap(nuova1,bit);





       Bitmap mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);*/



       /*Mat mat=new Mat(labels.rows(),labels.cols(),CvType.CV_8UC1);
        double[] data={0,0,0};
        for(int i=0;i<mat.size().height;i++){
            for(int j=0;j<mat.size().width;j++){
                mat.put(i,j,data);
            }
        }

        mat.setTo(new Scalar(100),mask);*/


       /* for (int i=0;i<labels.rows();i++){
            for (int j=0;j<labels.cols();j++){
                    ris[i] =labels.get(i,j);
                    if(ris[i][0]==0.0){
                        mat.put(i,j,a);
                    }
            }
        }*/


        //final ImageView g=(ImageView)view.findViewById(R.id.imageView5);


    }




    public void write(String imagefile) {
        File directory = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter/Rgbvalues");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory,imagefile);

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            Log.i("size",String.valueOf(channels.size()));
            for(int i=0;i<channels.size();i++) {
                myOutWriter.write(String.valueOf(channels.get(i)[0]));
                myOutWriter.write("\n");
                myOutWriter.write(String.valueOf(channels.get(i)[1]));
                myOutWriter.write("\n");
                myOutWriter.write(String.valueOf(channels.get(i)[2]));
                myOutWriter.write("\n");
            }
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void analizza() {
        g.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Mat original = new Mat(); //controllare tipo e dimensioni
        Utils.bitmapToMat(backup_image, original);
        Mat matlab = new Mat();
        double[][] ris = new double[rows][cols];
        double[] pxl = new double[3];
        double[] pxlconv = new double[3];
        astar = new ArrayList<Double>();
        bstar = new ArrayList<Double>();
        Lstar = new ArrayList<Double>();
        Mat matnomask = new Mat();
        Utils.bitmapToMat(backup_nomask, matnomask);
        // Imgproc.cvtColor(original,matlab,Imgproc.COLOR_BGR2Lab,3);
        int intris;

        channels = new ArrayList<>();
        String listnamefile = imgpathselected.split(".jpg")[0].split("HbMeterPhotos/")[1];
        listnamefile+= ".txt";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                ris[0] = labels.get(j, i);
                intris = (int) ris[0][0];
                if (superpixel_used.contains(intris)) {
                    pxl = matnomask.get(j, i);
                    channels.add(new Integer[]{(int) pxl[0], (int) pxl[1], (int) pxl[2]});
                    ColorUtils.RGBToLAB((int) pxl[0], (int) pxl[1], (int) pxl[2], pxlconv);
                    //if(pxlconv[0]>20 && pxlconv[0]<70) {/////////////////////////////////
                        astar.add(pxlconv[1]);
                        bstar.add(pxlconv[2]);///////////////////////////////////////////
                        Lstar.add(pxlconv[0]);///////////////////////////////////////////
                    //}////////////////////////////////////////
                }
            }
        }

        /*int mediar=0;
        int mediag=0;
        int mediab=0;
        for(int[] chan:channels){
            mediar+=chan[0];
            mediag+=chan[1];
            mediab+=chan[2];
        }
        mediar=mediar/channels.size();
        mediag=mediag/channels.size();
        mediab=mediab/channels.size();*/
        write(listnamefile);



        a_medio = 0;
        for (int i = 0; i < astar.size(); i++) {
            a_medio += astar.get(i);
          //  Log.i("singoloA",String.valueOf(astar.get(i)));
        }
        a_medio = a_medio / astar.size();
        Log.i("misurazione del contenitore avente gli elementi presi dall'immagine per calcolare l'a*", String.valueOf(astar.size()));
        Log.i("amedio", String.valueOf(a_medio));


        /////////////////////////////////////////////////////////////////////

        b_medio = 0;
        for (int i = 0; i < bstar.size(); i++) {
            b_medio += bstar.get(i);
        }
        b_medio = b_medio / bstar.size();
        Log.i("bmedio", String.valueOf(b_medio));



        L_medio = 0;
        for (int i = 0; i < Lstar.size(); i++) {
            L_medio += Lstar.get(i);
        }
        L_medio = L_medio / Lstar.size();
        Log.i("Lmedio", String.valueOf(L_medio));
        /////////////////////////////////////////////////////////////////////////


        //  double[] xd = original.get(1500,700);
        //   double [] labpixel= new double[3];
        //   ColorUtils.RGBToLAB((int)xd[0],(int)xd[1],(int)xd[2],labpixel);

        //    Log.i("r",String.valueOf(xd[0]));
        //  Log.i("g",String.valueOf(xd[1]));
        // Log.i("b",String.valueOf(xd[2]));

        //  Log.i("L",String.valueOf(labpixel[0]));
        // Log.i("a",String.valueOf(labpixel[1]));
        // Log.i("b",String.valueOf(labpixel[2]));
        // Log.i("lenght",String.valueOf(labpixel.length));
        //Log.i("0",String.valueOf(xd[3]));


        //  Bitmap imagelab=Bitmap.createBitmap(image.getWidth(),image.getHeight(),Bitmap.Config.ARGB_4444);
        // Utils.matToBitmap(matlab,imagelab);
        // Log.i("type",String.valueOf(matlab.type()));
        //g.setImageBitmap(imagelab);
        //

        Bundle bundle = new Bundle();
        bundle.putDouble("a*", a_medio);
        bundle.putString("imgpath", imgpathselected);
        Fragment prova = new ReportFragment();
        prova.setArguments(bundle);
        manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.content, prova).commit();
    }



    public void deselect_superpixel2(float x, float y, double superpixel){

        Log.i("deselect", "entrato nella deselect");

        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        imgmat2 = new Mat(backup_image.getWidth(), backup_image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(backup_image, imgmat2);
        double[] pixel2 = new double[3];
        boolean fatto = false;
        double[] ris2 = new double[rows];

        int i=0, j=0;
        int bho1 =(int) x-cols/9;
        int bho2 =(int) y-cols/9;
        if(bho1<0){
            bho1 = 0;
        }
        if(bho2<0){
            bho2=0;
        }

        for(i=bho1; i<x + cols/9; i++) {
            for(j=bho2; j<y + cols/9; j++) {
                ris2 = labels.get(j, i);

                //Log.i("superpixel deselect", String.valueOf(superpixel));
                if(ris2!=null && ris2[0]==superpixel) {
                    //Log.i("deselect2", "if dentro deselect");
                    fatto=true;
                    //image = backup_image.copy(backup_image.getConfig(), true);
                    //g.setImageBitmap(image);
                    pixel2 = imgmat2.get(j,i);
                    //Log.i("pixel red", String.valueOf(pixel2[0]));
                    //Log.i("pixel green", String.valueOf(pixel2[1]));
                    //Log.i("pixel blu", String.valueOf(pixel2[2]));
                    imgmat.put(j,i , pixel2);
                }
            }
        }

        Utils.matToBitmap(imgmat, image);
        g.setImageBitmap(image);


        Log.i("superpixel_used prima remove", superpixel_used.toString());
        //FOR PER CAPIRE IN CHE POSIZIONE SI TROVA IL SUPERPIXEL DA RIMUOVERE, PERCHE LA REMOVE O PRENDE L'INDICIE O L'OBJECT(CHE DA ERRORE)
        if(fatto==true) {
            int cont = 0;
            for (int k = 0; k < superpixel_used.size(); k++) {
                if (superpixel_used.get(k) == (int)superpixel) {
                    cont=k;
                }
            }
            Log.i("cont", String.valueOf(cont));
            superpixel_used.remove(cont);

            ///////////////////////////RICALCOLARE IL MINIMO

            medie_superpixel[(int)superpixel] = 0.0;

            Log.i("medie_superpixel", Arrays.toString(medie_superpixel));
            //for(int i=0; i<medie_superpixel.length;)


            /////////bisogna rimuovere la media calcolata per questo superpixel e impostarla a 0 (come da inizializzazione)
            //bisogna ciclarsi il vettore delle medie, prendere la più piccola(per come sta fatto) e aggiornare le variabili,
            //e ricolorare il minimo (forse c'è bisogno di un'altra funzione)


            Log.i("superpixel_used dopo remove", superpixel_used.toString());
        }


        double mediamedie = 0.0;
        int count =0;
        double valmin=3000.0, valmax=0.0;
        int posmin = 0, posmax=0;


        if(superpixel==superpixelminimo){

                for(int n=0; n< medie_superpixel.length; n++){
                    mediamedie = mediamedie + medie_superpixel[n];
                    if(medie_superpixel[n]!=0.0)count++;

                    if(medie_superpixel[n]!=0 && medie_superpixel[n]<valmin){
                        valmin = medie_superpixel[n];
                        posmin = n;
                    }
                    if(medie_superpixel[n]!=0 && medie_superpixel[n]>valmax){
                        valmax = medie_superpixel[n];
                        posmax = n;
                    }

                }
                mediamedie = mediamedie/count;
                if(Double.isNaN(mediamedie)){
                    mediamedie = 0.0;
                }


            Log.i("posmin", String.valueOf(posmin));
            Log.i("posmax", String.valueOf(posmax));


            if(Math.abs(mediamedie-valmin)>=Math.abs(mediamedie-valmax)){

                    superpixelminimo = posmin;
                    mediaminima = valmin;

            }else{

                    superpixelminimo = posmax;
                    mediaminima = valmax;
            }


            /*if(superpixelminimo==0){
                superpixelminimo=-1;
            }*/


            Log.i("superpixelminimo", String.valueOf(superpixelminimo));
            Log.i("mediaminima", String.valueOf(mediaminima));
            Log.i("superpixelgiallo",String.valueOf(superpixelminimo));

            if(superpixel_used.size()>0)
                superpixelGiallo(superpixelminimo);

        }





        if(superpixel_used.size()==0){
            reset.setAlpha(0.3f);
            reset.setClickable(false);

            analizza.setAlpha(0.3f);
            analizza.setClickable(false);
        }
    }



    public void getMinimumSuperpixel(){

        double minimo = 3000;
        double massimo = 0;
        double media = 0;
        int posmin=0, posmax=0;
        int count=0;


        for(int i=0; i< medie_superpixel.length; i++){
            media = media + medie_superpixel[i];
            if(medie_superpixel[i]!=0.0)count++;
            if(medie_superpixel[i]!=0.0 && medie_superpixel[i]<minimo){
                minimo = medie_superpixel[i];
                posmin = i;

            }
            if(medie_superpixel[i]>massimo){
                massimo = medie_superpixel[i];
                posmax = i;
            }
        }
        media = media/count;

        /*if(Math.abs(media-minimo)>Math.abs(massimo-media)){
            mediaminima = minimo;
            superpixelminimo = posmin;
        }else{ mediaminima = massimo;
            superpixelminimo = posmax;
        }*/

        double distanza = 0.0;
        double valore = 0.0;
        int pos = 0;
        for(int i=0; i<medie_superpixel.length; i++){
            if(medie_superpixel[i]!=0.0){
               if(Math.abs(media-medie_superpixel[i])>distanza){
                   distanza = Math.abs(media-medie_superpixel[i]);
                   valore = medie_superpixel[i];
                   pos = i;
               }
            }
        }

        mediaminima = valore;
        superpixelminimo = pos;


        Log.i("media",String.valueOf(media));
        Log.i("count",String.valueOf(count));
        Log.i("massimo-media",String.valueOf(massimo-media));
        Log.i("media-minimo",String.valueOf(media-minimo));


    }


    public void deselect_superpixel(float x, float y, double superpixel){
        Log.i("deselect", "entrato nella deselect");

        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        imgmat2 = new Mat(backup_image.getWidth(), backup_image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(backup_image, imgmat2);
        double[] pixel2 = new double[3];
        boolean fatto = false;

        int i=0, j=0;
        int bho1 =(int) x-cols/9;
        int bho2 =(int) y-cols/9;
        if(bho1<0){
            bho1 = 0;
        }
        if(bho2<0){
            bho2=0;
        }

        for(i=bho1; i<x + cols/9; i++) {
            for(j=bho2; j<y + cols/9; j++) {
                double[] ris2 = labels.get(j, i);
                //Log.i("superpixel deselect", String.valueOf(superpixel));
                if(ris2!=null && ris2[0]==superpixel) {
                    //Log.i("deselect2", "if dentro deselect");
                    fatto=true;
                    //image = backup_image.copy(backup_image.getConfig(), true);
                    //g.setImageBitmap(image);
                    pixel2 = imgmat2.get(j,i);
                    //Log.i("pixel red", String.valueOf(pixel2[0]));
                    //Log.i("pixel green", String.valueOf(pixel2[1]));
                    //Log.i("pixel blu", String.valueOf(pixel2[2]));
                    imgmat.put(j,i , pixel2);
                }
            }
        }

        Utils.matToBitmap(imgmat, image);
        g.setImageBitmap(image);


        Log.i("superpixel_used prima remove", superpixel_used.toString());
        //FOR PER CAPIRE IN CHE POSIZIONE SI TROVA IL SUPERPIXEL DA RIMUOVERE, PERCHE LA REMOVE O PRENDE L'INDICIE O L'OBJECT(CHE DA ERRORE)
        if(fatto==true) {
            int cont = 0;
            for (int k = 0; k < superpixel_used.size(); k++) {
                if (superpixel_used.get(k) == (int)superpixel) {
                    cont=k;
                }
            }
            Log.i("cont", String.valueOf(cont));
            superpixel_used.remove(cont);

            //medie_superpixel[(int)superpixelDeselected] = 0.0;



            Log.i("superpixel_used dopo remove", superpixel_used.toString());
        }
        if(superpixel_used.size()==0){
            reset.setAlpha(0.3f);
            reset.setClickable(false);

            analizza.setAlpha(0.3f);
            analizza.setClickable(false);
        }
    }

    void select2(float viewX, float viewY){
        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        double[][] ris = new double[rows][cols];
        double[] ris2 = labels.get((int) viewY, (int) viewX);
        double[] pixel2 = new double[3];

        ris[0] = labels.get((int) viewY, (int) viewX);
        Log.i("aserrrrrrrrrrrrrrrrrrr",String.valueOf((int)ris[0][0]));
        ArrayList<Integer> coordX = new ArrayList<Integer>();
        ArrayList<Integer> coordY  = new ArrayList<Integer>();
        coordX = (ArrayList<Integer>) imagelist[(int)ris[0][0]].getCoordX().clone();
        coordY= (ArrayList<Integer>)imagelist[(int)ris[0][0]].getCoordY().clone();
        int intris = (int)ris[0][0];
        Log.i("provaaaaaaaaaa",String.valueOf(intris));
        for(int i=1; i<imagelist[(int)ris[0][0]].getCoordX().size();i++){
        //    Log.i("ddddddddddd",String.valueOf(imagelist[10].getCoordX().get(2)));
//            Log.i("ddddddddddeeeed",String.valueOf(imagelist[10].getCoordX().get(0)));
           //Log.i("yyyyyyyyyyyyyyyyyyyyy",String.valueOf(imagelist[intris].getCoordY().get(i)));
          //  Log.i("xxxxxxxxxxxxxxxxxxxx",String.valueOf(imagelist[(int)ris[0][0]].getCoordX().get(i)));

            //Log.i("i",String.valueOf(i));
           // Log.i("aaaaa",String.valueOf(imagelist[intris].getCoordY().size()));
            //Log.i("eeeeee",String.valueOf(imagelist[intris].getCoordX().size()));
            //Log.i("iiiii",String.valueOf(imagelist[11].getCoordY().size()));
            int test  = imagelist[intris].getCoordY().get(i);
            pixel2 = imgmat.get(imagelist[intris].getCoordY().get(i), imagelist[intris].getCoordX().get(i));
            pixel2[2] = pixel2[0] + 250;
            imgmat.put(imagelist[intris].getCoordY().get(i), imagelist[intris].getCoordX().get(i), pixel2);
            if (!superpixel_used.contains((int) ris[0][0])) {
                superpixel_used.add((int) ris[0][0]);
            }
        }


    }

    void analyzeSLIC() {

        double[][] ris = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                ris[0] = labels.get(j, i);

                if (ris != null) {
                    //aggiungo ij nel proprio superpixel(ris)
                  //  Log.i("lista",String.valueOf(imagelist.length));
                 //   Log.i("ris",String.valueOf((int)ris[0][0]+1));
                   imagelist[(int)ris[0][0]].addcoordX(i);
                 imagelist[(int)ris[0][0]].addcoordY(j);
                    if(imagelist[(int)ris[0][0]].getLabel()==-1){imagelist[(int)ris[0][0]].setLabel((int)ris[0][0]);}
                }
            }

        }
        Log.i("STOPSTOPSTOPSTOP","ciaooo");
      Log.i("ddddddddddd",String.valueOf(imagelist[10].getCoordX().get(2)));
        Log.i("ddddddddddeeeed",String.valueOf(imagelist[10].getCoordX().get(0)));

    }
    void selectSuperpixel(float viewX, float viewY) {

        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        double[][] ris = new double[rows][cols];
        double[] ris2 = labels.get((int) viewY, (int) viewX);
        double[] a = {255, 255, 255};
        double[] pixel2 = new double[3];
        int viewXpositive = (int) viewX - (cols/9);
        int viewYpositive = (int) viewY - (cols/9);
        boolean trovato = false;
        int cont = -1;
        Log.i("aserrrrrrrrrrrrrrrrrrr",String.valueOf((int)ris[0][0]));

        if(viewX>0 && viewX<rows) {
            if(viewY>0 && viewY<cols) {
                select2(viewX, viewY);
            }
        }

        //Bitmap bim = Bitmap.createBitmap((int)imgmat.size().width,(int)imgmat.size().height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgmat, image);
        g.setImageBitmap(image);
    }

 /*    void selectSuperpixel(float viewX, float viewY) {

        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        double[][] ris = new double[rows][cols];
        double[] ris2 = labels.get((int) viewY, (int) viewX);
        double[] a = {255, 255, 255};
        double[] pixel2 = new double[3];
        int viewXpositive = (int) viewX - (cols/9);
        int viewYpositive = (int) viewY - (cols/9);
        boolean trovato = false;
        int cont = -1;
        if ((viewXpositive) < 0) {
            viewXpositive = 0;
        }
        if ((viewYpositive) < 0) {
            viewYpositive = 0;
        }

        if (viewX >= 0 && viewX <= cols) {
            for (int i = viewXpositive; i < rows; i++) {
                if (cont == 0 && trovato)
                    break;
                cont = 0;
                for (int j = viewYpositive; j < cols; j++) {
                    ris[0] = labels.get(j, i);
                    //Log.i("lista",String.valueOf(cols));
                    // Log.i("prova",String.valueOf(ris[0][0]));
                    //Log.i("lista",String.valueOf(i));
                    //Log.i("ris1",Arrays.toString(ris[0]));
                    if (ris2 != null && ris[0][0] == ris2[0]) {
                        trovato = true;
                        pixel2 = imgmat.get(j, i);
                        pixel2[2] = pixel2[0] + 250;
                        imgmat.put(j, i, pixel2);
                        cont++;
                        if (!superpixel_used.contains((int) ris[0][0])) {
                            superpixel_used.add((int) ris[0][0]);
                        }
                    }
                }

            }
        }

        //Bitmap bim = Bitmap.createBitmap((int)imgmat.size().width,(int)imgmat.size().height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgmat, image);
        g.setImageBitmap(image);
    } */



    public void letturaSwitch(){

        String valore = null;
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path,"/Android/data/HbMeter/dati.txt");

        if(file.exists()){
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    valore = line;
                }

                if(valore.equals("TRUE")){
                    switchvalue = true;
                }
                else switchvalue = false;


                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

        }else switchvalue = false;
    }



    void ripristinoSuperpixel(int superpixelminimo){
        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        imgmat2 = new Mat(backup_nomask.getWidth(), backup_nomask.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(backup_image, imgmat2);
        //Utils.bitmapToMat(backup_nomask, imgmat2);

        double[] pixel2 = new double[3];
        double[][] ris = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                ris[0] = labels.get(j, i);
                //pixel2 = imgmat.get(j,i);
                if (ris[0][0]==superpixelminimo) {
                    pixel2 = imgmat2.get(j,i);
                    pixel2[2] = pixel2[0] + 250;
                    imgmat.put(j,i, pixel2);
                }
            }
        }

    }




    void superpixelGiallo(int superpixelminimo){
        Log.i("entrato giallo", "entrato giallo");

        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        imgmat2 = new Mat(backup_nomask.getWidth(), backup_nomask.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(backup_image, imgmat2);
        //Utils.bitmapToMat(backup_nomask, imgmat2);

        double[] pixel2 = new double[3];
        double[][] ris = new double[rows][cols];


            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    ris[0] = labels.get(j, i);
                    //pixel2 = imgmat.get(j,i);
                    if ((int) ris[0][0] == superpixelminimo) {
                        pixel2 = imgmat2.get(j, i);
                        pixel2[0] = 40;
                        pixel2[1] = pixel2[1] + 200;
                        pixel2[2] = pixel2[2] + 250;
                        imgmat.put(j, i, pixel2);
                    }
                }
            }


        Utils.matToBitmap(imgmat, image);
        g.setImageBitmap(image);

    }








    void selectSuperpixelFlag(float viewX, float viewY) {

        imgmat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(image, imgmat);
        imgmat2 = new Mat(backup_nomask.getWidth(), backup_nomask.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(backup_image, imgmat2);
        double[][] ris = new double[rows][cols];
        double[] ris2 = labels.get((int) viewY, (int) viewX);
        double[] a = {255, 255, 255};
        double[] pixel2 = new double[3];
        int viewXpositive = (int) viewX - (cols/9);
        int viewYpositive = (int) viewY - (cols/9);
        boolean trovato = false;

        double nonlosocazzovuoifai = 0.0;


        int cont = -1;
        if ((viewXpositive) < 0) {
            viewXpositive = 0;
        }
        if ((viewYpositive) < 0) {
            viewYpositive = 0;
        }





        if (viewX >= 0 && viewX <= cols) {


            if(ris2!=null && medie_superpixel[(int)ris2[0]]==0.0) {
                int conta = 0;
                double media = 0;
                for (int i = viewXpositive; i < rows; i++) {
                    if (cont == 0 && trovato)
                        break;
                    cont = 0;
                    for (int j = viewYpositive; j < cols; j++) {
                        ris[0] = labels.get(j, i);
                        if (ris2 != null && ris[0][0] == ris2[0]) {
                            pixel2 = imgmat.get(j, i);
                            nonlosocazzovuoifai = ris[0][0];
                            media = media + pixel2[0];
                            conta++;
                        }
                    }
                }

                media = media / conta;
                //Log.i("ris[0][0]",String.valueOf(ris[0][0]));





                double mediamedie = 0.0;
                int count =0;
                for(int i=0; i< medie_superpixel.length; i++){
                    mediamedie = mediamedie + medie_superpixel[i];
                    if(medie_superpixel[i]!=0.0)count++;
                }
                mediamedie = mediamedie/count;
                if(Double.isNaN(mediamedie)){
                    mediamedie = 0.0;
                }


                if(ris2!=null && mediamedie==0.0){
                    superpixelminimo = (int)ris2[0];
                    mediaminima = media;
                }

                if(ris2!=null) {
                    medie_superpixel[(int) ris2[0]] = media;
                }
                Log.i("media",String.valueOf(media));
                Log.i("medie_superpixel", Arrays.toString(medie_superpixel));


                /*if(media>mediaminima){
                    mediaminima = media;
                    ripristinoSuperpixel(superpixelminimo);
                    superpixelminimo = (int) ris2[0];
                }*/









                /*if(mediaminima==0){
                    mediaminima = mediamedie;
                }*/
                //Log.i("media-mediamedie",String.valueOf(Math.abs(media-mediamedie)));
                //Log.i("mediaminima-mediamedie",String.valueOf(Math.abs(mediaminima-mediamedie)));
                Log.i("media",String.valueOf(media));
                Log.i("mediamedie",String.valueOf(mediamedie));
                Log.i("mediaminima",String.valueOf(mediaminima));


                if(Math.abs(media-mediamedie)>=Math.abs(mediaminima-mediamedie)){
                    if(media!=mediaminima) {
                        ripristinoSuperpixel(superpixelminimo);////////////////la prima volta seleziona sia giallo che blu
                    }
                    //getMinimumSuperpixel();

                    superpixelminimo = (int)ris2[0];
                    mediaminima = media;
                }


            }


            Log.i("supmin",String.valueOf(superpixelminimo));
            Log.i("numof",String.valueOf(numofsuperpixels));


            boolean deselezione = false;

            for (int i = viewXpositive; i < rows; i++) {
                    if (cont == 0 && trovato)
                        break;
                    cont = 0;
                    for (int j = viewYpositive; j < cols; j++) {
                        ris[0] = labels.get(j, i);
                        //Log.i("lista",String.valueOf(cols));
                        // Log.i("prova",String.valueOf(ris[0][0]));
                        //Log.i("lista",String.valueOf(i));
                        //Log.i("ris1",Arrays.toString(ris[0]));
                        if (ris2 != null && ris[0][0] == ris2[0]) {
                            trovato = true;
                            pixel2 = imgmat.get(j, i);

                                if ((int) ris2[0] == superpixelminimo) {

                                        pixel2[0]=40;
                                        pixel2[1] = pixel2[1] + 200;
                                        pixel2[2] = pixel2[2] + 250;

                                } else {
                                    pixel2[2] = pixel2[0] + 250;
                                }

                            imgmat.put(j, i, pixel2);
                            cont++;
                            if (!superpixel_used.contains((int) ris[0][0])) {
                                superpixel_used.add((int) ris[0][0]);
                            }
                        }
                    }

                }
        }

        //Bitmap bim = Bitmap.createBitmap((int)imgmat.size().width,(int)imgmat.size().height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgmat, image);
        g.setImageBitmap(image);
    }


    @Override
    public void onStart() {
        super.onStart();

        new Thread(){
            @Override
            public void run() {
                preanalizza();
            }
        }.start();
    }

    Canvas canvasMaster;
    ArrayList<int[]> linea = new ArrayList<>();

    private void drawOnProjectedBitMap(Bitmap bm, int x, int y) {

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(8);
        canvasMaster.drawLine(startx, starty, x, y, paint);
        g.invalidate();
        g.setImageBitmap(bm);
        startx = x;
        starty = y;


    }

    public String fromInt(int val) {
        return String.valueOf(val);
    }

    public void savePhoto(Bitmap bmp) {
        Calendar c = Calendar.getInstance();
        imgpathselected = originaluri.getPath().split("_ROTATED")[0];
        String ora, minuto, secondo;
        ora = fromInt(c.get(Calendar.HOUR_OF_DAY));
        if(ora.length()==1){
            ora = "0"+ora;
        }
        minuto = fromInt(c.get(Calendar.MINUTE));
        if(minuto.length()==1){
            minuto = "0"+minuto;
        }
        secondo = fromInt(c.get(Calendar.SECOND));
        if(secondo.length()==1){
            secondo = "0"+secondo;
        }


        String add="_"+"SELECTED"+ora+":"+ minuto+":"+ secondo+".jpg";
        imgpathselected += add;
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bmrt = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        FileOutputStream out = null;
        File imageFileName = new File(imgpathselected);
        Log.i("prova", imgpathselected.toString());


        try {
            out = new FileOutputStream(imageFileName);
            bmrt.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkLowestSuperpixel(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("Preparazione dell'immagine in corso...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        });

        imgpath = getArguments().getString("image");
        text=(TextView) view.findViewById(R.id.textprg);
        textsel=(TextView) view.findViewById(R.id.textselez);
        progbar = (ProgressBar) view.findViewById(R.id.bar3);
        g = (ImageView) view.findViewById(R.id.imageView5);



        xd = view.findViewById(R.id.imageView5).getDrawingCache();


        originaluri = Uri.fromFile(new File(imgpath));
        image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), originaluri);
            int width=image.getWidth()*65/100;
            int heigth=image.getHeight()*65/100;
            image=Bitmap.createScaledBitmap(image,width,heigth,true);
            backup_nomask = image.copy(image.getConfig(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        reset = (Button) view.findViewById(R.id.button_reset);
        rows = image.getWidth();
        cols = image.getHeight();

        prova = new ArrayList<>();

        letturaSwitch();

        g.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, final MotionEvent event) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progbar.setVisibility(View.VISIBLE);
                        textsel.setVisibility(View.VISIBLE);
                    }
                });


                int action = event.getAction();
                final int index = event.getActionIndex();
                final float[] coords = new float[]{event.getX(index), event.getY(index)};
                Matrix matrix = new Matrix();
                g.getImageMatrix().invert(matrix);
                matrix.postTranslate(getView().getScrollX(), getView().getScrollY());
                matrix.mapPoints(coords);

                switch (action) {
                    case MotionEvent.ACTION_DOWN:


                        bmt = image.copy(image.getConfig(), true);
                        canvasMaster = new Canvas(bmt);
                        canvasMaster.drawBitmap(bmt, 0, 0, null);
                        startx = coords[0];
                        starty = coords[1];
                        linea.add(new int[]{(int) startx, (int) starty});
                        //Log.i("size",String.valueOf(linea.size()));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        linea.add(new int[]{(int) coords[0], (int) coords[1]});
                        drawOnProjectedBitMap(bmt, (int) coords[0], (int) coords[1]);
                        //Log.i("size",String.valueOf(linea.size()));
                        break;


                    case MotionEvent.ACTION_UP:


                        //Log.i("numero superpixel", String.valueOf(labels.get((int)coords[1], (int)coords[0])[0]));
                        Log.i("superpixel_used", superpixel_used.toString());
                        // Log.i("contains?", String.valueOf(superpixel_used.contains((int)labels.get((int)coords[1], (int)coords[0])[0])));
                        Log.i("linea size", String.valueOf(linea.size()));
                        if (linea.size() < 5 && ((labels.get((int) coords[1], (int) coords[0]))) != null && superpixel_used.contains((int) labels.get((int) coords[1], (int) coords[0])[0])) {
                            if (switchvalue == false) {
                                deselect_superpixel(coords[0], coords[1], labels.get((int) coords[1], (int) coords[0])[0]);
                            } else
                                deselect_superpixel2(coords[0], coords[1], labels.get((int) coords[1], (int) coords[0])[0]);
                        } else {


                            g.setImageBitmap(image);
                            bmt = image.copy(image.getConfig(), true);
                            canvasMaster = new Canvas(bmt);
                            canvasMaster.drawBitmap(bmt, 0, 0, null);
                            if (switchvalue == false) {
                                selectSuperpixel(startx, starty); //per solo un tap
                            } else selectSuperpixelFlag(startx, starty);
                            //Log.i("size",String.valueOf(linea.size()));
                            for (int i = 0; i < linea.size(); i++) {
                                double[] pix = labels.get((linea.get(i))[1], (linea.get(i))[0]);
                                if (superpixel_used.isEmpty()) {
                                    if (switchvalue == false) {
                                        selectSuperpixel(linea.get(i)[0], linea.get(i)[1]);
                                    } else
                                        selectSuperpixelFlag(linea.get(i)[0], linea.get(i)[1]);
                                    /*Log.i("1", String.valueOf((linea.get(i))[0]));
                                    Log.i("3", String.valueOf(labels.size()));
                                    Log.i("1", String.valueOf((linea.get(i))[1]));
                                    Log.i("sup", String.valueOf(superpixel_used.get(i)));
                                    Log.i("adc", String.valueOf(pix[0]));*/
                                }
                                Log.i("1", String.valueOf((linea.get(i))[0]));
                                Log.i("1", String.valueOf((linea.get(i))[1]));
                                if (pix != null && superpixel_used.contains((int) (pix[0])) == false) {
                                    //Log.i("i", String.valueOf(i));
                                    if (linea.get(i)[1] >= 0 && linea.get(i)[1] <= cols) {
                                        if (switchvalue == false) {
                                            selectSuperpixel(linea.get(i)[0], linea.get(i)[1]);
                                        } else
                                            selectSuperpixelFlag(linea.get(i)[0], linea.get(i)[1]);
                                    }

                                }
                            }


                        }
                        Log.i("superpixel_used", superpixel_used.toString());
                        if (linea.size() != 0 && superpixel_used.size() > 0) {
                            reset.setClickable(true);
                            reset.setAlpha(1f);
                            analizza.setClickable(true);
                            analizza.setAlpha(1f);
                        }
                        linea.clear();
                        //drawOnProjectedBitMap((ImageView)v, bmt, x, y);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progbar.setVisibility(View.INVISIBLE);
                                textsel.setVisibility(View.GONE);
                            }
                        });


                        break;

                }
    /*
     * Return 'true' to indicate that the event have been consumed.
     * If auto-generated 'false', your code can detect ACTION_DOWN only,
     * cannot detect ACTION_MOVE and ACTION_UP.
     */

                return true;
            }
        });


       /* g.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                g.setDrawingCacheEnabled(false);

                float screenX = event.getX();
                float screenY = event.getY();
                float viewX =screenX-((getActivity().getWindowManager().getDefaultDisplay().getWidth()-cols)/2);
                float cazzovuoifai=(getActivity().getWindowManager().getDefaultDisplay().getWidth())-((getActivity().getWindowManager().getDefaultDisplay().getWidth()-cols));
                float viewY = screenY;
                float final_x=(viewX*cols)/cazzovuoifai;
                float viewwidth=v.getWidth()+((getActivity().getWindowManager().getDefaultDisplay().getWidth()-cols));
                float result=cols*screenX/viewwidth;
/////////////////
                final int index = event.getActionIndex();
                final float[] coords = new float[2];
                Matrix matrix = new Matrix();
                g.getImageMatrix().invert(matrix);
                matrix.postTranslate(view.getScrollX(), view.getScrollY());
                matrix.mapPoints(coords);
                final float [] coordsmove = new float[2];
////////////////
              //  Bitmap canv = Bitmap.createBitmap(g.getWidth(),g.getHeight(),image.getConfig()); //oppure viewwidth
                Bitmap canv = view.findViewById(R.id.imageView5).getDrawingCache();
                Bitmap lol= image.copy(image.getConfig(),true);
                Canvas canvas = new Canvas(lol);
                Paint p= new Paint(Paint.ANTI_ALIAS_FLAG);
                p.setColor(Color.GREEN);
                p.setStyle(Paint.Style.STROKE);

                switch(action) {
                    case   MotionEvent.ACTION_DOWN:
                        Log.i("down","down");

                       coords[0]= event.getX();
                        coords[1]=event.getY();

                      //  selectSuperpixel(coords[0],coords[1]);












                    //prova.add(new float[]{coords[0], coords[1]});
                    /*Canvas canvas = new Canvas(image);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(viewX,viewY, 10, paint);
                    g.setImageBitmap(image);
                   // Log.i("prova",String.valueOf(prova.size()));

                    return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("move","move");
                        coordsmove[0]=event.getX();
                        coordsmove[1]=event.getY();
                      //  Bitmap test =view.findViewById(R.id.imageView5).getDrawingCache();
                        Bitmap test = image.copy(image.getConfig(),true);
                        Canvas test1 = new Canvas(test);
                        Paint test2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                        test2.setColor(Color.GREEN);
                        test2.setStyle(Paint.Style.STROKE);
                        test2.setStrokeWidth(5);
                        test1.drawLine(coords[0],coords[1],coordsmove[0],coordsmove[1],test2);
                        coords[0]= coordsmove[0];
                        coords[1]= coordsmove[1];
                        g.setImageBitmap(test);



                        return true;
                    case MotionEvent.ACTION_UP:
                        /*Log.i("UP","UP");
                        canvas.drawLine(coords[0],coords[1],coordsmove[0],coordsmove[1],p);
                        g.setImageBitmap(lol);
                        g.invalidate();
                        selectSuperpixel(coords[0],coords[1]);
                }


                return false;
            }
        });*/

        analizza = (Button) view.findViewById(R.id.button_analizza);
        analizza.setClickable(false);
        analizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(analizza.getAlpha()==1.f) {


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Sei sicuro di aver selezionato correttamente la congiuntiva palpebrale?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progbar.setVisibility(View.VISIBLE);
                                            text.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            reset.setClickable(false);
                                            reset.setAlpha(0.3f);
                                            analizza.setClickable(false);
                                            analizza.setAlpha(0.3f);
                                            Bitmap bim = image.copy(image.getConfig(), true);
                                            Utils.matToBitmap(imgmat, bim);
                                            savePhoto(bim);
                                            analizza();
                                        }
                                    }.start();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder1.show();


                }


            }





        });

        reset.setClickable(false);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Reset", "reset_clicked");
                superpixel_used.clear();
                superpixelminimo = -1;
                mediaminima = 0;

                for(int i=0; i<numofsuperpixels; i++){
                    medie_superpixel[i]=0.0;
                }

                reset.setClickable(false);
                reset.setAlpha(0.3f);
                analizza.setClickable(false);
                analizza.setAlpha(0.3f);
                image = backup_image.copy(backup_image.getConfig(), true);
                g.setImageBitmap(image);

                Log.i("Reset_medie_superpixel", Arrays.toString(medie_superpixel));
                Log.i("Reset_superpixelused", superpixel_used.toString());

            }
        });

        we = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        we.setVisibility(View.GONE);
        getActivity().setTitle(R.string.elaborazione);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_elaborazione, container, false);

        return view;
    }
}


