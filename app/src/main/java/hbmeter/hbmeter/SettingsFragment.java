package hbmeter.hbmeter;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.Calendar;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    static final int REQUEST_ORIGINAL_CAMERA = 22;
    final int PERMISSION_CAMERA_SETTINGS=20;

    BottomNavigationView navigation;
    BottomNavigationItemView report,analizza,impostazioni;
    String mCurrentPhotoPath;
    String nome, cognome, data, sesso, incinta, cf, mail, mailMedico, mailLab, switchValue;
    int giorno, mese, anno;

    TextView nomeText, cognomeText, dataText, cfText, mailText, mailMedicoText, mailLabText;
    Switch switchImp;


    ProgressBar bar;
    RadioButton f, m;
    CheckBox c;
    ImageView omino;
    Button salva,backup;
    final int REQUEST_STORAGE_SETTINGS=5;




    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.note_legali:
                AlertDialog.Builder note = new AlertDialog.Builder(getActivity());
                note.setMessage("Note Legali HbMeter Android App\n\n" +
                        "La cartella di default per il salvataggio dei dati, quali foto, rotazioni e selezioni è: /Android/data/HbMeter\n\n"+
                        "La cartella di default per il salvataggio e la lettura del Backup è: /Android\n\n"+
                        "Al fine dell'utilizzo dell'applicazione viene richiesto l'uso della fotocamera e della memoria del dispositivo.\n");
                note.setCancelable(false);
                note.setNeutralButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                note.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    /*
 *
 * Zips a file at a location and places the resulting zip file at the toLocation
 * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
 */

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                impostazioni.setEnabled(false);
                analizza.setEnabled(false);
                report.setEnabled(false);
                salva.setEnabled(false);
                backup.setEnabled(false);
                bar.setVisibility(View.VISIBLE);
                enableDisableView(getView(),false);
            }
        });
        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableDisableView(getView(),true);
                impostazioni.setEnabled(true);
                analizza.setEnabled(true);
                report.setEnabled(true);
                salva.setEnabled(true);
                backup.setEnabled(true);
                Toast.makeText(getActivity(), "I tuoi file sono stati esportati!", Toast.LENGTH_SHORT).show();
                bar.setVisibility(View.GONE);

            }
        });
        return true;
    }


    public void unzip(String zipFile, String location) throws IOException {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar.setVisibility(View.VISIBLE);
                impostazioni.setEnabled(false);
                analizza.setEnabled(false);
                report.setEnabled(false);
                salva.setEnabled(false);
                backup.setEnabled(false);
                enableDisableView(getView(),false);
            }
        });
        final int BUFFER_SIZE = 2048;
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if ( !location.endsWith(File.separator) ) {
                location += File.separator;
            }
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableDisableView(getView(),true);
                impostazioni.setEnabled(true);
                analizza.setEnabled(true);
                report.setEnabled(true);
                salva.setEnabled(true);
                backup.setEnabled(true);
                bar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "I tuoi file sono stati importati!", Toast.LENGTH_SHORT).show();
            }
        });
    }

