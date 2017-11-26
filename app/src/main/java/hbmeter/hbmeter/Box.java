package hbmeter.hbmeter;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Giuseppe on 03/07/2017.
 */

public class Box extends View {

    private Paint paint = new Paint();
    private Paint paint2 = new Paint();
    Box(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint2.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        paint2.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint2.setStrokeWidth(5);
        paint2.setAlpha(60);

        //center

        //draw guide box

        canvas.drawRect(0, 0, getDisplay().getWidth(), getDisplay().getHeight()/3, paint2);
        canvas.drawRect(0, (getDisplay().getHeight()/3)*2, getDisplay().getWidth(), getDisplay().getHeight(), paint2);
        canvas.drawRect(0, getDisplay().getHeight()/3, getDisplay().getWidth(),(getDisplay().getHeight()/3)*2, paint);
    }
}
