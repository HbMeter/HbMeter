package hbmeter.hbmeter;


import android.support.v7.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class DettaglioFragment extends Fragment {

    View view;
    int posizione;
    Button prec,succ;
    TextView hblabview;
    String imgpath;
    ArrayList<Integer> listred;
    ArrayList<Integer> listgreen;
    ArrayList<Integer> listblue;
    int file_size;

    String hbemail;
    String a_medio;

    public DettaglioFragment() {
        // Required empty public constructor
    }

    void readFile(){
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path,"/Android/data/HbMeter/list.txt");
        TextView astarview = (TextView)view.findViewById(R.id.mediaastar);
        TextView hbview = (TextView)view.findViewById(R.id.hb);
        ImageView immagine = (ImageView)view.findViewById(R.id.immagine);
        hblabview = (TextView)view.findViewById(R.id.hblab);


        String astar=null;
        String hb=null;
        String hblabstring=null;

        imgpath=null;

        if(file.exists()){
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                int i=1;
                while (((line = br.readLine()) != null)&&i<posizione) {
                    i++;
                }

                if(line.split("&")[0].contains("#")){
                    hblabstring=line.split("#")[1].split("&")[0];
                    hblabview.setText("HbLab: "+hblabstring);
                }

                astar = line.split(" ")[1];
                astarview.setText("μA*: "+astar);
                a_medio=astar;

                hb = line.split(" ")[2].split("&")[0];
                hbview.setText("HbEye: "+hb);
                hbemail=hb;
                Log.i("path",line);
                imgpath = line.split("&")[1];
                Log.i("path",imgpath);
                Bitmap bitm= BitmapFactory.decodeFile(imgpath);
              /*  Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bmrt= Bitmap.createBitmap(bitm, 0, 0, bitm.getWidth(), bitm.getHeight(), matrix, true);*/
                immagine.setImageBitmap(bitm);


                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

        }
    }


    void updatefile(int position,String hb) {
        File path = Environment.getExternalStorageDirectory();
        String rigaintera = null;
        File file = new File(path, "/Android/data/HbMeter/list.txt");
        File file2 = new File(path, "/Android/data/HbMeter/list2.txt");

        if (file.exists()) {
            //StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                int i=1;
                while (((line = br.readLine()) != null)&&i<position) {
                    i++;
                }

                rigaintera = line;
                br.close();


                BufferedReader reader = new BufferedReader(new FileReader(file));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file2));

                String currentLine;

                while((currentLine = reader.readLine()) != null) {
                    if(null!=currentLine && !currentLine.equalsIgnoreCase(rigaintera)){
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }else if(currentLine!=null){
                        Log.i("ciao1",currentLine);
                        String cut=currentLine;//.split(" ")[3];
                        Log.i("ciao2",cut);
                        Log.i("ciao3",cut.split("&")[0].split(" ")[1]);
                        Log.i("hb",hb);
                        Log.i("hb",hb.split(" ")[0]);
                        if(!(cut.split("&")[0].contains("#"))) {
                            String ciao = currentLine.split("&")[0] + " #" + hb + "&" + currentLine.split("&")[1];
                            Log.i("modificato",ciao);
                            writer.write(ciao + System.getProperty("line.separator"));
                        }else{
                            String newline=currentLine.split(" #")[0]+" "+"#"+hb+"&"+currentLine.split("&")[1];
                            Log.i("ciaobo",newline);
                            writer.write(newline+System.getProperty("line.separator"));
                        }
                    }
                }
                writer.close();
                reader.close();
                boolean successful = file2.renameTo(file);


            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

        }
    }

    ArrayList<String> readsettings(){

        ArrayList<String> dati=new ArrayList<>();
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path,"/Android/data/HbMeter/dati.txt");

        if(file.exists()){
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

            Log.i("stringa", text.toString());
            if(!text.toString().split("\\n")[0].equals("null"))
                dati.add(text.toString().split("\\n")[0]);

            if(!text.toString().split("\\n")[1].equals("null"))
                dati.add(text.toString().split("\\n")[1]);

            if(!text.toString().split("\\n")[2].equals("null"))
                dati.add(text.toString().split("\\n")[2]);


            String sesso = text.toString().split("\\n")[3].toString();
            if (sesso.equals("M")) {
                dati.add("Maschio");
            } else if (sesso.equals("F")) {
                dati.add("Femmina");
                if (text.toString().split("\\n")[4].equals("Incinta")) {
                    dati.add("Incinta");
                }else dati.add("   \n");
            }

            if(!text.toString().split("\\n")[5].equals("null"))
                dati.add(text.toString().split("\\n")[5]);

            if(!text.toString().split("\\n")[6].equals("null"))
                dati.add(text.toString().split("\\n")[6]);

            if(!text.toString().split("\\n")[7].equals("null"))
                dati.add(text.toString().split("\\n")[7]);

            if(!text.toString().split("\\n")[8].equals("null"))
                dati.add(text.toString().split("\\n")[8]);

        }
        for (String item:dati){
            Log.i("array",item);
        }
        return dati;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_dettaglio, container, false);
        getActivity().setTitle(R.string.dettaglio);

        prec=(Button)view.findViewById(R.id.buttonleft);
        succ=(Button)view.findViewById(R.id.buttonright);
        prec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", posizione-1);
                bundle.putInt("filesize", file_size);
                Fragment dettaglio = new DettaglioFragment();
                dettaglio.setArguments(bundle);

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.content, dettaglio).commit();
            }
        });

        succ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", posizione+1);
                bundle.putInt("filesize", file_size);
                Fragment dettaglio = new DettaglioFragment();
                dettaglio.setArguments(bundle);

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.content, dettaglio).commit();
            }
        });

        Button hb=(Button)view.findViewById(R.id.hb1);
        hb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Inserisci il valore di emoglobina analizzato in laboratorio");

                final EditText input = new EditText(getActivity());
                input.setHint("Valore Emoglobina");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(!input.getText().toString().equals("")){
                                    updatefile(posizione, String.valueOf(input.getText().toString()));
                                    hblabview.setText("HbLab: "+input.getText().toString());

                                }else{
                                    Toast.makeText(getActivity(), "Non hai inserito nessun valore!Prova inserendone uno.", Toast.LENGTH_SHORT).show();
                                }                       }
                        });

                alertDialog.setNegativeButton("Annulla",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }

        });


        Button email=(Button)view.findViewById(R.id.email);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() == 1.0f) {
                    ArrayList<String> dati = readsettings();

                    String originalimage = imgpath.split("_SELECTED")[0];
                    originalimage += ".jpg";
                    Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.setType("text/plain");
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    uris.add(Uri.fromFile(new File(originalimage)));
                    Log.i("image", imgpath);
                    uris.add(Uri.fromFile(new File(imgpath)));
                    //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(originalimage)));
                    //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(rotatedimage)));
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Analisi Emoglobina - " + dati.get(0) + " " + dati.get(1));
                    Calendar c = Calendar.getInstance();
                    String giorno;
                    String mese;
                    String anno;

                    giorno = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                    mese = String.valueOf(((c.get(Calendar.MONTH)) + 1));
                    anno = String.valueOf((c.get(Calendar.YEAR)));
                    if(giorno.length()==1){
                        giorno= "0"+giorno;
                    }
                    if(mese.length()==1){
                        mese ="0"+mese;
                    }

                    int indice=0;
                    String text = "Analisi del giorno " + giorno + "-" + mese + "-" + anno;
                    text += "\n" + "\n" + dati.get(indice) + " " + dati.get(++indice) + "\n";
                    text += dati.get(++indice) + "\n" + dati.get(++indice) + "\n";
                    boolean incinta = false;
                    if (dati.get(indice+1).equals("Incinta")) {
                        text += dati.get(++indice) + "\n";
                        incinta = true;
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{dati.get(indice+3)});
                    } else {
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{dati.get(indice+3)});
                    }
                    text += "Codice Fiscale: "+dati.get(++indice) +"\n";
                    text += "A*: " + a_medio + "\n";
                    text += "Hb: " + hbemail + "\n";
                    if(hblabview.getText().toString().split(":").length>1) {
                        text +=hblabview.getText() + "\n";
                    }
                    text += "Email Personale: " + dati.get(++indice) +"\n";
                    try {
                        if (dati.get(indice + 2) != null) {
                            text += "Email Laboratorio: " + dati.get(indice + 2) + "\n";
                        }
                    }catch(IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    text += "\n\n"+"Inviato tramite Hb Meter Android App";
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                    //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Myimage.jpeg"));
                    startActivity(Intent.createChooser(emailIntent, "Invia analisi al Medico.."));
                }else if(v.getAlpha()==0.2f){
                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    builder1.setMessage("E' necessario compilare i campi obbligatori nelle impostazioni per inviare un'analisi.");
                    builder1.setCancelable(false);
                    builder1.setNeutralButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder1.show();
                }
            }
        });



        posizione = getArguments().getInt("position");
        file_size=getArguments().getInt("filesize");

        Log.i("posizione",String.valueOf(posizione));
        Log.i("filesize",String.valueOf(file_size));


        if(posizione==1){
            prec.setAlpha(0.2f);
            prec.setClickable(false);

        }
        if(posizione==file_size){
            succ.setAlpha(0.2f);
            succ.setClickable(false);
        }

        readFile();
        ArrayList<String> dati2=readsettings();
        boolean incinta=false;
        String qualcosa = null;
        if(dati2.size()>=6) {
            if (dati2.get(4).equals("Incinta")) {
                incinta = true;
                qualcosa = dati2.get(5);
            } else {
                qualcosa = dati2.get(4);
            }
        }
        if(qualcosa==null || qualcosa.equals("") || qualcosa.equals("null") || qualcosa.equals(null)){
            email.setAlpha(0.2f);
        }

        readlist();

       /*DataPoint[] red=new DataPoint[listred.size()];
        DataPoint[] green=new DataPoint[listgreen.size()];
        DataPoint[] blue=new DataPoint[listblue.size()];

        for(int i=0;i<listred.size();i++){
            red[i]=new DataPoint(i,listred.get(i));
            green[i]=new DataPoint(i,listgreen.get(i));
            blue[i]=new DataPoint(i,listblue.get(i));
        }*/
        LineChart graph=(LineChart)view.findViewById(R.id.istogramma);
        //graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        int[] occred=occorrenze(listred);
        int[] occgre=occorrenze(listgreen);
        int[] occblue=occorrenze(listblue);
        List<Entry> entryred=new ArrayList<Entry>();
        List<Entry> entrygreen=new ArrayList<Entry>();
        List<Entry> entryblue=new ArrayList<Entry>();
        int i=0;
        while(i<255){
            if(occred[i]!=0){
                entryred.add(new Entry(i,occred[i]));
            }
            if(occblue[i]!=0){
                entrygreen.add(new Entry(i,occgre[i]));
            }
            if(occgre[i]!=0){
                entryblue.add(new Entry(i,occblue[i]));
            }
            i++;
        }

        LineDataSet dataSetred = new LineDataSet(entryred, "Red"); // add entries to dataset
        dataSetred.setColor(Color.RED);
        dataSetred.setCircleColor(Color.RED);
        dataSetred.setFillColor(Color.RED);
        dataSetred.setDrawFilled(true);
        dataSetred.setDrawCircles(false);
        dataSetred.setFormLineWidth(1.0f);
        dataSetred.setFormSize(15.0f);
        LineDataSet dataSetgreen = new LineDataSet(entrygreen, "Green"); // add entries to dataset
        dataSetgreen.setColor(Color.GREEN);
        dataSetgreen.setCircleColor(Color.GREEN);
        dataSetgreen.setFillColor(Color.GREEN);
        dataSetgreen.setDrawFilled(true);
        dataSetgreen.setDrawCircles(false);
        dataSetred.setFormLineWidth(1.0f);
        dataSetred.setFormSize(15.0f);
        LineDataSet dataSetblue = new LineDataSet(entryblue, "Blue"); // add entries to dataset
        dataSetblue.setColor(Color.BLUE);
        dataSetblue.setCircleColor(Color.BLUE);
        dataSetblue.setFillColor(Color.BLUE);
        dataSetblue.setDrawFilled(true);
        dataSetblue.setDrawCircles(false);
        dataSetred.setFormLineWidth(1.0f);
        dataSetred.setFormSize(15.0f);
        graph.getAxisLeft().setEnabled(false);
        graph.getAxisRight().setEnabled(false);
        XAxis xAxis = graph.getXAxis();
        Description descr=new Description();
        descr.setText("");
        graph.setDescription(descr);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        /*LineData lineDatared = new LineData(dataSetred);
        graph.setData(lineDatared);*/
        LineData lineDatared=new LineData();
        lineDatared.addDataSet(dataSetgreen);
        lineDatared.addDataSet(dataSetblue);
        lineDatared.addDataSet(dataSetred);
        graph.setData(lineDatared);
        graph.invalidate();

        /*LineData lineDatagreen = new LineData(dataSetgreen);
        graph.setData(lineDatagreen);
        graph.invalidate();


        LineData lineDatablue = new LineData(dataSetblue);
        graph.setData(lineDatablue);
        graph.invalidate();*/

        /*LineGraphSeries<DataPoint> seriesred = new LineGraphSeries<>(red);
        //graph.getViewport().setMaxY(255);


        seriesred.setColor(Color.RED);
        //graph.addSeries(seriesred);

        LineGraphSeries<DataPoint> seriesgreen = new LineGraphSeries<>(green);

        seriesgreen.setColor(Color.GREEN);
        //graph.addSeries(seriesgreen);



        LineGraphSeries<DataPoint> seriesblue = new LineGraphSeries<>(blue);

        seriesblue.setColor(Color.BLUE);*/
        //graph.addSeries(seriesblue);


        return view;
    }

    int[] occorrenze(ArrayList<Integer> valori){
        int i=0;
        int[] result=new int[256];
        while(i<valori.size()){
            result[valori.get(i)]++;
            i++;
        }
        return result;
    }

    void readlist() {
        String filename = imgpath.split(".jpg")[0].split("HbMeterPhotos/")[1];
        String folder=imgpath.split("HbMeter")[0];
        folder+="HbMeter/Rgbvalues/";
        String namelist =folder+filename+".txt";
        Log.i("readist",namelist);
        listred = new ArrayList<>();
        listgreen = new ArrayList<>();
        listblue = new ArrayList<>();

        File file = new File(namelist);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null){
                    listred.add(Integer.parseInt(line));
                    line=br.readLine();
                    listgreen.add(Integer.parseInt(line));
                    line=br.readLine();
                    listblue.add(Integer.parseInt(line));
                }


                br.close();


            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

        }else{
            Log.i("nontrovato","xd");
        }

    }

}