/*
 *
 * Zips a subfolder
 *
 */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter");
        File image = new File(
                storageDir      /* directory */
                ,"imgprofile.jpg"
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("image",mCurrentPhotoPath);
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.impostazioni);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);


        navigation=(BottomNavigationView)getActivity().findViewById(R.id.navigation);
        report = (BottomNavigationItemView) navigation.findViewById(R.id.navigation_home);
        analizza = (BottomNavigationItemView) navigation.findViewById(R.id.navigation_dashboard);
        impostazioni = (BottomNavigationItemView) navigation.findViewById(R.id.navigation_notifications);
        cfText=(TextView)view.findViewById(R.id.cf);
        nomeText=(TextView)view.findViewById(R.id.nome);
        bar=(ProgressBar)view.findViewById(R.id.bar5);
        omino=(ImageView)view.findViewById(R.id.imageView7);
        cognomeText=(TextView)view.findViewById(R.id.cognome);
        mailText=(TextView)view.findViewById(R.id.mail);
        mailMedicoText=(TextView)view.findViewById(R.id.mailmedico);
        backup=(Button)view.findViewById(R.id.button4);
        mailLabText=(TextView)view.findViewById(R.id.maillab);
        dataText=(TextView)view.findViewById(R.id.data);
        dataText.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        f=(RadioButton)view.findViewById(R.id.femmina);
        m=(RadioButton)view.findViewById(R.id.maschio);
        c=(CheckBox)view.findViewById(R.id.checkBox);
        salva = (Button)view.findViewById(R.id.button3);
        switchImp = (Switch) view.findViewById(R.id.switchImp);





        switchImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchImp.isChecked()){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Attivando questa impostazione verrà abilitata la selezione assistita della congiuntiva palpebrale," +
                            " segnando in celeste l'area che probabilmente è quella più errata." +
                            "\n\n" +
                            "Attenzione: attivando questa impostazione l'app richiederà più lavoro, e risulterà meno veloce.");
                    builder1.setCancelable(true);


                    builder1.setNeutralButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    builder1.show();
                }
            }
        });


        omino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_CAMERA_SETTINGS);
                }else{
                    Uri uriSavedImage = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", (new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/HbMeter", "imgprofile.jpg")));
                    //Uri uriSavedImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/HbMeter", "imgprofile.jpg"));
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(camera, REQUEST_ORIGINAL_CAMERA);
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_SETTINGS);

        }

        else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            readFile();
        }


        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("Scegli se Importare un backup esistente o Esportare un backup.");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Esportare",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                zipFileAtPath(Environment.getExternalStorageDirectory().toString() + "/Android/data/HbMeter", Environment.getExternalStorageDirectory().toString() + "/Android/backup.zip");
                                            }
                                        }.start();
                                    }
                                });

                        builder1.setNegativeButton(
                                "Importare",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new Thread(){
                                            @Override
                                            public void run() {
                                                if(new File(Environment.getExternalStorageDirectory().toString() + "/Android/backup.zip").exists()) {
                                                    try {
                                                        unzip(Environment.getExternalStorageDirectory().toString() + "/Android/backup.zip", Environment.getExternalStorageDirectory().toString() + "/Android/data");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }else {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                                            builder1.setMessage("Impossibile importare,file di backup non presente.");
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
                                                    });
                                                }
                                            }
                                        }.start();
                                    }
                                });
                        builder1.show();
                    }
                });
            }
        });


        salva.setOnClickListener(new View.OnClickListener() {
            @Override




            public void onClick(View v) {
                nome = nomeText.getText().toString();
                if(nome.equals("")) nome="null";

                cf = cfText.getText().toString();
                if(cf.equals("")) cf="null";

                cognome = cognomeText.getText().toString();
                if(cognome.equals("")) cognome="null";

                mail = mailText.getText().toString();
                if(mail.equals("")) mail="null";

                mailMedico = mailMedicoText.getText().toString();
                if(mailMedico.equals("")) mailMedico="null";

                mailLab = mailLabText.getText().toString();
                if(mailLab.equals("")) mailLab="null";

                data = dataText.getText().toString();
                if(data.equals("")) data="null";

                sesso="null";
                if(f.isChecked()){
                    sesso="F";
                }
                else if(m.isChecked()){
                    sesso="M";
                }
                if(c.isChecked()){
                    incinta = "Incinta";
                }
                else incinta = "null";

                switchValue = "null";
                if(switchImp.isChecked()){
                    switchValue = "TRUE";
                }




                /*giorno = Integer.valueOf(data.split("/")[0]);
                mese = Integer.valueOf(data.split("/")[1]);
                anno = Integer.valueOf(data.split("/")[2]);*/


                String tutto = nome + "\n" + cognome + "\n" +
                        /*String.valueOf(giorno) + "\n" + String.valueOf(mese) + "\n" + String.valueOf(anno) + "\n" + */
                        data + "\n" + sesso + "\n" + incinta + "\n" + cf + "\n" + mail + "\n" + mailMedico + "\n" + mailLab + "\n" + switchValue;


                if(nome.equals("null") || cognome.equals("null") || data.equals("null") || mail.equals("null") || mailMedico.equals("null") || sesso.equals("null") || cf.equals("null")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Compila tutti i campi obbligatori. (*)");
                    builder1.setCancelable(false);
                    builder1.setNeutralButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder1.show();
                }else {
                    saveFile(tutto);
                    Toast.makeText(getActivity(), "I tuoi dati sono stati salvati!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        f.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {
                    c.setVisibility(View.VISIBLE);

                }
            }
        });

        m.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {
                    c.setVisibility(View.INVISIBLE);
                    c.setChecked(false);
                }
            }
        });

        try {
            Uri uri=Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter","imgprofile.jpg"));
            Log.i("if","ciao");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
            Bitmap modified=null;
            boolean modifiedbool=false;
                if (bitmap.getHeight() > 4096 || bitmap.getWidth() > 4096) {
                    int width = bitmap.getWidth() * 65 / 100;
                    int heigth = bitmap.getHeight() * 65 / 100;
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, heigth, true);
                    modified = bitmap.copy(bitmap.getConfig(), true);
                }
            if(modifiedbool){
                bitmap=modified;
            }
                ExifInterface ei = new ExifInterface(Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter/imgprofile.jpg");
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                        rotatedBitmap=bitmap;
                        break;

                    default:
                        rotatedBitmap=bitmap;
                        break;
                }

                Log.i("ruoto","ciao");
                omino.setImageBitmap(rotatedBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                e.printStackTrace();
            }


        return view;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void saveFile(String data){
        String path =
                Environment.getExternalStorageDirectory().toString()+"/Android/data/HbMeter";
        File folder = new File(path);
        folder.mkdirs();

        File file = new File(folder, "dati.txt");

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(data);
            //myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("fra",String.valueOf(requestCode));
        Log.i("fra2",String.valueOf(REQUEST_ORIGINAL_CAMERA));
        getActivity().getFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment()).commit();

    }


    void readFile(){

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
                nomeText.setText(text.toString().split("\\n")[0]);

            if(!text.toString().split("\\n")[1].equals("null"))
                cognomeText.setText(text.toString().split("\\n")[1]);

            if(!text.toString().split("\\n")[2].equals("null"))
                dataText.setText(text.toString().split("\\n")[2]);


            sesso = text.toString().split("\\n")[3].toString();
            if (sesso.equals("M")) {
                m.setChecked(true);
            } else if (sesso.equals("F")) {
                f.setChecked(true);
                if (text.toString().split("\\n")[4].equals("Incinta")) {
                    c.setVisibility(View.VISIBLE);
                    c.setChecked(true);
                } else {
                    c.setVisibility(View.VISIBLE);
                }
            }

            if(!text.toString().split("\\n")[5].equals("null"))
                cfText.setText(text.toString().split("\\n")[5]);

            if(!text.toString().split("\\n")[6].equals("null"))
                mailText.setText(text.toString().split("\\n")[6]);

            if(!text.toString().split("\\n")[7].equals("null"))
                mailMedicoText.setText(text.toString().split("\\n")[7]);

            if(!text.toString().split("\\n")[8].equals("null"))
                mailLabText.setText(text.toString().split("\\n")[8]);

            if(!text.toString().split("\\n")[9].equals("null"))
                switchImp.setChecked(true);

        }
    }




}
