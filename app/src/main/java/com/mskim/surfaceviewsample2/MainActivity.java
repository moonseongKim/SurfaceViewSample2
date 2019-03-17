package com.mskim.surfaceviewsample2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity {

    private static int deviceWidth, deviceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        EventSufaceView mySurfaceView = new EventSufaceView(this);
        setContentView(mySurfaceView);

        DisplayMetrics disp = getApplicationContext().getResources().getDisplayMetrics();
        deviceWidth = disp.widthPixels;
        deviceHeight = disp.heightPixels;
    }

    public class EventSufaceView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceThread thread;

        public EventSufaceView(Context context) {
            super(context);
            init();
        }

        public EventSufaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public EventSufaceView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        private void init() {
            getHolder().addCallback(this);
            thread = new SurfaceThread(getHolder(), this);

            setFocusable(true); // make sure we get key events
        }

        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            thread.setRunning(true);
            thread.start();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            thread.setRunning(false);
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public class SurfaceThread extends Thread {

        private SurfaceHolder mThreadSurfaceHolder;
        private EventSufaceView mThreadSurfaceView;
        private boolean myThreadRun = false;
        private int x = 0;
        private int quadWidth = 100;
        private int quadHeight = 100;

        public SurfaceThread(SurfaceHolder surfaceHolder, EventSufaceView surfaceView) {
            mThreadSurfaceHolder = surfaceHolder;
            mThreadSurfaceView = surfaceView;
        }

        public void setRunning(boolean b) {
            myThreadRun = b;
        }

        @Override
        public void run() {
            while (myThreadRun) {
                Canvas c;
                c = mThreadSurfaceHolder.lockCanvas(null);
                if (c != null) {
                    try {
                        synchronized (mThreadSurfaceHolder) {
                            Paint mPaint = new Paint();
                            mPaint.setColor(Color.WHITE);
                            c.drawRect(0, 0, deviceWidth, deviceHeight, mPaint);
                            mPaint.setColor(Color.RED);

                            c.drawRect(x, x, x - quadWidth, x - quadHeight, mPaint);

                            x += 5;
                            if (x - quadWidth >= deviceWidth) {
                                x = 0;
                            }
                        }
                    } finally {
                        mThreadSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}