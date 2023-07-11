package com.virex.giftest.ui.Html;

import static android.graphics.Paint.FILTER_BITMAP_FLAG;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class MyGifDrawable extends BaseGifDrawable implements Runnable, Animatable {

    private int mCurrentIndex = 1;

    private UpdateListener mListener;

    //private final long begin = SystemClock.uptimeMillis();
    Paint paint;
    Rect destRect;
    private volatile boolean running;

    public MyGifDrawable(InputStream source, UpdateListener listener) {
        super(source);
        this.mListener = listener;
    }

    public void setListener(UpdateListener listener){
        this.mListener = listener;
    }

    /**
     * Interface to notify listener to update/redraw
     * Can't figure out how to invalidate the drawable (or span in which it sits) itself to force redraw
     */
    public interface UpdateListener {
        void update(Drawable drawable);
    }

    /**
     * Naive method to proceed to next frame. Also notifies listener.
     */
    public void nextFrame() {
        mCurrentIndex = (mCurrentIndex + 1) % getNumberOfFrames();
        if (mCurrentIndex==0) mCurrentIndex=1; //fix
        if (mListener != null) mListener.update(this);
    }

    /**
     * Return display duration for current frame
     */
    public int getFrameDuration() {
        return getDuration(mCurrentIndex);
    }

    /**
     * Return drawable for current frame
     */
    public Drawable getDrawable() {
        return getFrame(mCurrentIndex);
    }

    private Paint getPaint() {
        if (this.paint == null) {
            this.paint = new Paint(FILTER_BITMAP_FLAG);
        }
        return this.paint;
    }

    private Rect getDestRect() {
        if (this.destRect == null) {
            this.destRect = new Rect();
        }
        return this.destRect;
    }

    @Override
    public void draw(Canvas canvas) {
        //if (!this.isRecycled) {
            /*
            if (this.applyGravity) {
                Gravity.apply(119, getIntrinsicWidth(), getIntrinsicHeight(), getBounds(), getDestRect());
                this.applyGravity = false;
            }
             */
        canvas.drawBitmap(((BitmapDrawable)getDrawable()).getBitmap(), null, getDestRect(), getPaint());
       // }
    }

    @Override
    public void start() {
        if (!isRunning()) {
            running = true;
            run();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            running = false;
            this.unscheduleSelf(this);
        }
    }

    @Override
    public boolean isRunning() {
        return running && getFrameDuration() > 0;
    }

    @Override
    public void run() {
        if (getFrameDuration() > 0) {
            this.invalidateSelf();
            //this.invalidateDrawable(getDrawable());
            //this.scheduleSelf(this, SystemClock.uptimeMillis() + rate);
            this.scheduleSelf(this, getFrameDuration());
            nextFrame();
        }
    }


    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who != null && what != null) {
            //postDelayed(what, when);
            nextFrame();
            this.scheduleSelf(this, getFrameDuration());
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who != null && what != null) {
            this.unscheduleSelf(this);
        }
    }
}
