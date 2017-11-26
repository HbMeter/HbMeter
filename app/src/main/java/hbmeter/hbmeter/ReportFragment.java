package hbmeter.hbmeter;


import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_APPEND;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    ListView list;
    BottomNavigationView we;
    android.app.FragmentManager manager;
    View view;
    GraphView chart;
    int file_size;


    ArrayList<String> labeldata_array = new ArrayList<String>();
    ArrayList<String> labelorario_array = new ArrayList<String>();
    ArrayList<String> labelastar_array = new ArrayList<String>();
    ArrayList<String> labelhb_array = new ArrayList<String>();
    ArrayList<String> labelhblab_array = new ArrayList<String>();
    ArrayList<String> labelnumero_array = new ArrayList<String>();


    ArrayAdapter<String> adapter;
    ArrayList<String> listItems=new ArrayList<String>();
    final int REQUEST_STORAGE_REPORT=9;


    public ReportFragment() {
    }


    private void addFile(String data){
        String path =
                Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter";
        File folder = new File(path);
        folder.mkdirs();

        File file = new File(folder, "list.txt");

        try {

            file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.write(data);
            myOutWriter.write("\n");

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    void delete(int position) {
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


    void refresh(){

        Fragment refresh = new ReportFragment();
        manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.content, refresh).commit();
    }

    void initList(){
        ArrayList<DataPoint> points=new ArrayList<>();
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path,"/Android/data/HbMeter/list.txt");
        String ciserve=null;
        Double a_star=null;
        String hb=null;
        ImageView img=(ImageView)view.findViewById(R.id.imageView6);
        chart=(GraphView)view.findViewById(R.id.graph);
        //TextView b = (TextView)view.findViewById(R.id.textView3);
        //TextView c = (TextView)view.findViewById(R.id.textView2);

        ImageView redcircle=(ImageView)view.findViewById(R.id.cerchiorosso);
        ImageView yellowcircle=(ImageView)view.findViewById(R.id.cerchiogiallo);
        ImageView greencircle=(ImageView)view.findViewById(R.id.cerchioverde);



        if(file.exists()){
            //StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                file_size=0;
                while ((line = br.readLine()) != null) {
                    String linea1 = line.split("&")[0];
                    String orario = line.split("SELECTED")[1].split(".jpg")[0];
                    double a=Double.parseDouble(linea1.split(" ")[1]);
                    String astar=linea1.split(" ")[1];
                    String data=linea1.split(" ")[0];
                    String hblab=null;
                    points.add(new DataPoint(file_size,a));
                    //listItems.add(0, addSpace(linea));

                    labeldata_array.add(0,data);
                    labelorario_array.add(0, orario);
                    labelastar_array.add(0, astar);
                    labelhb_array.add(0, "Hb");
                    String filesize = String.valueOf(file_size);
                    if(filesize.length()==1){
                        filesize="0"+filesize;
                    }
                    labelnumero_array.add(0, filesize);


                    if(linea1.split(" ").length==4){
                        hblab = linea1.split(" ")[3];
                        hblab = hblab.split("#")[1];
                        labelhblab_array.add(0, hblab);
                    }else{
                        labelhblab_array.add(0, "---");
                    }

                    ciserve = line;
                    file_size++;
                }
                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points.toArray(new DataPoint[points.size()]));

            series.setDrawDataPoints(true);
            series.setDataPointsRadius(12);
            series.setThickness(8);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
            series.setCustomPaint(paint);
            chart.addSeries(series);


            chart.getViewport().setMinX(points.size()-10);
            chart.getViewport().setMaxX(points.size());
            chart.getViewport().setMinY(0);
            chart.getViewport().setMaxY(60);

            chart.getViewport().setYAxisBoundsManual(true);
            chart.getViewport().setXAxisBoundsManual(true);

            chart.getViewport().setScrollable(true);
            chart.getViewport().setScrollableY(true);


            //chart.getViewport().setScalable(true); // enables horizontal zooming and scrolling
            //chart.getViewport().setScalableY(true); // enables vertical zooming and scrolling

           if(ciserve!=null) {
               String imgpath = ciserve.split("&")[1];
               a_star = Double.parseDouble(ciserve.split(" ")[1]);


               Bitmap bitm = BitmapFactory.decodeFile(imgpath);
            /*Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bmrt= Bitmap.createBitmap(bitm, 0, 0, bitm.getWidth(), bitm.getHeight(), matrix, true);*/
               img.setImageBitmap(bitm);

               hb = ciserve.split(" ")[2].split("&")[0];
           }

           if(a_star!=null) {
               if (a_star > 0.0 && a_star < 20.0) {
                   redcircle.setAlpha(1f);
               } else if (a_star >= 20.0 && a_star < 40.0) {
                   yellowcircle.setAlpha(1f);
               } else if (a_star >= 40.0) {
                   greencircle.setAlpha(1f);
               }
           }

            //b.setText(a_star);
            //c.setText(hb);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_REPORT);

        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            // Inflate the layout for this fragment

            view = inflater.inflate(R.layout.fragment_report, container, false);


            double a_medio_cut;
            ImageView img = (ImageView) view.findViewById(R.id.imageView6);

            ImageView redcircle = (ImageView) view.findViewById(R.id.cerchiorosso);
            ImageView yellowcircle = (ImageView) view.findViewById(R.id.cerchiogiallo);
            ImageView greencircle = (ImageView) view.findViewById(R.id.cerchioverde);

            //TextView b = (TextView)view.findViewById(R.id.textView3);
            //TextView d = (TextView)view.findViewById(R.id.textView2);

            final Fragment dettaglio = new DettaglioFragment();

            initList();

            //getActivity().setTitle(R.string.analisi);


            list = (ListView) view.findViewById(R.id.listview);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getActivity(), String.valueOf(listItems.size()-position),Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", labeldata_array.size() - position);
                    bundle.putInt("filesize", file_size);

                    dettaglio.setArguments(bundle);

                    manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content, dettaglio).commit();
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Sei sicuro di voler eliminare questa analisi?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    delete(labeldata_array.size() - position);
                                    labeldata_array.remove(position);
                                    labelorario_array.remove(position);
                                    labelastar_array.remove(position);
                                    labelhb_array.remove(position);
                                    labelhblab_array.remove(position);
                                    labelnumero_array.remove(position);


                                    refresh();

                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder1.create();
                    alert.getWindow().setGravity(Gravity.CENTER);
                    alert.show();


                    return true;
                }
            });


            CommonBaseAdapter adapter = new CommonBaseAdapter(getActivity(), labeldata_array, labelorario_array, labelastar_array, labelhb_array, labelhblab_array, labelnumero_array);
            list.setAdapter(adapter);

        /*labeldata_array.add("24-07-2017");
        labelorario_array.add("16:32:07");
        labelastar_array.add("46.22");
        labelhb_array.add("Hb");*/


            if (getArguments() != null) {    //SI ARRIVA DOPO ELABORAZIONE
                Calendar c = Calendar.getInstance();
                Double a_medio = getArguments().getDouble("a*");

                a_medio_cut = Double.parseDouble(new DecimalFormat("##.##").format(a_medio).replace(',', '.'));

                //b.setText(String.valueOf(a_medio_cut));
                String imgpath = getArguments().getString("imgpath");
                Bitmap bitm = BitmapFactory.decodeFile(imgpath);
           /* Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bmrt= Bitmap.createBitmap(bitm, 0, 0, bitm.getWidth(), bitm.getHeight(), matrix, true);*/
                img.setImageBitmap(bitm);


                if (a_medio_cut > 0.0 && a_medio_cut < 20.0) {
                    redcircle.setAlpha(1f);
                } else if (a_medio_cut >= 20.0 && a_medio_cut < 40.0) {
                    yellowcircle.setAlpha(1f);
                } else if (a_medio_cut >= 40.0) {
                    greencircle.setAlpha(1f);
                }

                //b.setText(String.valueOf(a_medio_cut));

                //d.setText("Hb");

                String line = String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "-" + String.valueOf((c.get(Calendar.MONTH)) + 1) + "-" + String.valueOf(c.get(Calendar.YEAR)) + " " + String.valueOf(a_medio_cut) + " Hb";
                //    listItems.add("2017-23-08              60                  8");
                //listItems.add(line);

                Log.i("line", line);
                //Log.i("linespace", addSpace(line));

                labeldata_array.add(0, line.split(" ")[0]);
                labelorario_array.add(0, imgpath.split("SELECTED")[1].split(".jpg")[0]);
                labelastar_array.add(0, line.split(" ")[1]);
                labelhb_array.add(0, line.split(" ")[2]);
                //listItems.add(0, addSpace(line));


                String fileLine = line + "&" + imgpath;

                addFile(fileLine);

                refresh();

            }


            we = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
            we.setVisibility(View.VISIBLE);
            we.getMenu().getItem(0).setChecked(true);


            //adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);
            //list.setAdapter(adapter);
            getActivity().setTitle(R.string.report);


            return view;
        }
        return view;
    }
}
