package hbmeter.hbmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;


/**
 * Created by Giuseppe on 16/06/2017.
 */

public class StateSplashActivity extends AppCompatActivity {

    private static final String TAG_LOG = StateSplashActivity.class.getName();

    private long mStartTime = -1L;
    private static final String START_TIME_KEY = "porting.hbmeter.key.START_TIME_KEY";
    private static final String IS_DONE_KEY = "porting.hbmeter.key.IS_DONE_KEY";


    private static final int GO_AHEAD_WHAT = 1;

    private boolean mIsDone;
    private static final long MIN_WAIT_INTERVAL = 0L;
    private static final long MAX_WAIT_INTERVAL = 700L;

    private UiHandler mHandler;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //getwindow = riferimento alla finestra contenente la schermata, getdecorview =
        //riferimento alla view che rappresenta tutto ciò che la finestra contiene.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        //setta la costante di classe view, per non far vedere i tasti di navigazione sotto.
        if(savedInstanceState != null){
            decorView.setSystemUiVisibility(uiOptions);
            this.mStartTime = savedInstanceState.getLong(START_TIME_KEY);
        }
        mHandler = new UiHandler(this);

    }

    private static class UiHandler extends Handler {

        private WeakReference<StateSplashActivity> mActivityRef;

        public UiHandler(final StateSplashActivity srcActivity){
            this.mActivityRef = new WeakReference<StateSplashActivity>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg){
            final StateSplashActivity srcActivity = this.mActivityRef.get();
            if(srcActivity == null){
                Log.d(TAG_LOG, "Reference to NoLeakSplashActivity Lost!");
                return;
            }
            switch(msg.what){
                case GO_AHEAD_WHAT:
                    long elapsedTime = SystemClock.uptimeMillis() - srcActivity.mStartTime;
                    if(elapsedTime >= MIN_WAIT_INTERVAL && !srcActivity.mIsDone){
                        srcActivity.mIsDone = true;
                        srcActivity.goAhead();
                    }
                    break;
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mStartTime == -1L){
            mStartTime = SystemClock.uptimeMillis();
        }
        final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage, mStartTime + MAX_WAIT_INTERVAL);
        Log.d(TAG_LOG, "Handler message sent!");
    }

    private void goAhead(){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DONE_KEY, mIsDone);
        outState.putLong(START_TIME_KEY, mStartTime);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        this.mIsDone = savedInstanceState.getBoolean(IS_DONE_KEY);
    }
}
