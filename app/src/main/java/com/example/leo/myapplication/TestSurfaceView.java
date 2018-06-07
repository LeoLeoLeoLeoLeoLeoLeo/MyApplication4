package com.example.leo.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    final SurfaceHolder mSurfaceHolder;
    DrawingThread mThread;

    private int width=0;
    private int height=0;
    private Bitmap b;
    private Paint p;

    private boolean zoom = false;

    private Rect src;
    private Rect dest;

    public TestSurfaceView(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread= new DrawingThread();
    }

    public TestSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread= new DrawingThread();

    }

    public TestSurfaceView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread= new DrawingThread();


    }



    @Override
    protected void onDraw(Canvas pCanvas) {


        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.BLUE);
        pCanvas.drawBitmap(b, src, dest, p);
        //pCanvas.drawRect(dest, p);
        //pCanvas.drawText(Integer.toString(getLeft()), getLeft(), getTop(), p);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        width = getRight()-getLeft();
        height = getBottom()-getTop();
        b = BitmapFactory.decodeResource(getResources(),R.drawable.distant);
        p = new Paint();

        src = new Rect(800,600,800+ width,600 +height);
        dest = new Rect(getLeft(),getTop(),getRight(),getBottom());
        mThread.keepDrawing = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


        width = getRight()-getLeft();
        height = getBottom()-getTop();
        b = BitmapFactory.decodeResource(getResources(),R.drawable.distant);
        p = new Paint();

        src = new Rect(800,600,800+ width,600 +height);
        dest = new Rect(getLeft(),getTop(),getRight(),getBottom());

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.keepDrawing = false;

        boolean joined = false;
        while (!joined){
            try {
                mThread.join();
                joined = true;
            } catch (InterruptedException ignored) {}
        }
    }


    private class DrawingThread extends Thread {

        boolean keepDrawing = true;

        @Override
        public void run() {
            while (keepDrawing) {
                Canvas canvas = null;

                try {
                    canvas = mSurfaceHolder.lockCanvas();

                    synchronized (mSurfaceHolder) {
                        onDraw(canvas);
                    }
                } finally {
                    if (canvas != null){
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}

            }
        }
    }

    public void setSrc(int left, int top, int right, int bottom){
        src.set(left, top, right, bottom);
    }

    public void initSrc(){
        src.set(800,600,800+ width,600 +height);
    }


    public Rect getSrc() {
        return src;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event){

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_POINTER_DOWN: {
                zoom = true;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                zoom = false;
            }

            case MotionEvent.ACTION_MOVE: {
                if (zoom){
                    float distanceX = event.getX(1);
                    float distanceY = event.getY(1);
                    setSrc(src.left - Math.round(distanceX), src.top - Math.round(distanceY), src.right + Math.round(distanceX), src.bottom + Math.round(distanceY));

                }
            }
        }

        return true;
    }
}
