package hbmeter.hbmeter;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by Jt1995 on 05/07/2017.
 */

public class CutPhoto {
    static Bitmap bmp;
    static String name;


    static void cut(Bitmap image,String imgname) {
        bmp=image;
        name=imgname;
        int START_X = 0;
        int START_Y = (bmp.getHeight() / 3) ;
        int WIDTH_PX = bmp.getWidth();
        int HEIGHT_PX = bmp.getHeight() / 3;

        Bitmap rotatedBitmap = null;
// Crop bitmap
        try {
            Bitmap newBitmap = Bitmap.createBitmap(bmp, START_X, START_Y, WIDTH_PX, HEIGHT_PX, null, false);

            Matrix mat2 = new Matrix();

            mat2.postRotate(-90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(newBitmap, newBitmap.getWidth(), newBitmap.getHeight(), true);

            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), mat2, true);

            FileOutputStream out = new FileOutputStream(new File(name));
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);


            out.flush();
            out.close();
            out = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
